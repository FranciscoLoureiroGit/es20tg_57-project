package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ClarificationAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ClarificationAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.ClarificationAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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

    @Autowired
    private ClarificationAnswerRepository clarificationAnswerRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto getClarificationById(Integer clarificationId) {
        return clarificationRepository.findById(clarificationId).map(ClarificationDto::new)
                .orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto getClarificationByKey(Integer key) {
        return clarificationRepository.findByKey(key).map(ClarificationDto::new)
                .orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, key));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ClarificationDto> getClarificationsByQuestion(int questionAnswerId) {
        return clarificationRepository.findByQuestionAnswer(questionAnswerId).stream().map(ClarificationDto::new)
                .sorted(Comparator
                        .comparing(ClarificationDto::getTitle))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ClarificationDto> getClarifications() {
        return clarificationRepository.findAll().stream().map(ClarificationDto::new)
                .collect(Collectors.toList());
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationAnswerDto getClarificationAnswer(int studentId, int questionAnswerId) {
        User user = userRepository.getOne(studentId);
        Set<ClarificationAnswer> clarificationAnswers = user.getClarificationAnswers();
        for (ClarificationAnswer ca : clarificationAnswers ) {
            if (ca.getClarification().getQuestionAnswer().getId().equals(questionAnswerId)) {
                return new ClarificationAnswerDto(ca);
            }
        }
        throw new TutorException(NO_CLARIFICATION_ANSWER);
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto createClarification(Integer questionAnswerId, ClarificationDto clarificationDto, Integer userId){
        // Gets question answer from database
        QuestionAnswer questionAnswer = getQuestionAnswer(questionAnswerId);

        // Does all inputs validation
        User user = checkInput(questionAnswer, clarificationDto, userId);

        Clarification clarification = setClarificationValues(clarificationDto, questionAnswer, user, clarificationRepository);
        clarificationRepository.save(clarification);

        return new ClarificationDto(clarification);
    }

    private QuestionAnswer getQuestionAnswer(int questionAnswerId) {
        return questionAnswerRepository.findById(questionAnswerId).orElseThrow(() ->
                    new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerId));
    }

    private User checkInput(QuestionAnswer questionAnswer, ClarificationDto clarificationDto, Integer userId) {
        //Input Validation
        if (clarificationDto.getTitle().equals("") && !clarificationDto.getDescription().equals("")) {
            throw new TutorException(CLARIFICATION_TITLE_IS_EMPTY);
        } else if (clarificationDto.getDescription().equals("") && !clarificationDto.getTitle().equals("")) {
            throw new TutorException(CLARIFICATION_DESCRP_IS_EMPTY);
        } else if (questionAnswer == null && userId != null) {
            throw new TutorException(QUESTION_ANSWER_NOT_FOUND);
        } else if (userId == null && questionAnswer != null) {
            throw new TutorException(USER_NOT_FOUND);
        } else if (questionAnswer == null || questionAnswer.getId() == null || clarificationDto.getTitle().equals("")){
            throw new TutorException(CLARIFICATION_MISSING_DATA);
        } else if (!userId.equals(questionAnswer.getQuizAnswer().getUser().getId()))
            throw new TutorException(CLARIFICATION_USER_NOT_ALLOWED, userId);
        else {
            User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
            if (user.getRole() != User.Role.STUDENT)
                throw new TutorException(CLARIFICATION_USER_NOT_ALLOWED, userId);
            return user;
        }
    }

    private static Clarification setClarificationValues(ClarificationDto clarificationDto, QuestionAnswer questionAnswer,
                                                        User user, ClarificationRepository clarificationRepository) {
        if (clarificationDto.getKey() == null) {
            int maxQuestionNumber = clarificationRepository.getMaxClarificationNumber() != null ?
                    clarificationRepository.getMaxClarificationNumber() : 0;
            clarificationDto.setKey(maxQuestionNumber + 1);
        }
        // Creates the clarification object and sets its values
        Clarification clarification = new Clarification(clarificationDto);

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
