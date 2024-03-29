package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ClarificationRepository extends JpaRepository<Clarification, Integer> {
    @Query(value = "select * from clarifications c where c.id in (select c1.id from clarifications c1 "+
            "join question_answers qA on c1.question_answer_id = qA.id join quiz_questions q on " +
            "qA.quiz_question_id = q.id where q.question_id = :questionId and c1.is_public = true)", nativeQuery = true)
    List<Clarification> findByQuestion(int questionId);

    @Query(value = "SELECT * FROM clarifications c WHERE c.user_id = :student_id", nativeQuery = true)
    List<Clarification> findClarificationsByStudentId(Integer student_id);

    @Query(value = "select * from clarifications c where c.user_id in (select c2.users_id from users_course_executions c1 " +
            "join users_course_executions c2 on c1.course_executions_id = c2.course_executions_id where c1.users_id = :teacher_id)", nativeQuery = true)
    List<Clarification> findClarificationsByTeacher(Integer teacher_id);
}
