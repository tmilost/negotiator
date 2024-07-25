package eu.bbmri_eric.negotiator.integration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.bbmri_eric.negotiator.configuration.state_machine.resource.NegotiationResourceEvent;
import eu.bbmri_eric.negotiator.database.model.AccessForm;
import eu.bbmri_eric.negotiator.database.repository.AccessFormRepository;
import eu.bbmri_eric.negotiator.database.repository.InformationRequirementRepository;
import eu.bbmri_eric.negotiator.dto.InformationRequirementCreateDTO;
import eu.bbmri_eric.negotiator.dto.InformationRequirementDTO;
import eu.bbmri_eric.negotiator.service.InformationRequirementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class InformationRequirementServiceTest {
  @Autowired private InformationRequirementService service;
  @Autowired private AccessFormRepository accessFormRepository;
  @Autowired private InformationRequirementRepository informationRequirementRepository;

  @Test
  void createInformationRequirement_passedWrongParameters_throwsNullPointerException() {
    assertThrows(NullPointerException.class, () -> service.createInformationRequirement(null));
    assertThrows(
        NullPointerException.class,
        () -> service.createInformationRequirement(new InformationRequirementCreateDTO()));
  }

  @Test
  void createInformationRequirement_correctParameters_saved() {
    assertNotNull(
        service.createInformationRequirement(
            new InformationRequirementCreateDTO(1L, NegotiationResourceEvent.CONTACT)));
  }

  @Test
  void updateInformationRequirement_newParameters_ok() {
    AccessForm accessForm = accessFormRepository.save(new AccessForm("test2"));
    InformationRequirementCreateDTO createDTO =
        new InformationRequirementCreateDTO(1L, NegotiationResourceEvent.CONTACT);
    InformationRequirementDTO savedDTO = service.createInformationRequirement(createDTO);
    createDTO.setRequiredAccessFormId(accessForm.getId());
    createDTO.setForResourceState(NegotiationResourceEvent.CONTACT);
    savedDTO = service.updateInformationRequirement(createDTO, savedDTO.getId());
    assertEquals(accessForm.getId(), savedDTO.getRequiredAccessForm().getId());
    assertEquals(NegotiationResourceEvent.CONTACT, savedDTO.getForResourceEvent());
  }

  @Test
  void findAlL_1saved_ok() {
    informationRequirementRepository.deleteAll();
    assertEquals(0, informationRequirementRepository.findAll().size());
    assertNotNull(
        service.createInformationRequirement(
            new InformationRequirementCreateDTO(1L, NegotiationResourceEvent.CONTACT)));
    assertEquals(1, service.getAllInformationRequirements().size());
  }
}
