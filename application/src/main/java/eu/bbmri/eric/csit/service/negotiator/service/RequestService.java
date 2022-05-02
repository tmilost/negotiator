package eu.bbmri.eric.csit.service.negotiator.service;

import eu.bbmri.eric.csit.service.model.Project;
import eu.bbmri.eric.csit.service.model.Query;
import eu.bbmri.eric.csit.service.model.Request;
import eu.bbmri.eric.csit.service.negotiator.dto.request.RequestRequest;
import eu.bbmri.eric.csit.service.negotiator.exceptions.EntityNotFoundException;
import eu.bbmri.eric.csit.service.negotiator.exceptions.EntityNotStorableException;
import eu.bbmri.eric.csit.service.negotiator.exceptions.WrongRequestException;
import eu.bbmri.eric.csit.service.repository.RequestRepository;
import java.util.HashSet;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class RequestService {

  @Autowired private RequestRepository requestRepository;
  @Autowired private ProjectService projectService;
  @Autowired private QueryService queryService;
  @Autowired private ModelMapper modelMapper;

  private List<Query> findQueries(List<Long> queriesId) {
    List<Query> queries;
    try {
      queries = queryService.findAllById(queriesId);
    } catch (EntityNotFoundException ex) {
      throw new WrongRequestException("One or more of the specified queries do not exist");
    }
    return queries;
  }

  private Request create(Project project, RequestRequest request) {
    List<Query> queries = findQueries(request.getQueries());

    final Request requestEntity = modelMapper.map(request, Request.class);
    requestEntity.setProject(project);

    try {
      requestRepository.save(requestEntity);
      queries.forEach(query -> {
        query.setRequest(requestEntity);
        queryService.update(query);
      });
      requestEntity.setQueries(new HashSet<>(queries));
      return requestEntity;
    } catch (DataIntegrityViolationException ex) {
      throw new EntityNotStorableException();
    }
  }

  public Request create(Long projectId, RequestRequest request) {
    Project project = projectService.findById(projectId);
    return create(project, request);
  }

  public Request create(RequestRequest request) {
    if (request.getProject() == null) {
      throw new WrongRequestException();
    }
    Project project = projectService.create(request.getProject());
    return create(project, request);
  }

  public Request update(Long id, RequestRequest request) {
    final Request requestEntity = findById(id);
    requestEntity.setTitle(request.getTitle());
    requestEntity.setDescription(request.getDescription());

    List<Query> queries = findQueries(request.getQueries());
    try {
      requestRepository.save(requestEntity);
      queries.forEach(query -> {
        query.setRequest(requestEntity);
        queryService.update(query);
      });
      requestEntity.setQueries(new HashSet<>(queries));
      return requestEntity;
    } catch (DataIntegrityViolationException ex) {
      throw new EntityNotStorableException();
    }
  }

  public List<Request> findAll() {
    return requestRepository.findAll();
  }

  public Request findById(Long id) {
    return requestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));
  }
}
