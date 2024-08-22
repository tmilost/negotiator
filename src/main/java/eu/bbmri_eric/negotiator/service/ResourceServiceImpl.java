package eu.bbmri_eric.negotiator.service;

import eu.bbmri_eric.negotiator.configuration.security.auth.NegotiatorUserDetailsService;
import eu.bbmri_eric.negotiator.configuration.state_machine.negotiation.NegotiationState;
import eu.bbmri_eric.negotiator.configuration.state_machine.resource.NegotiationResourceState;
import eu.bbmri_eric.negotiator.database.model.Negotiation;
import eu.bbmri_eric.negotiator.database.model.Network;
import eu.bbmri_eric.negotiator.database.model.Request;
import eu.bbmri_eric.negotiator.database.model.Resource;
import eu.bbmri_eric.negotiator.database.model.views.ResourceViewDTO;
import eu.bbmri_eric.negotiator.database.repository.NegotiationRepository;
import eu.bbmri_eric.negotiator.database.repository.NetworkRepository;
import eu.bbmri_eric.negotiator.database.repository.PersonRepository;
import eu.bbmri_eric.negotiator.database.repository.RequestRepository;
import eu.bbmri_eric.negotiator.database.repository.ResourceRepository;
import eu.bbmri_eric.negotiator.dto.person.ResourceResponseModel;
import eu.bbmri_eric.negotiator.dto.resource.ResourceWithStatusDTO;
import eu.bbmri_eric.negotiator.events.NewResourcesAddedEvent;
import eu.bbmri_eric.negotiator.exceptions.EntityNotFoundException;
import eu.bbmri_eric.negotiator.exceptions.ForbiddenRequestException;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import lombok.extern.apachecommons.CommonsLog;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@CommonsLog
public class ResourceServiceImpl implements ResourceService {

  private final NetworkRepository networkRepository;
  private final ResourceRepository repository;
  private final PersonRepository personRepository;
  private final NegotiationRepository negotiationRepository;
  private final ModelMapper modelMapper;
  private final RequestRepository requestRepository;
  private final UserNotificationServiceImpl userNotificationServiceImpl;
  private final ApplicationEventPublisher applicationEventPublisher;

  public ResourceServiceImpl(
      NetworkRepository networkRepository,
      ResourceRepository repository,
      PersonRepository personRepository,
      NegotiationRepository negotiationRepository,
      ModelMapper modelMapper,
      RequestRepository requestRepository,
      UserNotificationServiceImpl userNotificationServiceImpl,
      ApplicationEventPublisher applicationEventPublisher) {
    this.networkRepository = networkRepository;
    this.repository = repository;
    this.personRepository = personRepository;
    this.negotiationRepository = negotiationRepository;
    this.modelMapper = modelMapper;
    this.requestRepository = requestRepository;
    this.userNotificationServiceImpl = userNotificationServiceImpl;
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Override
  public ResourceResponseModel findById(Long id) {
    return modelMapper.map(
        repository.findById(id).orElseThrow(() -> new EntityNotFoundException(id)),
        ResourceResponseModel.class);
  }

  @Override
  public Iterable<ResourceResponseModel> findAll(Pageable pageable) {
    return repository
        .findAll(pageable)
        .map(resource -> modelMapper.map(resource, ResourceResponseModel.class));
  }

  @Override
  public Iterable<ResourceResponseModel> findAllForNetwork(Pageable pageable, Long networkId) {

    Network network =
        networkRepository
            .findById(networkId)
            .orElseThrow(() -> new EntityNotFoundException(networkId));
    return repository
        .findAllByNetworksContains(network, pageable)
        .map(resource -> modelMapper.map(resource, ResourceResponseModel.class));
  }

  @Override
  public List<ResourceWithStatusDTO> findAllInNegotiation(String negotiationId) {
    if (!negotiationRepository.existsById(negotiationId)) {
      throw new EntityNotFoundException(negotiationId);
    }
    Long userId = NegotiatorUserDetailsService.getCurrentlyAuthenticatedUserInternalId();
    if (userIsntAuthorized(negotiationId, userId)
        && !NegotiatorUserDetailsService.isCurrentlyAuthenticatedUserAdmin()) {
      throw new ForbiddenRequestException("You do not have permission to access this resource");
    }
    List<ResourceViewDTO> resourceViewDTOS = repository.findByNegotiation(negotiationId);
    return resourceViewDTOS.stream()
        .map(resourceViewDTO -> modelMapper.map(resourceViewDTO, ResourceWithStatusDTO.class))
        .toList();
  }

  @Override
  @Transactional
  public List<ResourceWithStatusDTO> addResourcesToNegotiation(
      String negotiationId, List<Long> resourceIds) {
    assignNewResources(negotiationId, resourceIds);
    return getResourceWithStatusDTOS(negotiationId);
  }

  private void assignNewResources(String negotiationId, List<Long> resourceIds) {
    Request request = getRequest(negotiationId);
    Negotiation negotiation = getNegotiation(negotiationId);
    Set<Resource> resources = getResources(negotiationId, resourceIds, negotiation);
    initializeStateForNewResources(negotiation, resources);
    persistChanges(negotiation, resources, request);
  }

  private @NonNull List<ResourceWithStatusDTO> getResourceWithStatusDTOS(String negotiationId) {
    List<ResourceViewDTO> resourceViewDTOS = repository.findByNegotiation(negotiationId);
    log.debug(
        "Negotiation %s now has %s resources after modification"
            .formatted(negotiationId, resourceViewDTOS.size()));
    return resourceViewDTOS.stream()
        .map(resourceViewDTO -> modelMapper.map(resourceViewDTO, ResourceWithStatusDTO.class))
        .toList();
  }

  private void persistChanges(Negotiation negotiation, Set<Resource> resources, Request request) {
    negotiationRepository.saveAndFlush(negotiation);
    resources.addAll(negotiation.getResources());
    request.setResources(resources);
    requestRepository.saveAndFlush(request);
    if (negotiation.getCurrentState().equals(NegotiationState.IN_PROGRESS)) {
      applicationEventPublisher.publishEvent(new NewResourcesAddedEvent(this, negotiation));
    }
  }

  private static void initializeStateForNewResources(
      Negotiation negotiation, Set<Resource> resources) {
    if (negotiation.getCurrentState().equals(NegotiationState.IN_PROGRESS)) {
      for (Resource resource : resources) {
        negotiation.setStateForResource(resource.getSourceId(), NegotiationResourceState.SUBMITTED);
      }
      log.debug(negotiation.getCurrentStatePerResource().keySet().size());
    }
  }

  private @NonNull Set<Resource> getResources(
      String negotiationId, List<Long> resourceIds, Negotiation negotiation) {
    log.debug(
        "Negotiation %s has %s resources before modification"
            .formatted(negotiationId, negotiation.getResources().size()));
    Set<Resource> resources = new HashSet<>(repository.findAllById(resourceIds));
    log.debug(resources.size());
    resources.removeAll(negotiation.getResources());
    log.debug(
        "Request is to add %s new resources to negotiation %s"
            .formatted(resources.size(), negotiationId));
    return resources;
  }

  private Negotiation getNegotiation(String negotiationId) {
    Negotiation negotiation =
        negotiationRepository
            .findById(negotiationId)
            .orElseThrow(() -> new EntityNotFoundException(negotiationId));
    return negotiation;
  }

  private Request getRequest(String negotiationId) {
    Request request =
        requestRepository
            .findByNegotiation_Id(negotiationId)
            .orElseThrow(() -> new EntityNotFoundException(negotiationId));
    return request;
  }

  private boolean userIsntAuthorized(String negotiationId, Long userId) {
    return !personRepository.isRepresentativeOfAnyResourceOfNegotiation(userId, negotiationId)
        && !negotiationRepository.existsByIdAndCreatedBy_Id(negotiationId, userId);
  }
}
