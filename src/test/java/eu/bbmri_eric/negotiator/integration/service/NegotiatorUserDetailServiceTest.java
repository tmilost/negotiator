package eu.bbmri_eric.negotiator.integration.service;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import eu.bbmri_eric.negotiator.user.HttpBasicUserDetails;
import eu.bbmri_eric.negotiator.user.NegotiatorUserDetailsService;
import eu.bbmri_eric.negotiator.user.Person;
import eu.bbmri_eric.negotiator.user.PersonRepository;
import eu.bbmri_eric.negotiator.util.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest(loadTestData = true)
public class NegotiatorUserDetailServiceTest {

  @Autowired NegotiatorUserDetailsService negotiatorUserDetailsService;
  @Autowired PersonRepository personRepository;

  // TODO: Fix basic auth

  @Test
  public void testIsAuthenticated_whenPasswordIsPresent() throws Exception {

    // First check that the person exist in the db but doesn't have password assigned
    Person p = personRepository.findByName("researcher").orElse(null);
    assertNotNull(p);
    assertNotNull(p.getPassword());

    assertInstanceOf(
        HttpBasicUserDetails.class, negotiatorUserDetailsService.loadUserByUsername("researcher"));
  }
}
