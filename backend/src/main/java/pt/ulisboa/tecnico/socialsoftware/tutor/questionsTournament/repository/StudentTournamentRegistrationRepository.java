package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.StudentTournamentRegistration;

import java.util.Optional;

@Repository
@Transactional
public interface StudentTournamentRegistrationRepository extends JpaRepository<StudentTournamentRegistration, Integer> {
    @Query(value = "select * from student_tournament_registrations tr where tr.questions_tournament_id = :tournamentId and tr.user_id = :userId", nativeQuery = true)
    Optional<StudentTournamentRegistration> findByTournamentAndStudent(Integer tournamentId, Integer userId);

    @Query(value = "DELETE from student_tournament_registrations tr where tr.questions_tournament_id = :tournamentId", nativeQuery = true)
    void deleteTournamentRegistrations(Integer tournamentId);
}
