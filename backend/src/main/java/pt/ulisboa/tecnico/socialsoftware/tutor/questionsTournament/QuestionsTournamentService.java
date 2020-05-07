package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.StudentTournamentRegistration;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.QuestionsTournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.StudentTournamentRegistrationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.QuestionsTournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.StudentTournamentRegistrationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    private QuestionsTournamentRepository tournamentRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private QuizService quizService;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findTournamentCourseExecution(int tournamentId) {
        return this.tournamentRepository.findById(tournamentId)
                .map(QuestionsTournament::getCourseExecution)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionsTournamentDto createQuestionsTournament(int executionId, int userId, QuestionsTournamentDto questionsTournamentDto){
        CourseExecution courseExecution = getCourseExecution(executionId);
        User user = getUserFromRepository(userId);

        QuestionsTournament questionsTournament = new QuestionsTournament(questionsTournamentDto);
        questionsTournament.setStudentTournamentCreator(user);
        questionsTournament.setCourseExecution(courseExecution);
        addTopics(questionsTournamentDto, questionsTournament);
        courseExecution.addQuestionsTournament(questionsTournament);

        tournamentRepository.save(questionsTournament);

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
        if(tournamentNotHaveQuiz(questionsTournament) && userIsNotTheCreator(user, questionsTournament))
            createAndGenerateQuiz(questionsTournament);
        return new StudentTournamentRegistrationDto(registration);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatementQuizDto getTournamentQuiz(int userId, int tournamentId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        QuestionsTournament tournament = getTournamentFromRepository(tournamentId);
        Quiz quiz = tournament.getQuiz();
        checkTournament(tournament);
        if(quiz == null) {
            tournament.forceClose();
            StatementQuizDto sqDto = new StatementQuizDto();
            sqDto.setConclusionDate(DateHandler.toISOString(tournament.getEndingDate()));
            return sqDto;
        }
        return getStatementQuizDto(user, quiz);
    }

    private void checkTournament(QuestionsTournament tournament) {
        if(!tournament.isStarted())
            throw new TutorException(TOURNAMENT_NOT_AVAILABLE);
    }

    private boolean isTournamentQuizAvailable(Quiz quiz) {
        LocalDateTime now = DateHandler.now();
        if(quiz.getAvailableDate() == null || quiz.getAvailableDate().isBefore(now)) {
            return quiz.getConclusionDate() == null || quiz.getConclusionDate().isAfter(now);
        }
        return false;
    }

    private StatementQuizDto getStatementQuizDto(User user, Quiz quiz) {
        QuizAnswer quizAnswer = getQuizAnswer(user, quiz);
        if(quizAnswer.isCompleted())
            return new StatementQuizDto();
        else
            return new StatementQuizDto(quizAnswer);
    }

    private QuizAnswer getQuizAnswer(User user, Quiz quiz) {
        return quizAnswerRepository.findQuizAnswer(quiz.getId(), user.getId()).orElseGet(() -> {
                QuizAnswer qa = new QuizAnswer(user, quiz);
                quizAnswerRepository.save(qa);
                return qa;
            });
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void cancelTournament(int userId, Integer tournamentId){
        User user = getUserFromRepository(userId);
        QuestionsTournament tournament = getTournamentFromRepository(tournamentId);
        CourseExecution courseExecution = tournament.getCourseExecution();

        if(!userIsNotTheCreator(user,tournament)){
            tournamentRepository.deleteById(tournamentId);
            courseExecution.deleteTournament(tournament);
        } else{
            throw new TutorException(USER_NOT_TOURNAMENT_CREATOR);
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionsTournamentDto> getOpenTournamentsByCourse(int executionId){
        CourseExecution courseExecution = getCourseExecution(executionId);
        return courseExecution.getOpenQuestionsTournamentsDto();
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionsTournamentDto> getRegisteredTournaments(int executionId, int userId){
        return tournamentRepository.getRegisteredTournaments(executionId, userId).stream().map(QuestionsTournamentDto::new).collect(Collectors.toList());
    }

    private void addTopics(QuestionsTournamentDto questionsTournamentDto, QuestionsTournament questionsTournament) {
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
    }

    private CourseExecution getCourseExecution(int executionId) {
        return courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
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
        registrationRepository.save(registration);
        return registration;
    }

    private boolean tournamentNotHaveQuiz(QuestionsTournament questionsTournament) {
        return questionsTournament.getQuiz() == null;
    }

    private boolean userIsNotTheCreator(User user, QuestionsTournament questionsTournament) {
        return !questionsTournament.getStudentTournamentCreator().equals(user);
    }

    private void createAndGenerateQuiz(QuestionsTournament questionsTournament) {
        Quiz quiz = new Quiz();
        quiz.setKey(quizService.getMaxQuizKey() + 1);
        quiz.setType(Quiz.QuizType.GENERATED.toString());
        quiz.setCreationDate(DateHandler.now());
        quiz.setAvailableDate(questionsTournament.getStartingDate());
        quiz.setConclusionDate(questionsTournament.getEndingDate());
        quiz.setResultsDate(questionsTournament.getEndingDate());
        quiz.setCourseExecution(questionsTournament.getCourseExecution());
        questionsTournament.getCourseExecution().addQuiz(quiz);
        questionsTournament.generateQuizQuestions(quiz);
        quizRepository.save(quiz);
        quiz.setQuestionsTournament(questionsTournament);
        questionsTournament.setQuiz(quiz);
    }

    private void addRegistrationToUser(User user, StudentTournamentRegistration registration) {
        user.addStudentTournamentRegistration(registration);
    }

    private void addRegistrationToTournament(QuestionsTournament questionsTournament, StudentTournamentRegistration registration) {
        questionsTournament.addStudentTournamentRegistration(registration);
    }
}
