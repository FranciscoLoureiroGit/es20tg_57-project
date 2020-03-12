package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.StudentTournamentRegistration;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.QuestionsTournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.StudentTournamentRegistrationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.QuestionsTournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.StudentTournamentRegistrationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;


import javax.persistence.EntityManager;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

import java.sql.SQLException;

@Service
public class QuestionsTournamentService {


    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private StudentTournamentRegistrationRepository registrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    QuestionsTournamentRepository tournamentRepository;

    @Autowired
    EntityManager entityManager;

    public Integer getMaxQuestionsTournamentKey() {
        Integer maxQuestionsTournamentKey = tournamentRepository.getMaxQuestionsTournamentKey();
        return maxQuestionsTournamentKey != null ? maxQuestionsTournamentKey : 0;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionsTournamentDto createQuestionsTournament(int executionId, int userId, QuestionsTournamentDto questionsTournamentDto){
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if(questionsTournamentDto.getKey() == null) {
            questionsTournamentDto.setKey(getMaxQuestionsTournamentKey() + 1);
        }
        QuestionsTournament questionsTournament = new QuestionsTournament(questionsTournamentDto,user,courseExecution);
        questionsTournament.setStudentTournamentCreator(user);
        questionsTournament.setCourseExecution(courseExecution);

        if(questionsTournamentDto.getTopics() != null
                && questionsTournamentDto.getTopics().size() != 0){
            for (TopicDto topicDto : questionsTournamentDto.getTopics()){
                Topic topic = topicRepository.findById(topicDto.getId())
                        .orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND,topicDto.getId()));
                questionsTournament.addTopic(topic);
            }
        } else{
            throw new TutorException(QUESTIONSTOURNAMENT_NOT_CONSISTENT,"topics");
        }
        entityManager.persist((questionsTournament));
        return new QuestionsTournamentDto(questionsTournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentTournamentRegistrationDto studentRegister(Integer userId, Integer questionsTournamentId){
        checkNullInput(userId, questionsTournamentId);
        checkDuplicatedRegistration(userId, questionsTournamentId);
        User user = getUserFromRepository(userId);
        QuestionsTournament questionsTournament = getTournamentFromRepository(questionsTournamentId);
        StudentTournamentRegistration registration = createStudentTournamentRegistration(user, questionsTournament);
        return new StudentTournamentRegistrationDto(registration);
    }

    private void checkNullInput(Integer userId, Integer questionsTournamentId) {
        if(userId == null || questionsTournamentId == null)
            throw new TutorException(NULLID);
    }

    private User getUserFromRepository(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
    }

    private QuestionsTournament getTournamentFromRepository(Integer questionsTournamentId) {
        return tournamentRepository.findById(questionsTournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_EXIST, questionsTournamentId));
    }

    private void checkDuplicatedRegistration(Integer userId, Integer questionsTournamentId) {
        if(registrationRepository.findByTournamentAndStudent(questionsTournamentId, userId).isPresent())
            throw new TutorException(DUPLICATED_REGISTRATION);
    }

    private StudentTournamentRegistration createStudentTournamentRegistration(User user, QuestionsTournament questionsTournament) {
        StudentTournamentRegistration registration = new StudentTournamentRegistration(user, questionsTournament);
        addRegistrationToUser(user, registration);
        addRegistrationToTournament(questionsTournament, registration);
        entityManager.persist(registration);
        return registration;
    }

    private void addRegistrationToUser(User user, StudentTournamentRegistration registration) {
        user.addStudentTournamentRegistration(registration);
    }

    private void addRegistrationToTournament(QuestionsTournament questionsTournament, StudentTournamentRegistration registration) {
        questionsTournament.addStudentTournamentRegistration(registration);
    }
}
