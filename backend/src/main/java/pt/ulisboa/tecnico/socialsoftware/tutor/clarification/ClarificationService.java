package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
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
    private QuestionRepository questionRepository;

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

    public ClarificationDto createClarification(QuestionDto questionDto, String title, String description, UserDto userDto){
        Question question = questionRepository.findById(questionDto.getId()).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionDto.getId()));
        //User user = userRepository.findById(userDto.getId()).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userDto.getId()));

        if (questionDto.getKey() == null) {
            int maxQuestionNumber = questionRepository.getMaxQuestionNumber() != null ?
                    questionRepository.getMaxQuestionNumber() : 0;
            questionDto.setKey(maxQuestionNumber + 1);
        }

        Clarification clarification = new Clarification();
        //clarification.setDescription();
        //clarification.setTitle();
        clarification.setCreationDate(LocalDateTime.now());
        this.entityManager.persist(question);
        return new ClarificationDto(clarification);
    }
}
