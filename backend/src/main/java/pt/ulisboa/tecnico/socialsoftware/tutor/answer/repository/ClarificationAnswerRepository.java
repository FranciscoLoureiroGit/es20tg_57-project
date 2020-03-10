package pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ClarificationAnswer;

import java.util.List;

@Repository
@Transactional
public interface ClarificationAnswerRepository extends JpaRepository<ClarificationAnswer, Integer> {
    @Query(value = "SELECT * FROM clarification_answer ca WHERE ca.clarification_answer_id = :id", nativeQuery = true)
    List<ClarificationAnswer> findClarificationAnswer(int id);
}