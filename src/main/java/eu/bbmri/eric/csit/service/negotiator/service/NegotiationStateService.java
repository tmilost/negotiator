package eu.bbmri.eric.csit.service.negotiator.service;

import eu.bbmri.eric.csit.service.negotiator.database.model.NegotiationEvent;
import eu.bbmri.eric.csit.service.negotiator.database.model.NegotiationState;
import eu.bbmri.eric.csit.service.negotiator.exceptions.EntityNotFoundException;
import eu.bbmri.eric.csit.service.negotiator.exceptions.WrongRequestException;
import java.util.NoSuchElementException;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public interface NegotiationStateService {

  /**
   * Initializes the state machine for the first time
   *
   * @param negotiationId for which the state machine is created
   */

  void initializeTheStateMachine(String negotiationId);

  /**
   * Initializes the state machine for the first time for a specific resource in a Negotiation
   *
   * @param negotiationId for which the state machine is created
   * @param resourceId for which the state machine is created
   */

  void initializeTheStateMachine(String negotiationId, String resourceId);

  /**
   * Returns the current state of a Negotiation
   *
   * @param negotiationId for which state is requested
   * @return NegotiationState
   */
  NegotiationState getCurrentState(String negotiationId) throws EntityNotFoundException;

  /**
   * Returns the current state of a Resource Negotiation
   *
   * @param negotiationId for which state is requested
   * @param resourceId for which state is requested
   * @return Current state
   */
  NegotiationState getCurrentState(String negotiationId, String resourceId)
      throws EntityNotFoundException;

  /**
   * Returns all possible events that can be sent for this negotiation
   *
   * @param negotiationId of the Negotiation
   * @return a lists of all possible events
   */

  Set<NegotiationEvent> getPossibleEvents(String negotiationId) throws EntityNotFoundException;


  /**
   * Returns all possible events that can be sent for this resource negotiation
   *
   * @param negotiationId that is of interest
   * @param resourceId that is of interest
   * @return a set of all possible events
   */
  Set<NegotiationEvent> getPossibleEvents(String negotiationId, String resourceId)
      throws EntityNotFoundException;

  /**
   * Send an event to a particular negotiation
   *
   * @return the new status of the Negotiation
   */
  NegotiationState sendEvent(String negotiationId, NegotiationEvent negotiationEvent)
      throws WrongRequestException, EntityNotFoundException;

  /**
   * Send an event to a particular resource negotiation
   *
   * @return The new state
   * @throws NoSuchElementException In case the combination of Negotiation and Resource was not
   * found
   */
  NegotiationState sendEvent(String negotiationId, String resourceId,
      NegotiationEvent negotiationEvent) throws WrongRequestException, EntityNotFoundException;
}
