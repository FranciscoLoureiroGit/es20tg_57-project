package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
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

    // ---- GET Services ----

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
    public List<ClarificationDto> getAllClarifications() {
        return clarificationRepository.findAll().stream().map(ClarificationDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ClarificationDto> getClarificationsByStudent(int studentId) {
        return clarificationRepository.findClarificationsByStudentId(studentId).stream().map(ClarificationDto::new).collect(Collectors.toList());

    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ClarificationDto> getClarificationsByTeacher(int teacherId) {
        return clarificationRepository.findClarificationsByTeacher(teacherId).stream().map(ClarificationDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ClarificationDto> getPublicClarifications() {
        return this.getAllClarifications().stream().filter(ClarificationDto::getPublic).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ClarificationDto> getPublicQuestionClarifications(int questionAnswerId) {
        QuestionAnswer questionAnswer = questionAnswerRepository.findById(questionAnswerId).orElseThrow(() ->
                new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerId));
        return clarificationRepository.findByQuestion(questionAnswer.getQuizQuestion().getQuestion().getId())
                .stream().map(ClarificationDto::new)
                .sorted(Comparator.comparing(ClarificationDto::getTitle))
                .collect(Collectors.toList());
    }


    // ------- SET / CREATE Services -------


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto setPrivacy(int clarificationId, boolean isPublic) {
        Clarification clarification = clarificationRepository.findById(clarificationId).orElseThrow(() ->
                new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));
        clarification.setPublic(isPublic);
        return new ClarificationDto(clarification);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationDto createClarification(Integer questionAnswerId, ClarificationDto clarificationDto, Integer userId){
        // Gets question answer from database
        QuestionAnswer questionAnswer = getQuestionAnswer(questionAnswerId);

        // Does all inputs validation
        if (userId != null){
            User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
            checkInput(questionAnswer, clarificationDto, user);
            Clarification clarification = setClarificationValues(clarificationDto, questionAnswer, user);
            clarificationRepository.save(clarification);

            return new ClarificationDto(clarification);
        }
        throw new TutorException(CLARIFICATION_MISSING_DATA);
    }

    private QuestionAnswer getQuestionAnswer(int questionAnswerId) {
        return questionAnswerRepository.findById(questionAnswerId).orElseThrow(() ->
                    new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerId));
    }

    private void checkInput(QuestionAnswer questionAnswer, ClarificationDto clarificationDto, User user) {
        //Input Validation
        if (clarificationDto.getTitle().equals("") && !clarificationDto.getDescription().equals("")) {
            throw new TutorException(CLARIFICATION_TITLE_IS_EMPTY);
        } else if (clarificationDto.getDescription().equals("") && !clarificationDto.getTitle().equals("")) {
            throw new TutorException(CLARIFICATION_DESCRP_IS_EMPTY);
        } else if (questionAnswer == null || questionAnswer.getId() == null || clarificationDto.getTitle().equals("")){
            throw new TutorException(CLARIFICATION_MISSING_DATA);
        } else if (!user.getId().equals(questionAnswer.getQuizAnswer().getUser().getId()))
            throw new TutorException(CLARIFICATION_USER_NOT_ALLOWED, user.getId());
        else {
            if (user.getRole() != User.Role.STUDENT)
                throw new TutorException(CLARIFICATION_USER_NOT_ALLOWED, user.getId());
            else if (hasClarificationRequest(user, questionAnswer)) {
                throw new TutorException(CLARIFICATION_EXISTS);
            }
        }
    }

    private static boolean hasClarificationRequest(User user, QuestionAnswer questionAnswer) {
        Set<Clarification> clarificationSet = user.getClarifications();
        for (Clarification c: clarificationSet) {
            if (c.getQuestionAnswer().getId().equals(questionAnswer.getId())) {
                return true;
            }
        }
        return false;
    }

    private static Clarification setClarificationValues(ClarificationDto clarificationDto, QuestionAnswer questionAnswer, User user) {
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
