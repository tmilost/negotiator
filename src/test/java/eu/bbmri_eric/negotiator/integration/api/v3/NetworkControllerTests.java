package eu.bbmri_eric.negotiator.integration.api.v3;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import eu.bbmri_eric.negotiator.NegotiatorApplication;
import eu.bbmri_eric.negotiator.database.model.Network;
import eu.bbmri_eric.negotiator.database.repository.NetworkRepository;
import eu.bbmri_eric.negotiator.dto.NetworkDTO;
import java.util.Optional;

import jakarta.transaction.Transactional;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = NegotiatorApplication.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@CommonsLog
public class NetworkControllerTests {

  private static final String NETWORKS_URL = "/v3/networks";

  @Autowired private WebApplicationContext context;

  private MockMvc mockMvc;
  @Autowired private NetworkRepository networkRepository;

  @BeforeEach
  public void before() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @Test
  public void testGetAll_Networks_ok() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get(NETWORKS_URL))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/hal+json"))
        .andExpect(jsonPath("$.page.totalElements", is(3)))
        .andExpect(jsonPath("$._embedded.networks.length()", is(3)))
        .andExpect(jsonPath("$._embedded.networks.[0].id", is(1)))
        .andExpect(jsonPath("$._embedded.networks.[1].id", is(2)))
        .andExpect(jsonPath("$._embedded.networks.[2].id", is(3)));
  }

  @Test
  public void testGet_Network_ok() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get(NETWORKS_URL + "/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/hal+json"))
        .andExpect(jsonPath("$.id", is(1)));
  }

  @Test
  public void testGet_resources_ok() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get(NETWORKS_URL + "/1/resources"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/hal+json"))
        .andExpect(jsonPath("$.page.totalElements", is(3)))
        .andExpect(jsonPath("$._embedded.resources.length()", is(3)))
        .andExpect(jsonPath("$._embedded.resources.[0].id", is("4")))
        .andExpect(jsonPath("$._embedded.resources.[1].id", is("5")))
        .andExpect(jsonPath("$._embedded.resources.[2].id", is("6")));
  }

  @Test
  public void testGet_managers_ok() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get(NETWORKS_URL + "/1/managers"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/hal+json"))
        .andExpect(jsonPath("$.page.totalElements", is(2)))
        .andExpect(jsonPath("$._embedded.users.length()", is(2)))
        .andExpect(jsonPath("$._embedded.users.[0].id", is("101")))
        .andExpect(jsonPath("$._embedded.users.[1].id", is("102")));
  }

  @Test
  public void testGet_negotiations_ok() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get(NETWORKS_URL + "/1/negotiations"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/hal+json"))
        .andExpect(jsonPath("$.page.totalElements", is(4)))
        .andExpect(jsonPath("$._embedded.negotiations.length()", is(4)))
        .andExpect(jsonPath("$._embedded.negotiations.[0].id", is("negotiation-1")))
        .andExpect(jsonPath("$._embedded.negotiations.[1].id", is("negotiation-5")))
        .andExpect(jsonPath("$._embedded.negotiations.[2].id", is("negotiation-3")))
        .andExpect(jsonPath("$._embedded.negotiations.[3].id", is("negotiation-4")));
  }

  @Test
  public void testDelete_Network_ok() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.delete(NETWORKS_URL + "/1"))
        .andExpect(status().isNoContent());

    Optional<Network> network = networkRepository.findById(1L);
    assertFalse(network.isPresent());
  }

  @Test
  public void testDelete_Network_EntityNotFound() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.delete(NETWORKS_URL + "/314"))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testCreate_Network_ok() throws Exception {
    NetworkDTO networkDTO =
        NetworkDTO.builder()
            .externalId("externalId")
            .contactEmail("new@negotiator.com")
            .name("newNetwork")
            .uri("http://newuri.org")
            .build();
    String requestBody = TestUtils.jsonFromRequest(networkDTO);
    MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post(NETWORKS_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.name", is(networkDTO.getName())))
            .andExpect(jsonPath("$.externalId", is(networkDTO.getExternalId())))
            .andExpect(jsonPath("$.uri", is(networkDTO.getUri())))
            .andExpect(jsonPath("$.contactEmail", is(networkDTO.getContactEmail())))
            .andReturn();

    long id = JsonPath.parse(result.getResponse().getContentAsString()).read("$.id", Long.class);
    Optional<Network> network = networkRepository.findById(id);
    assert network.isPresent();
    assertEquals(networkDTO.getName(), network.get().getName());
  }

  @Test
  public void testUpdate_Network_ok() throws Exception {
    NetworkDTO networkDTO =
        NetworkDTO.builder()
            .id(1L)
            .externalId("externalId")
            .contactEmail("new@negotiator.com")
            .name("newNetwork")
            .uri("http://newuri.org")
            .build();
    String requestBody = TestUtils.jsonFromRequest(networkDTO);
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(NETWORKS_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.id", is(1)));

    Optional<Network> network = networkRepository.findById(1L);
    assert network.isPresent();
    assertEquals(networkDTO.getName(), network.get().getName());
  }

  @Test
  @Transactional
  public void testAddResourcesToNetwork_ok() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(NETWORKS_URL + "/1/resources")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[7,8]"))
        .andExpect(status().isOk());

    Optional<Network> network = networkRepository.findById(1L);
    assert network.isPresent();
    assertTrue(network.get().getResources().stream().anyMatch(resource -> resource.getId() == 7));
    assertTrue(network.get().getResources().stream().anyMatch(resource -> resource.getId() == 8));
  }

  @Test
  @Transactional
  public void testAddManagersToNetwork_ok() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(NETWORKS_URL + "/1/managers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[103,104]"))
        .andExpect(status().isOk());

    Optional<Network> network = networkRepository.findById(1L);
    assert network.isPresent();
    assertTrue(network.get().getManagers().stream().anyMatch(manager -> manager.getId() == 103));
    assertTrue(network.get().getManagers().stream().anyMatch(manager -> manager.getId() == 104));
  }

  @Test
  @Transactional
  public void testRemoveResourceFromNetwork_ok() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.delete(NETWORKS_URL + "/1/resources/4"))
        .andExpect(status().isNoContent());

    Optional<Network> network = networkRepository.findById(1L);
    assert network.isPresent();
    assertTrue(network.get().getResources().stream().noneMatch(resource -> resource.getId() == 4));
  }

  @Test
  @Transactional
  public void testRemoveManagerFromNetwork_ok() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.delete(NETWORKS_URL + "/1/managers/101"))
        .andExpect(status().isNoContent());

    Optional<Network> network = networkRepository.findById(1L);
    assert network.isPresent();
    assertTrue(network.get().getManagers().stream().noneMatch(manager -> manager.getId() == 101));
  }
}
