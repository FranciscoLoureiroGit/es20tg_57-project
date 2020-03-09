package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface ClarificationRepository extends JpaRepository<Clarification, Integer> {

}
