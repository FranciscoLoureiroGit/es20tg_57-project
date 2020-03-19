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

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto createClarification(QuestionAnswerDto questionAnswerDto, String title, String description, UserDto userDto){
        checkInput(questionAnswerDto, title, description, userDto);

        // Gets user and question answer from database
        QuestionAnswer questionAnswer = getQuestionAnswer(questionAnswerDto);
        User user = getUser(userDto);

        // User validation
        userValidation(userDto, questionAnswer, user);

        Clarification clarification = setClarificationValues(title, description, questionAnswer, user, clarificationRepository);
        clarificationRepository.save(clarification);

        return new ClarificationDto(clarification);
    }

    private void userValidation(UserDto userDto, QuestionAnswer questionAnswer, User user) {
        if (userDto.getId() != questionAnswer.getQuizAnswer().getUser().getId() || user.getRole() != User.Role.STUDENT)
            throw new TutorException(CLARIFICATION_USER_NOT_ALLOWED, userDto.getId());
    }

    private User getUser(UserDto userDto) {
        return userRepository.findById(userDto.getId()).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userDto.getId()));
    }

    private QuestionAnswer getQuestionAnswer(QuestionAnswerDto questionAnswerDto) {
        return questionAnswerRepository.findById(questionAnswerDto.getId()).orElseThrow(() ->
                    new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerDto.getId()));
    }

    private void checkInput(QuestionAnswerDto questionAnswerDto, String title, String description, UserDto userDto) {
        //Input Validation
        if (title.equals("") && !description.equals("")) {
            throw new TutorException(CLARIFICATION_TITLE_IS_EMPTY);
        } else if (description.equals("") && !title.equals("")) {
            throw new TutorException(CLARIFICATION_DESCRP_IS_EMPTY);
        } else if (questionAnswerDto == null && userDto != null) {
            throw new TutorException(QUESTION_ANSWER_NOT_FOUND);
        } else if (userDto == null && questionAnswerDto != null) {
            throw new TutorException(USER_NOT_FOUND);
        } else if (questionAnswerDto == null || questionAnswerDto.getId() == null || title.equals("")){
            throw new TutorException(CLARIFICATION_MISSING_DATA);
        }
    }

    private static Clarification setClarificationValues(String title, String description, QuestionAnswer questionAnswer,
                                                        User user, ClarificationRepository clarificationRepository) {
        // Creates the clarification object and sets its values
        Clarification clarification = new Clarification();

        int maxQuestionNumber = clarificationRepository.getMaxClarificationNumber() != null ?
                clarificationRepository.getMaxClarificationNumber() : 0;
        clarification.setKey(maxQuestionNumber + 1);

        clarification.setDescription(description);
        clarification.setTitle(title);
        clarification.setQuestionAnswer(questionAnswer);
        clarification.setHasAnswer(false);
        clarification.setStatus(Clarification.Status.OPEN);
        clarification.setCreationDate(LocalDateTime.now());
        clarification.setUser(user);
        questionAnswer.addClarification(clarification);
        user.addClarification(clarification);
        return clarification;
    }
}
