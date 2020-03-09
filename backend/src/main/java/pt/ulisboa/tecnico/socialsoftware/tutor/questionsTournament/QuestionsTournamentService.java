package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.StudentTournamentRegistration;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.QuestionsTournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.StudentTournamentRegistrationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.StudentTournamentRegistrationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

import java.sql.SQLException;

public class QuestionsTournamentService {

    @Autowired
    private StudentTournamentRegistrationRepository registrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    QuestionsTournamentRepository tournamentRepository;

    public QuestionsTournamentDto createQuestionsTournament(Integer userId, QuestionsTournamentDto questionsTournamentDto){
        return null;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentTournamentRegistrationDto studentRegister(Integer userId, QuestionsTournamentDto questionsTournamentDto){
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        QuestionsTournament questionsTournament = tournamentRepository.findById(questionsTournamentDto.getId()).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, questionsTournamentDto.getId()));
        if(user.getRole() == User.Role.STUDENT) {
            StudentTournamentRegistration registration = new StudentTournamentRegistration();
            registrationRepository.save(registration);
        }
        else {
            throw new TutorException(USER_NOT_STUDENT);
        }

        // check if input is OK
        // check if user is a student
        // check if student is enrolled on QuestionsTournament's course
        return null;
    }
}
