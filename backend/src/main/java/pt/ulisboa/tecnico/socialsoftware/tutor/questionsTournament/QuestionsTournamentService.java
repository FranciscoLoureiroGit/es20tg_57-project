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
import java.util.List;

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
        if(userId == null || questionsTournamentId == null) {
            throw new TutorException(NULLUSERID);
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        QuestionsTournament questionsTournament = tournamentRepository.findById(questionsTournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_EXIST, questionsTournamentId));
        /*
        if(registrationRepository.findByTournamentAndStudent(questionsTournamentId, userId).isPresent()) {
            throw new TutorException(DUPLICATED_REGISTRATION);
        }
        */

        List<StudentTournamentRegistration> registrationList = registrationRepository.findAll();
        for(StudentTournamentRegistration registration : registrationList){
            if(registration.getQuestionsTournament().getId().intValue() == (questionsTournamentId) && registration.getStudent().getId().intValue() == userId){
                throw new TutorException(DUPLICATED_REGISTRATION);
            }
        }


        StudentTournamentRegistration registration = new StudentTournamentRegistration(user, questionsTournament);
        entityManager.persist(registration);
        user.addStudentTournamentRegistration(registration);
        questionsTournament.addStudentTournamentRegistration(registration);
        return new StudentTournamentRegistrationDto(registration);

        // check if input is OK
        // check if user is a student
        // check if student is enrolled on QuestionsTournament's course
        // check if registration is duplicated
        // check if tournament not started or already ended
    }
}
