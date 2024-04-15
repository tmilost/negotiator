package eu.bbmri_eric.negotiator.service;

import eu.bbmri_eric.negotiator.dto.access_form.AccessFormElementDTO;
import eu.bbmri_eric.negotiator.dto.access_form.ElementMetaDTO;
import java.util.List;

public interface AccessFormElementService {
  List<ElementMetaDTO> getAll();

  AccessFormElementDTO getById(Long id);
}
