package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.StudentTournamentRegistration;

@Repository
@Transactional
public interface StudentTournamentRegistrationRepository extends JpaRepository<StudentTournamentRegistration, Integer> {
    @Query(value = "select * from tournamentRegistrations t where t.tournament = :tournament and t.username = :username", nativeQuery = true)
    Optional<StudentTournamentRegistration> findAllByTournamentStudent()
}
