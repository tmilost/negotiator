package eu.bbmri_eric.negotiator.negotiation;

import eu.bbmri_eric.negotiator.negotiation.state_machine.negotiation.NegotiationState;
import eu.bbmri_eric.negotiator.negotiation.state_machine.resource.NegotiationResourceState;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NegotiationRepository
    extends JpaRepository<Negotiation, String>, JpaSpecificationExecutor<Negotiation> {

  Optional<Negotiation> findDetailedById(String id);

  @Query(value = "SELECT currentState from Negotiation where id = :id")
  Optional<NegotiationState> findNegotiationStateById(String id);

  List<Negotiation> findAllByCurrentState(NegotiationState state);

  @Query(
      value =
          """
    SELECT count ( distinct n.id)
    FROM Negotiation n
    join n.resourcesLink rl
    JOIN rl.id.resource r
    JOIN r.networks networks
    where networks.id = :networkId and rl.currentState = 'REPRESENTATIVE_CONTACTED' or rl.currentState = 'REPRESENTATIVE_UNREACHABLE'
""")
  Integer countIgnoredForNetwork(Long networkId);

  @Query(
      value =
          """
            SELECT PERCENTILE_CONT(0.5)
           WITHIN GROUP (ORDER BY EXTRACT(EPOCH FROM (nrlr.creation_date - n.creation_date)) / 86400)
           AS median_days
            FROM Negotiation n
            LEFT JOIN public.negotiation_resource_lifecycle_record nrlr on n.id = nrlr.negotiation_id
            left join public.resource r on r.id = nrlr.resource_id
                    left join public.network_resources_link nrl on r.id = nrl.resource_id
                    left join public.network n2 on n2.id = nrl.network_id
            where n2.id = :networkId and nrlr.changed_to = 'CHECKING_AVAILABILITY' or nrlr.changed_to = 'RESOURCE_UNAVAILABLE'
        """,
      nativeQuery = true)
  Double getMedianResponseForNetwork(Long networkId);

  @Query(
      value =
          """
            SELECT count ( distinct n.id)
            FROM Negotiation n
            join n.resourcesLink rl
            JOIN rl.id.resource r
            JOIN r.networks networks
            where networks.id = :networkId and rl.currentState = 'RESOURCE_MADE_AVAILABLE'
        """)
  Integer getNumberOfSuccessfulNegotiationsForNetwork(Long networkId);

  @Query(
      value =
          """
          SELECT rl.currentState
          FROM Negotiation n
          JOIN n.resourcesLink rl
          JOIN rl.id.resource
          WHERE n.id = :negotiationId AND rl.id.resource.sourceId = :resourceId
        """)
  Optional<NegotiationResourceState> findNegotiationResourceStateById(
      String negotiationId, String resourceId);

  boolean existsByIdAndCreatedBy_Id(String negotiationId, Long personId);

  List<Negotiation> findByModifiedDateBeforeAndCurrentState(
      LocalDateTime thresholdTime, NegotiationState currentState);

  @Query(
      value =
          "SELECT EXISTS ("
              + "SELECT distinct(n.id) "
              + "FROM negotiation n "
              + "    JOIN negotiation_resource_link rrl ON rrl.negotiation_id = n.id "
              + "    JOIN resource rs ON rrl.resource_id = rs.id "
              + "    JOIN organization o ON rs.organization_id = o.id "
              + "WHERE n.id = :negotiationId and o.external_id = :organizationExternalId)",
      nativeQuery = true)
  boolean isOrganizationPartOfNegotiation(String negotiationId, String organizationExternalId);

  @Query(
      value =
          "SELECT distinct (n) "
              + "FROM Negotiation n "
              + "JOIN n.resourcesLink rl "
              + "JOIN rl.id.resource rs "
              + "JOIN rs.networks net "
              + "WHERE net.id = :networkId")
  Page<Negotiation> findAllForNetwork(Long networkId, Pageable pageable);

  @Query(
      value =
          "select count (distinct n.id) "
              + "FROM Negotiation n "
              + "JOIN n.resourcesLink rl "
              + "JOIN rl.id.resource rs "
              + "JOIN rs.networks net "
              + "WHERE net.id = :networkId")
  Integer countAllForNetwork(Long networkId);

  @Query(
      value =
          "select n.currentState, COUNT ( distinct n.id)"
              + "FROM Negotiation n "
              + "JOIN n.resourcesLink rl "
              + "JOIN rl.id.resource rs "
              + "JOIN rs.networks net "
              + "WHERE net.id = :networkId group by n.currentState")
  List<Object[]> countStatusDistribution(Long networkId);

  @Query(
      value =
          """
SELECT distinct n from Negotiation n join n.resourcesLink rl
join rl.id.resource rs
join rs.representatives reps
where n.currentState = 'IN_PROGRESS' and reps.id = :personId and rl.currentState = 'REPRESENTATIVE_CONTACTED'
""")
  List<Negotiation> findNegotiationsWithNoStatusUpdateFor(Long personId);

  @Query(value = "SELECT n FROM Negotiation n WHERE FUNCTION('DATE', n.creationDate) = :targetDate")
  Set<Negotiation> findAllCreatedOn(LocalDateTime targetDate);

  @Query(
      value =
          """
    SELECT count (distinct n.created_by) FROM Negotiation n
                                         LEFT JOIN public.negotiation_resource_link nrlr on n.id = nrlr.negotiation_id
            left join public.resource r on r.id = nrlr.resource_id
                    left join public.network_resources_link nrl on r.id = nrl.resource_id
                    left join public.network n2 on n2.id = nrl.network_id
                                         WHERE n.created_by NOT IN (
                                                                     SELECT DISTINCT created_by
                                                                     FROM Negotiation
                                                                     WHERE creation_date < :since
                                                                 ) and n2.id = :networkId
""",
      nativeQuery = true)
  Integer getNumberOfNewRequesters(LocalDateTime since, Long networkId);

  @Query(
      value =
          """
        SELECT COUNT(DISTINCT created_by) AS SUM_COUNT
        FROM (
            SELECT nrlr.created_by
            FROM Negotiation n
            LEFT JOIN public.negotiation_resource_link nrl ON n.id = nrl.negotiation_id
            LEFT JOIN public.negotiation_resource_lifecycle_record nrlr ON n.id = nrlr.negotiation_id
            LEFT JOIN public.resource r ON r.id = nrl.resource_id
            LEFT JOIN public.network_resources_link netrl ON r.id = netrl.resource_id
            LEFT JOIN public.network n2 ON n2.id = netrl.network_id
            WHERE nrlr.creation_date > :since
              AND nrlr.creation_date < :until
              AND n2.id = :networkId
            UNION
            SELECT p.created_by
            FROM post p
            LEFT JOIN public.resource_representative_link rrl ON p.created_by = rrl.person_id
            LEFT JOIN public.network_resources_link l ON rrl.resource_id = l.resource_id
            WHERE p.creation_date > :since
              AND p.creation_date < :until
              AND l.network_id = :networkId
        ) AS combined;
    """,
      nativeQuery = true)
  Integer getNumberOfActiveRepresentatives(
      LocalDateTime since, LocalDateTime until, Long networkId);
}
