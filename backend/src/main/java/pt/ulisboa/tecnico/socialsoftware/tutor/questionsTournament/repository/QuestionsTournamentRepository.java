package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.StudentTournamentRegistration;

import java.util.Optional;

@Repository
@Transactional
public interface QuestionsTournamentRepository extends JpaRepository<QuestionsTournament, Integer> {
    @Query(value = "select * from questionsTournaments t where t.tournamentId = :tournamentId", nativeQuery = true)
    Optional<QuestionsTournament> findByTournament(Integer tournamentId);
}
