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
    @Query(value = "select * from tournamentRegistrations t where t.tournamentId = :tournamentId and t.username = :username", nativeQuery = true)
    Optional<StudentTournamentRegistration> findByTournamentStudent(Integer tournamentId, String username);
}
