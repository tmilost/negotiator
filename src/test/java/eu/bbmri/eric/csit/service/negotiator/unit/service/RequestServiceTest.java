package eu.bbmri.eric.csit.service.negotiator.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.bbmri.eric.csit.service.negotiator.database.model.DataSource;
import eu.bbmri.eric.csit.service.negotiator.database.model.Request;
import eu.bbmri.eric.csit.service.negotiator.database.model.Resource;
import eu.bbmri.eric.csit.service.negotiator.database.repository.DataSourceRepository;
import eu.bbmri.eric.csit.service.negotiator.database.repository.RequestRepository;
import eu.bbmri.eric.csit.service.negotiator.database.repository.ResourceRepository;
import eu.bbmri.eric.csit.service.negotiator.dto.request.RequestCreateDTO;
import eu.bbmri.eric.csit.service.negotiator.dto.request.RequestDTO;
import eu.bbmri.eric.csit.service.negotiator.dto.request.ResourceDTO;
import eu.bbmri.eric.csit.service.negotiator.mappers.RequestModelsMapper;
import eu.bbmri.eric.csit.service.negotiator.mappers.ResourceModelMapper;
import eu.bbmri.eric.csit.service.negotiator.service.RequestService;
import eu.bbmri.eric.csit.service.negotiator.service.RequestServiceImpl;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;

@CommonsLog
public class RequestServiceTest {

  @Mock RequestRepository requestRepository;

  @Mock ResourceRepository resourceRepository;

  @Mock DataSourceRepository dataSourceRepository;

  @Spy ModelMapper modelMapper = new ModelMapper();
  @InjectMocks RequestService requestService = new RequestServiceImpl();

  @InjectMocks RequestModelsMapper requestModelsMapper;
  @InjectMocks ResourceModelMapper resourceModelMapper;

  private AutoCloseable closeable;

  @BeforeEach
  void before() {
    closeable = MockitoAnnotations.openMocks(this);
    resourceModelMapper.addMappings();
    requestModelsMapper.addMappings();
  }

  @Test
  void getAll_ReturnsAll() {
    when(requestRepository.findAll()).thenReturn(List.of(new Request()));
    assertEquals(1, requestService.findAll().size());
    when(requestRepository.findAll()).thenReturn(List.of(new Request(), new Request()));
    assertEquals(2, requestService.findAll().size());
  }

  @Test
  void getById_Ok() {
    Request request = new Request();
    request.setId("newRequest");
    when(requestRepository.findDetailedById("newRequest")).thenReturn(Optional.of(request));
    assertEquals(request.getId(), requestService.findById(request.getId()).getId());
  }

  private static RequestCreateDTO createSimpleRequestDTO(ResourceDTO resourceDTO) {
    RequestCreateDTO requestCreateDTO =
        RequestCreateDTO.builder()
            .url("https://directory.com")
            .humanReadable("I want everything")
            .resources(Set.of(resourceDTO))
            .build();
    return requestCreateDTO;
  }

  private static ResourceDTO createSimpleResourceDTO() {
    ResourceDTO resourceDTO =
        ResourceDTO.builder().id("test:collection").name("My collection").build();
    return resourceDTO;
  }

  @Test
  void create_validParameters_Ok() {
    ResourceDTO resourceDTO = createSimpleResourceDTO();
    RequestCreateDTO requestCreateDTO = createSimpleRequestDTO(resourceDTO);
    Request requestToBeSaved = modelMapper.map(requestCreateDTO, Request.class);
    Resource resourceToBeSaved = modelMapper.map(resourceDTO, Resource.class);
    when(resourceRepository.findBySourceId(resourceDTO.getId()))
        .thenReturn(Optional.of(resourceToBeSaved));
    when(dataSourceRepository.findByUrl(any())).thenReturn(Optional.of(new DataSource()));
    when(requestRepository.save(any())).thenReturn(requestToBeSaved);
    RequestDTO savedRequest = requestService.create(requestCreateDTO);
    assertEquals(requestCreateDTO.getHumanReadable(), savedRequest.getHumanReadable());
    assertEquals(resourceDTO.getId(), savedRequest.getResources().iterator().next().getId());
  }

  @Test
  void update_validParameters_Ok() {
    create_validParameters_Ok();
    //    RequestCreateDTO updatedRequestCreateDTO = createSimpleRequestDTO(resourceDTO);
    //    Request updatedSavedRequest = modelMapper.map(updatedRequestCreateDTO, Request.class);
    //
    // when(requestRepository.findById("SavedRequest")).thenReturn(Optional.of(requestToBeSaved));
    //    when(requestRepository.save(requestToBeSaved)).thenReturn(updatedSavedRequest);
    RequestDTO updatedRequest =
        requestService.update("SavedRequest", createSimpleRequestDTO(createSimpleResourceDTO()));
    assertEquals("Now I want nothing!", updatedRequest.getHumanReadable());
  }
}
