package eu.bbmri.eric.csit.service.negotiator.service;

import eu.bbmri.eric.csit.service.negotiator.dto.project.ProjectCreateDTO;
import eu.bbmri.eric.csit.service.negotiator.dto.project.ProjectDTO;
import eu.bbmri.eric.csit.service.negotiator.exceptions.EntityNotFoundException;
import eu.bbmri.eric.csit.service.negotiator.exceptions.EntityNotStorableException;
import java.util.List;

public interface ProjectService {

  /**
   * Creates a new project
   *
   * @param projectBody a ProjectCreateDTO with data of the project to create
   * @return a ProjectDTO of the newly created project
   * @throws EntityNotStorableException if something wrong happens creating the project
   */
  ProjectDTO create(ProjectCreateDTO projectBody) throws EntityNotStorableException;

  /**
   * Retrieves the project identified by :id
   *
   * @param id the id of the project to retrieve
   * @return a ProjectDTO with the data of the project
   * @throws EntityNotFoundException if the Project is not found
   */
  ProjectDTO findById(String id) throws EntityNotFoundException;

  /**
   * Retrieves a list of projects in the negotiator
   *
   * @return a List of ProjectDTO with the data of the projects
   * @throws EntityNotFoundException if the Project is not found
   */
  List<ProjectDTO> findAll();

}
