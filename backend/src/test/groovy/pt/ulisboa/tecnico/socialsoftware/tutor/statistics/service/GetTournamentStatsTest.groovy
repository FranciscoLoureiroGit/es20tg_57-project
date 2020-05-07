package pt.ulisboa.tecnico.socialsoftware.tutor.statistics.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.QuestionsTournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.StudentTournamentRegistration
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.QuestionsTournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.StudentTournamentRegistrationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsService
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.TournamentStatsDto
import spock.lang.Specification

@DataJpaTest
class GetTournamentStatsTest extends Specification {
    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "AS1"
    static final String ACADEMIC_TERM = "1 SEM"

    @Autowired
    QuestionsTournamentService questionsTournamentService

    @Autowired
    StatsService statsService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionsTournamentRepository questionsTournamentRepository

    @Autowired
    StudentTournamentRegistrationRepository studentTournamentRegistrationRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    TopicRepository topicRepository

    def result
    def result2
    def quiz
    def quiz2
    def quizAnswer
    def quizAnswer2
    def course
    def courseExecution1
    def courseExecution2
    def user
    def user2
    def tournament1
    def tournament2
    def tournament3
    def question1 = new Question()
    def question2 = new Question()
    def question3 = new Question()
    def question4 = new Question()
    def question5 = new Question()
    def questionAnswer1
    def questionAnswer2
    def questionAnswer3
    def topic1Dto = new TopicDto()
    def topic2Dto = new TopicDto()
    def topic1
    def topic2
    def startingDate = DateHandler.now().plusSeconds(6)
    def endingDate = DateHandler.now().plusSeconds(20)

    def setup(){
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution1 = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution1)

        courseExecution2 = new CourseExecution(course, "OTH", "2 SEM", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution2)

        user = new User('name', "username", 1, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution1)
        courseExecution1.getUsers().add(user)

        user.getCourseExecutions().add(courseExecution2)
        courseExecution2.getUsers().add(user)

        userRepository.save(user)

        user2 = new User('name2', "username2", 2, User.Role.STUDENT)
        user2.getCourseExecutions().add(courseExecution1)
        courseExecution1.getUsers().add(user2)

        user2.getCourseExecutions().add(courseExecution2)
        courseExecution2.getUsers().add(user2)

        userRepository.save(user2)

        setTopics();
        topic1 = new Topic(course, topic1Dto)
        topic2 = new Topic(course, topic2Dto)

        tournament1 = new QuestionsTournament()
        tournament1.setCourseExecution(courseExecution1)
        tournament1.setNumberOfQuestions(3)
        tournament1.setStudentTournamentCreator(user)
        tournament1.setStartingDate(startingDate)
        tournament1.setEndingDate(endingDate)
        tournament1.addTopic(topic1)
        tournament1.addTopic(topic2)

        questionsTournamentRepository.save(tournament1)

        tournament2 = new QuestionsTournament()
        tournament2.setCourseExecution(courseExecution1)
        tournament2.setNumberOfQuestions(3)
        tournament2.setStudentTournamentCreator(user)
        tournament2.setStartingDate(startingDate)
        tournament2.setEndingDate(endingDate)
        tournament2.addTopic(topic1)
        tournament2.addTopic(topic2)

        questionsTournamentRepository.save(tournament2)

        tournament3 = new QuestionsTournament()
        tournament3.setCourseExecution(courseExecution2)
        tournament3.setNumberOfQuestions(4)
        tournament3.setStudentTournamentCreator(user)
        tournament3.setStartingDate(startingDate)
        tournament3.setEndingDate(endingDate)

        questionsTournamentRepository.save(tournament3)

        setQuestions(question1)
        setQuestions(question2)
        setQuestions(question3)
        setQuestions(question4)
        setQuestions(question5)

        quiz = new Quiz();
        quiz.setKey(2);
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quiz.setCreationDate(DateHandler.now())
        quiz.setAvailableDate(tournament1.getStartingDate())
        quiz.setConclusionDate(tournament1.getEndingDate())
        quiz.setResultsDate(tournament1.getEndingDate())
        quiz.setCourseExecution(tournament1.getCourseExecution())
        tournament1.getCourseExecution().addQuiz(quiz)
        tournament1.generateQuizQuestions(quiz)
        quizRepository.save(quiz)
        quiz.setQuestionsTournament(tournament1)
        tournament1.setQuiz(quiz)

        quiz2 = new Quiz();
        quiz2.setKey(3);
        quiz2.setType(Quiz.QuizType.GENERATED.toString())
        quiz2.setCreationDate(DateHandler.now())
        quiz2.setAvailableDate(tournament2.getStartingDate())
        quiz2.setConclusionDate(tournament2.getEndingDate())
        quiz2.setResultsDate(tournament2.getEndingDate())
        quiz2.setCourseExecution(tournament2.getCourseExecution())
        tournament2.getCourseExecution().addQuiz(quiz2)
        tournament2.generateQuizQuestions(quiz2)
        quizRepository.save(quiz2)
        quiz2.setQuestionsTournament(tournament2)
        tournament2.setQuiz(quiz2)
    }

    def "a student participates in 1 tournament"(){
        given: "a student registered in tournament"
        def registration = new StudentTournamentRegistration(user, tournament1)
        user.addStudentTournamentRegistration(registration)
        tournament1.addStudentTournamentRegistration(registration)
        studentTournamentRegistrationRepository.save(registration)

        and: "a quiz answer"

        quiz = tournament1.getQuiz()
        assert quiz != null
        assert tournament1.getQuiz() != null
        quizAnswer = new QuizAnswer(user,quiz)
        def option = new Option()
        option.setCorrect(true)
        assert quiz.getQuizQuestions().size() != 0
        questionAnswer1 = new QuestionAnswer(quizAnswer, quiz.getQuizQuestions()[0],2 ,option,0)
        questionAnswer2 = new QuestionAnswer(quizAnswer, quiz.getQuizQuestions()[1],3 ,option,1)
        questionAnswer3 = new QuestionAnswer(quizAnswer, quiz.getQuizQuestions()[2],1 ,option,2)
        List<QuestionAnswer> questionsAnswerList = new ArrayList<QuestionAnswer>()
        questionsAnswerList.add(questionAnswer1)
        questionsAnswerList.add(questionAnswer2)
        questionsAnswerList.add(questionAnswer3)
        quizAnswer.setQuestionAnswers(questionsAnswerList)
        assert quizAnswer.isTournamentQuizAnswer()
        quiz.addQuizAnswer(quizAnswer)
        while(!tournament1.isClosed())
            continue;

        when:
        result = statsService.getTournamentStats(user.getId(), courseExecution1.getId())
        assert tournament1.isClosed()

        then:
        assert tournament1.getTournamentWinner() == user
        result.getCorrectAnswers() == 3
        result.getTotalAnswers() == 3
        result.getTournamentsWon() == 1
        result.getTotalTournaments() == 1
    }

    def "a student participates in 2 tournaments"(){
        given: "a student registered in two tournaments"
        def registration = new StudentTournamentRegistration(user, tournament1)
        user.addStudentTournamentRegistration(registration)
        tournament1.addStudentTournamentRegistration(registration)
        studentTournamentRegistrationRepository.save(registration)

        def registration2 = new StudentTournamentRegistration(user, tournament2)
        user.addStudentTournamentRegistration(registration2)
        tournament2.addStudentTournamentRegistration(registration2)
        studentTournamentRegistrationRepository.save(registration)

        and: "two quiz answers"

        quiz = tournament1.getQuiz()
        assert quiz != null
        assert tournament1.getQuiz() != null
        quizAnswer = new QuizAnswer(user,quiz)
        def optionCorrect = new Option()
        optionCorrect.setCorrect(true)
        assert quiz.getQuizQuestions().size() != 0
        questionAnswer1 = new QuestionAnswer(quizAnswer, quiz.getQuizQuestions()[0],2 ,optionCorrect,0)
        questionAnswer2 = new QuestionAnswer(quizAnswer, quiz.getQuizQuestions()[1],3 ,optionCorrect,1)
        questionAnswer3 = new QuestionAnswer(quizAnswer, quiz.getQuizQuestions()[2],1 ,optionCorrect,2)
        List<QuestionAnswer> questionsAnswerList = new ArrayList<QuestionAnswer>()
        questionsAnswerList.add(questionAnswer1)
        questionsAnswerList.add(questionAnswer2)
        questionsAnswerList.add(questionAnswer3)
        quizAnswer.setQuestionAnswers(questionsAnswerList)
        assert quizAnswer.isTournamentQuizAnswer()
        quiz.addQuizAnswer(quizAnswer)
        while(!tournament1.isClosed())
            continue;

        quiz = tournament2.getQuiz()
        assert quiz != null
        assert tournament2.getQuiz() != null
        def quizAnswer = new QuizAnswer(user,quiz)
        def optionWrong = new Option()
        optionWrong.setCorrect(false)
        assert quiz.getQuizQuestions().size() != 0
        questionAnswer1 = new QuestionAnswer(quizAnswer, quiz.getQuizQuestions()[0],2 ,optionCorrect,0)
        questionAnswer2 = new QuestionAnswer(quizAnswer, quiz.getQuizQuestions()[1],3 ,optionCorrect,1)
        questionAnswer3 = new QuestionAnswer(quizAnswer, quiz.getQuizQuestions()[2],1 ,optionWrong,2)
        List<QuestionAnswer> questionsAnswerList2 = new ArrayList<QuestionAnswer>()
        questionsAnswerList2.add(questionAnswer1)
        questionsAnswerList2.add(questionAnswer2)
        questionsAnswerList2.add(questionAnswer3)
        quizAnswer.setQuestionAnswers(questionsAnswerList2)
        assert quizAnswer.isTournamentQuizAnswer()
        quiz.addQuizAnswer(quizAnswer)
        while(!tournament1.isClosed() || !tournament2.isClosed())
            continue;

        when:
        result = statsService.getTournamentStats(user.getId(), courseExecution1.getId())
        assert tournament1.isClosed()

        then:
        tournament1.getTournamentWinner() == user
        tournament2.getTournamentWinner() == user
        result.getCorrectAnswers() == 5
        result.getTotalAnswers() == 6
        result.getTournamentsWon() == 2
        result.getTotalTournaments() == 2
    }

    def "two students participate in 1 tournament"(){
        given: "a student registered in tournament"
        def registration = new StudentTournamentRegistration(user, tournament1)
        user.addStudentTournamentRegistration(registration)
        tournament1.addStudentTournamentRegistration(registration)
        studentTournamentRegistrationRepository.save(registration)

        and: "another student registered in a tournament"
        def registration2 = new StudentTournamentRegistration(user2, tournament1)
        user2.addStudentTournamentRegistration(registration2)
        tournament1.addStudentTournamentRegistration(registration2)
        studentTournamentRegistrationRepository.save(registration2)

        and: "two quiz answers"

        quiz = tournament1.getQuiz()
        assert quiz != null
        assert tournament1.getQuiz() != null
        quizAnswer = new QuizAnswer(user,quiz)
        def optionCorrect = new Option()
        optionCorrect.setCorrect(true)
        def optionWrong = new Option()
        optionWrong.setCorrect(false)
        assert quiz.getQuizQuestions().size() != 0
        questionAnswer1 = new QuestionAnswer(quizAnswer, quiz.getQuizQuestions()[0],2 ,optionCorrect,0)
        questionAnswer2 = new QuestionAnswer(quizAnswer, quiz.getQuizQuestions()[1],3 ,optionCorrect,1)
        questionAnswer3 = new QuestionAnswer(quizAnswer, quiz.getQuizQuestions()[2],1 ,optionWrong,2)
        List<QuestionAnswer> questionsAnswerList = new ArrayList<QuestionAnswer>()
        questionsAnswerList.add(questionAnswer1)
        questionsAnswerList.add(questionAnswer2)
        questionsAnswerList.add(questionAnswer3)
        quizAnswer.setQuestionAnswers(questionsAnswerList)

        quizAnswer2 = new QuizAnswer(user2,quiz)
        assert quiz.getQuizQuestions().size() != 0
        def questionAnswer4 = new QuestionAnswer(quizAnswer2, quiz.getQuizQuestions()[0],2 ,optionCorrect,0)
        def questionAnswer5 = new QuestionAnswer(quizAnswer2, quiz.getQuizQuestions()[1],3 ,optionCorrect,1)
        def questionAnswer6 = new QuestionAnswer(quizAnswer2, quiz.getQuizQuestions()[2],1 ,optionCorrect,2)
        List<QuestionAnswer> questionsAnswerList2 = new ArrayList<QuestionAnswer>()
        questionsAnswerList2.add(questionAnswer4)
        questionsAnswerList2.add(questionAnswer5)
        questionsAnswerList2.add(questionAnswer6)
        quizAnswer2.setQuestionAnswers(questionsAnswerList2)

        assert quizAnswer.isTournamentQuizAnswer()
        quiz.addQuizAnswer(quizAnswer)
        quiz.addQuizAnswer(quizAnswer2)
        while(!tournament1.isClosed())
            continue;

        when:
        result = statsService.getTournamentStats(user.getId(), courseExecution1.getId())
        result2 = statsService.getTournamentStats(user2.getId(), courseExecution1.getId())

        assert tournament1.isClosed()

        then:
        assert tournament1.getTournamentWinner() == user2
        assert tournament1.getWinnerQuizAnswer() == quizAnswer2
        result.getCorrectAnswers() == 2
        result.getTotalAnswers() == 3
        result.getTournamentsWon() == 0
        result.getTotalTournaments() == 1
        result2.getCorrectAnswers() == 3
        result2.getTotalAnswers() == 3
        result2.getTournamentsWon() == 1
        result2.getTotalTournaments() == 1
    }

    private void setTopics() {
        topic1Dto.setId(1)
        topic2Dto.setId(2)
        topic1Dto.setNumberOfQuestions(2)
        topic2Dto.setNumberOfQuestions(2)
        topic1Dto.setName("A")
        topic2Dto.setName("B")
        topicRepository.save(new Topic(course, topic1Dto))
        topicRepository.save(new Topic(course, topic2Dto))
    }

    private void setQuestions(Question question){
        question.setCourse(course)
        question.addTopic(topic1)
        question.addTopic(topic2)
    }

    @TestConfiguration
    static class StatsServiceImplTestContextConfiguration {
        @Bean
        QuestionsTournamentService questionsTournamentService() {
            return new QuestionsTournamentService()
        }

        @Bean
        StatsService statsService() {
            return new StatsService()
        }

        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }
        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}

