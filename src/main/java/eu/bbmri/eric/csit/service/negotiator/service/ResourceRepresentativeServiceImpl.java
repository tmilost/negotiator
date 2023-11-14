package eu.bbmri.eric.csit.service.negotiator.service;

import eu.bbmri.eric.csit.service.negotiator.configuration.state_machine.negotiation.NegotiationState;
import eu.bbmri.eric.csit.service.negotiator.database.model.Negotiation;
import eu.bbmri.eric.csit.service.negotiator.database.model.Person;
import eu.bbmri.eric.csit.service.negotiator.database.model.Resource;
import eu.bbmri.eric.csit.service.negotiator.database.repository.NegotiationRepository;
import eu.bbmri.eric.csit.service.negotiator.database.repository.PersonRepository;
import eu.bbmri.eric.csit.service.negotiator.database.repository.ResourceRepository;
import eu.bbmri.eric.csit.service.negotiator.dto.negotiation.NegotiationDTO;
import eu.bbmri.eric.csit.service.negotiator.exceptions.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceRepresentativeServiceImpl implements ResourceRepresentativeService {

  @Autowired ResourceRepository resourceRepository;
  @Autowired PersonRepository personRepository;
  @Autowired NegotiationRepository negotiationRepository;

  @Autowired ModelMapper modelMapper;

  @Override
  public boolean isRepresentativeOfResource(Long personId, String resourceExternalId) {
    Resource resource =
        resourceRepository
            .findBySourceId(resourceExternalId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Resource with external id " + resourceExternalId + " not found"));
    return personRepository.existsByIdAndResourcesIn(personId, Set.of(resource));
  }

  @Override
  public List<NegotiationDTO> findNegotiationsConcerningRepresentative(Long personId) {
    Person person =
        personRepository
            .findById(personId)
            .orElseThrow(
                () -> new EntityNotFoundException("Person with id " + personId + " not found"));
    List<Negotiation> negotiations =
        negotiationRepository.findByResourceExternalIdsAndCurrentState(
            person.getResources().stream().map(Resource::getSourceId).collect(Collectors.toList()),
            NegotiationState.IN_PROGRESS);
    return negotiations.stream()
        .map(negotiation -> modelMapper.map(negotiation, NegotiationDTO.class))
        .collect(Collectors.toList());
  }

  @Override
  public List<Resource> getRepresentedResourcesForUser(Long personId)
      throws EntityNotFoundException {
    return personRepository
        .findById(personId)
        .orElseThrow(() -> new EntityNotFoundException("Person with id " + personId + " not found"))
        .getResources()
        .stream()
        .toList();
  }

  @Override
  public boolean isRepresentativeAny(Long personId, List<String> resourceIds) {
    Person person =
        personRepository
            .findById(personId)
            .orElseThrow(
                () -> new EntityNotFoundException("Person with id " + personId + " not found"));
    return person.getResources().stream()
        .anyMatch(resource -> resourceIds.contains(resource.getSourceId()));
  }
}
