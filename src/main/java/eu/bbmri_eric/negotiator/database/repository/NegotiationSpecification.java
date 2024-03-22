package eu.bbmri_eric.negotiator.database.repository;

import eu.bbmri_eric.negotiator.api.controller.v3.NegotiationRole;
import eu.bbmri_eric.negotiator.configuration.state_machine.negotiation.NegotiationState;
import eu.bbmri_eric.negotiator.database.model.Negotiation;
import eu.bbmri_eric.negotiator.database.model.Person;
import eu.bbmri_eric.negotiator.database.model.Resource;
import eu.bbmri_eric.negotiator.dto.negotiation.NegotiationFilters;
import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

/** Class to create query condition for NegotiationRepository. The conditions can be */
public class NegotiationSpecification {

  public static Specification<Negotiation> fromNegatiationFilters(
      NegotiationFilters requestParameters, Person user) {

    Specification<Negotiation> specs = null;
    if (user != null) {
      if (requestParameters.getRole() == null) {
        specs = initOrAnd(specs, byAuthorOrRepresentative(user));
      } else if (requestParameters.getRole() == NegotiationRole.AUTHOR) {
        specs = initOrAnd(specs, hasAuthor(user));
      } else {
        specs = initOrAnd(specs, hasResourcesIn(user.getResources()));
      }
    }

    if (requestParameters.getStatus() != null && !requestParameters.getStatus().isEmpty()) {
      specs = initOrAnd(specs, hasState(requestParameters.getStatus()));
    }

    if (requestParameters.getCreatedAfter() != null
        || requestParameters.getCreatedBefore() != null) {
      specs =
          initOrAnd(
              specs,
              createdBetween(
                  requestParameters.getCreatedAfter(), requestParameters.getCreatedBefore()));
    }

    return specs;
  }

  private static Specification<Negotiation> initOrAnd(
      Specification<Negotiation> overallSpec, Specification<Negotiation> newSpec) {
    if (overallSpec == null) {
      return newSpec;
    } else {
      return overallSpec.and(newSpec);
    }
  }

  /**
   * Condition to filter Negotiation by states
   *
   * @param states a List of NegotiationState to use as filter
   * @return a Specification to add as part of a query to filter Negotiations
   */
  public static Specification<Negotiation> hasState(List<NegotiationState> states) {
    return new Specification<>() {
      List<NegotiationState> inputStates;

      @Nullable
      @Override
      public Predicate toPredicate(
          @Nonnull Root<Negotiation> root,
          @Nonnull CriteriaQuery<?> query,
          @Nonnull CriteriaBuilder criteriaBuilder) {
        inputStates = states;
        if (states.size() == 1) {
          return criteriaBuilder.equal(root.get("currentState"), states.get(0));
        } else {
          return criteriaBuilder.in(root.get("currentState")).value(states);
        }
      }

      @Override
      public String toString() {
        return inputStates.toString();
      }
    };
  }

  /**
   * Condition to filter Negotiation by author (i.e., createdBy) or resourceIn
   *
   * @param person the Person that created the negotiation or is representative of some resource
   * @return a Specification to add as part of a query to filter Negotiations
   */
  public static Specification<Negotiation> byAuthorOrRepresentative(Person person) {
    return new Specification<>() {
      @Nullable
      @Override
      public Predicate toPredicate(
          @Nonnull Root<Negotiation> root,
          @Nonnull CriteriaQuery<?> query,
          @Nonnull CriteriaBuilder criteriaBuilder) {
        Predicate authorPredicate = criteriaBuilder.equal(root.get("createdBy"), person);
        if (person.getResources() != null && !person.getResources().isEmpty()) {
          Predicate involvedInResources =
              root.joinSet("requests").joinSet("resources").in(person.getResources());
          return criteriaBuilder.or(authorPredicate, involvedInResources);
        }
        return authorPredicate;
      }
    };
  }

  /**
   * Condition to filter Negotiation by author (i.e., createdBy)
   *
   * @param person the Person that created the negotiation
   * @return a Specification to add as part of a query to filter Negotiations
   */
  public static Specification<Negotiation> hasAuthor(Person person) {
    return new Specification<>() {
      @Nullable
      @Override
      public Predicate toPredicate(
          @Nonnull Root<Negotiation> root,
          @Nonnull CriteriaQuery<?> query,
          @Nonnull CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.equal(root.get("createdBy"), person);
      }
    };
  }

  /**
   * Condition to filter Negotiation by the Resources involved
   *
   * @param resources the Person that created the negotiation
   * @return a Specification to add as part of a query to filter Negotiations
   */
  public static Specification<Negotiation> hasResourcesIn(Set<Resource> resources) {
    return new Specification<>() {
      @Nullable
      @Override
      public Predicate toPredicate(
          @Nonnull Root<Negotiation> root,
          @Nonnull CriteriaQuery<?> query,
          @Nonnull CriteriaBuilder criteriaBuilder) {
        return root.joinSet("requests").joinSet("resources").in(resources);
      }
    };
  }

  /**
   * Condition to filter by creationDate. Both lower and upper bound date may be avoided
   *
   * @param after the LocalDate after which the Negotiation was created
   * @param before the LocalDate before which the Negotiation was created
   * @return a Specification to add as part of a query to filter Negotiations
   */
  public static Specification<Negotiation> createdBetween(LocalDate after, LocalDate before) {
    return new Specification<>() {
      @Nullable
      @Override
      public Predicate toPredicate(
          @Nonnull Root<Negotiation> root,
          @Nonnull CriteriaQuery<?> query,
          @Nonnull CriteriaBuilder criteriaBuilder) {
        if (after == null && before == null) { // return void condition
          return criteriaBuilder.conjunction();
        } else if (after != null && before != null) {
          return criteriaBuilder.between(root.get("creationDate"), after, before);
        } else if (after != null) {
          return criteriaBuilder.greaterThan(root.get("creationDate"), after);
        } else {
          return criteriaBuilder.lessThan(root.get("creationDate"), before);
        }
      }
    };
  }
}
