package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ClarificationRepository extends JpaRepository<Clarification, Integer> {
    @Query(value = "SELECT * FROM clarifications c WHERE c.clarification_id = :clarificationId", nativeQuery = true)
    List<Clarification> findClarifications(int clarificationId);

    @Query(value = "SELECT * FROM clarifications c WHERE c.key = :key", nativeQuery = true)
    Optional<Clarification> findByKey(Integer key);

}
