package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class ClarificationService {

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClarificationRepository clarificationRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto findClarificationById(Integer clarificationId) {
        return clarificationRepository.findById(clarificationId).map(ClarificationDto::new)
                .orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto findClarificationByKey(Integer key) {
        return clarificationRepository.findByKey(key).map(ClarificationDto::new)
                .orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, key));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ClarificationDto> findClarifications(int questionId) {
        return clarificationRepository.findClarifications(questionId).stream().map(ClarificationDto::new).collect(Collectors.toList());
    }

    public ClarificationDto createClarification(QuestionAnswerDto questionAnswerDto, String title, String description, UserDto userDto){
        //Input Validation
        if (title.equals("")) {
            throw new TutorException(CLARIFICATION_TITLE_IS_EMPTY);
        } else if (description.equals("")) {
            throw new TutorException(CLARIFICATION_DESCRP_IS_EMPTY);
        } else if (questionAnswerDto == null) {
            throw new TutorException(QUESTION_ANSWER_NOT_FOUND);
        } else if (userDto == null) {
            throw new TutorException(USER_NOT_FOUND);
        }

        // Gets user and question answer from database
        QuestionAnswer questionAnswer = questionAnswerRepository.findById(questionAnswerDto.getId()).orElseThrow(() ->
                new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerDto.getId()));
        User user = userRepository.findById(userDto.getId()).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userDto.getId()));

        // User validation
        if (userDto.getId() != questionAnswer.getId() || user.getRole() == User.Role.STUDENT)
            throw new TutorException(CLARIFICATION_USER_NOT_ALLOWED);

        // Creates the clarification object and returns its Dto
        Clarification clarification = new Clarification();
        clarification.setDescription(description);
        clarification.setTitle(title);
        clarification.setQuestionAnswer(questionAnswer);
        clarification.setHasAnswer(false);
        clarification.setStatus(Clarification.Status.OPEN);
        clarification.setCreationDate(LocalDateTime.now());
        clarification.setStudent(user);
        this.entityManager.persist(clarification);
        return new ClarificationDto(clarification);
    }
}
