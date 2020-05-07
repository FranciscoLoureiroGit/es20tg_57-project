package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.StudentTournamentRegistration;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface QuestionsTournamentRepository extends JpaRepository<QuestionsTournament, Integer> {
    @Query(value = "select * from QUESTIONSTOURNAMENTS t where t.id = :tournamentId", nativeQuery = true)
    Optional<QuestionsTournament> findByTournamentId(Integer tournamentId);

    @Query(value = "select * from QUESTIONSTOURNAMENTS t " +
            "JOIN student_tournament_registrations st " +
            "ON t.id = st.questions_tournament_id " +
            "where st.user_id = :userId and course_execution_id = :courseExecutionId", nativeQuery = true)
    List<QuestionsTournament> getRegisteredTournaments(Integer courseExecutionId, Integer userId);
}
