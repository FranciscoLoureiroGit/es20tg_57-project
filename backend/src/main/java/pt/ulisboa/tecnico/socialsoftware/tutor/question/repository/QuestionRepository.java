package pt.ulisboa.tecnico.socialsoftware.tutor.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query(value = "SELECT * FROM questions q WHERE q.course_id = :courseId", nativeQuery = true)
    List<Question> findQuestions(int courseId);

    @Query(value = "SELECT * FROM questions q WHERE q.course_id = :courseId AND q.status = 'AVAILABLE'", nativeQuery = true)
    List<Question> findAvailableQuestions(int courseId);

    @Query(value = "SELECT count(*) FROM questions q WHERE q.course_id = :courseId AND q.status = 'AVAILABLE'", nativeQuery = true)
    Integer getAvailableQuestionsSize(Integer courseId);

    @Query(value = "SELECT * FROM questions q WHERE q.key = :key", nativeQuery = true)
    Optional<Question> findByKey(Integer key);

    @Query(value = "SELECT * FROM questions q WHERE q.student_id = :student_id", nativeQuery = true)
    List<Question> findQuestionsByStudentId(Integer student_id);

}