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
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ExtraClarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ExtraClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ExtraClarificationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.NotificationService;
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.domain.Notification;
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.dto.NotificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;
import static pt.ulisboa.tecnico.socialsoftware.tutor.notification.NotificationMessages.*;

@Service
public class ClarificationService {

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClarificationRepository clarificationRepository;

    @Autowired
    private ExtraClarificationRepository extraClarificationRepository;

    @Autowired
    private NotificationService notificationService;

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

        // create notification for this service (notify student)
        if (isPublic) {
            NotificationDto notificationDto = new NotificationDto(CLARIFICATION_PUBLIC_TITLE.label, CLARIFICATION_PUBLIC_DESCRIPTION.label, Notification.Status.DELIVERED.name(), clarification.getUser().getUsername());
            notificationService.createNotification(notificationDto, clarification.getUser().getUsername());
        } else {
            NotificationDto notificationDto = new NotificationDto(CLARIFICATION_PRIVATE_TITLE.label, CLARIFICATION_PRIVATE_DESCRIPTION.label, Notification.Status.DELIVERED.name(), clarification.getUser().getUsername());
            notificationService.createNotification(notificationDto, clarification.getUser().getUsername());
        }

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

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation =  Isolation.REPEATABLE_READ)
    public ExtraClarificationDto createExtraClarification(ExtraClarificationDto extraClarificationDto, Integer userId){

        if(extraClarificationDto.getComment() == null || extraClarificationDto.getComment().equals("")){
            throw new TutorException(EMPTY_EXTRA_CLARIFICATION_COMMENT);
        }
        if(extraClarificationDto.getCommentType() == null || extraClarificationDto.getCommentType().equals("")){
            throw new TutorException(NO_EXTRA_CLARIFICATION_TYPE);
        }
        if(extraClarificationDto.getParentClarificationId() == null){
            throw new TutorException(NO_EXTRA_CLARIFICATION_PARENT);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if( !(user.getRole() == User.Role.STUDENT && extraClarificationDto.getCommentType().equals("QUESTION"))
                && !(user.getRole() == User.Role.TEACHER && extraClarificationDto.getCommentType().equals("ANSWER"))){
            throw new TutorException(EXTRA_CLARIFICATION_NO_COMMENT_PERMISSION);
        }

        Clarification clarification = clarificationRepository.findById(extraClarificationDto.getParentClarificationId()).orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, extraClarificationDto.getParentClarificationId()));

        if( (extraClarificationDto.getCommentType().equals("QUESTION") && clarification.getExtraClarificationList().size()%2 != 0)
                || (extraClarificationDto.getCommentType().equals("ANSWER") && clarification.getExtraClarificationList().size()%2 != 1)){
            throw new TutorException(EXTRA_CLARIFICATION_INVALID_NEXT_COMMENT_TYPE, extraClarificationDto.getCommentType());
        }

        ExtraClarification extraClarification = new ExtraClarification(extraClarificationDto);
        extraClarification.setParentClarification(clarification);
        extraClarification.setCreationDate(LocalDateTime.now());


        clarification.addExtraClarification(extraClarification);

        extraClarificationRepository.save(extraClarification);

        // create notification for this service (notify teacher)
        NotificationDto notificationDto = new NotificationDto(EXTRA_CLARIFICATION_TITLE.label,EXTRA_CLARIFICATION_DESCRIPTION.label, "DELIVERED", clarification.getClarificationAnswer().getUser().getUsername());
        notificationService.createNotification(notificationDto, clarification.getClarificationAnswer().getUser().getUsername());

        return new ExtraClarificationDto(extraClarification);

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
