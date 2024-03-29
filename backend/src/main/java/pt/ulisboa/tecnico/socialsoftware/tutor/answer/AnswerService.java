package pt.ulisboa.tecnico.socialsoftware.tutor.answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ClarificationAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ClarificationAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.ClarificationAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class AnswerService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ClarificationRepository clarificationRepository;

    @Autowired
    private ClarificationAnswerRepository clarificationAnswerRepository;

    @Autowired
    private AnswersXmlImport xmlImporter;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findQuestionAnswerCourseExecution(int questionAnswerId) {
        return this.questionAnswerRepository.findById(questionAnswerId)
                .map(QuestionAnswer::getCourseExecution)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerId));
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuizAnswerDto createQuizAnswer(Integer userId, Integer quizId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);
        quizAnswerRepository.save(quizAnswer);

        return new QuizAnswerDto(quizAnswer);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<CorrectAnswerDto> concludeQuiz(User user, Integer quizId) {
        QuizAnswer quizAnswer = user.getQuizAnswers().stream().filter(qa -> qa.getQuiz().getId().equals(quizId)).findFirst().orElseThrow(() ->
                new TutorException(QUIZ_NOT_FOUND, quizId));

        if (quizAnswer.getQuiz().getAvailableDate() != null && quizAnswer.getQuiz().getAvailableDate().isAfter(DateHandler.now())) {
            throw new TutorException(QUIZ_NOT_YET_AVAILABLE);
        }

        if (!quizAnswer.isCompleted()) {
            quizAnswer.setAnswerDate(DateHandler.now());
            quizAnswer.setCompleted(true);
        }

        // In class quiz when student submits before resultsDate
        if (quizAnswer.getQuiz().getResultsDate() != null &&
            quizAnswer.getQuiz().getType().equals(Quiz.QuizType.IN_CLASS) &&
            DateHandler.now().isBefore(quizAnswer.getQuiz().getResultsDate())) {

            return new ArrayList<>();
        }

        return quizAnswer.getQuestionAnswers().stream()
                .sorted(Comparator.comparing(QuestionAnswer::getSequence))
                .map(CorrectAnswerDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void submitAnswer(User user, Integer quizId, StatementAnswerDto answer) {
        QuizAnswer quizAnswer = user.getQuizAnswers().stream()
                .filter(qa -> qa.getQuiz().getId().equals(quizId))
                .findFirst()
                .orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        QuestionAnswer questionAnswer = quizAnswer.getQuestionAnswers().stream()
                .filter(qa -> qa.getSequence().equals(answer.getSequence()))
                .findFirst()
                .orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, answer.getSequence()));

        if (isNotAssignedStudent(user, quizAnswer)) {
            throw new TutorException(QUIZ_USER_MISMATCH, String.valueOf(quizAnswer.getQuiz().getId()), user.getUsername());
        }

        if (quizAnswer.getQuiz().getConclusionDate() != null && quizAnswer.getQuiz().getConclusionDate().isBefore(DateHandler.now())) {
            throw new TutorException(QUIZ_NO_LONGER_AVAILABLE);
        }

        if (quizAnswer.getQuiz().getAvailableDate() != null && quizAnswer.getQuiz().getAvailableDate().isAfter(DateHandler.now())) {
            throw new TutorException(QUIZ_NOT_YET_AVAILABLE);
        }

        if (!quizAnswer.isCompleted()) {

            Option option;
            if (answer.getOptionId() != null) {
                option = optionRepository.findById(answer.getOptionId())
                        .orElseThrow(() -> new TutorException(OPTION_NOT_FOUND, answer.getOptionId()));

                if (isNotQuestionOption(questionAnswer.getQuizQuestion(), option)) {
                    throw new TutorException(QUESTION_OPTION_MISMATCH, questionAnswer.getQuizQuestion().getQuestion().getId(), option.getId());
                }

                if (questionAnswer.getOption() != null) {
                    questionAnswer.getOption().getQuestionAnswers().remove(questionAnswer);
                }

                questionAnswer.setOption(option);
                questionAnswer.setTimeTaken(answer.getTimeTaken());
                quizAnswer.setAnswerDate(DateHandler.now());
            }
        }
    }

    private boolean isNotQuestionOption(QuizQuestion quizQuestion, Option option) {
        return quizQuestion.getQuestion().getOptions().stream().map(Option::getId).noneMatch(value -> value.equals(option.getId()));
    }

    private boolean isNotAssignedStudent(User user, QuizAnswer quizAnswer) {
        return !user.getId().equals(quizAnswer.getUser().getId());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String exportAnswers() {
        AnswersXmlExport xmlExport = new AnswersXmlExport();

        return xmlExport.export(quizAnswerRepository.findAll());
    }


    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void importAnswers(String answersXml) {
        xmlImporter.importAnswers(answersXml, this, questionRepository, quizRepository, quizAnswerRepository, userRepository);
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationAnswerDto getClarificationAnswer(Integer clarificationId) {
        Clarification clarification = clarificationRepository.findById(clarificationId).orElseThrow(() ->
                new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));
        if (clarification.getClarificationAnswer() != null) {
            return new ClarificationAnswerDto(clarification.getClarificationAnswer());
        } else {
            throw new TutorException(NO_CLARIFICATION_ANSWER);
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ClarificationAnswerDto> getAllClarificationAnswers() {
        return clarificationAnswerRepository.findAll().stream().map(ClarificationAnswerDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationAnswerDto createClarificationAnswer(ClarificationAnswerDto clarificationAnswerDto, Integer userId) {

        if(clarificationAnswerDto == null ) throw new TutorException(ErrorMessage.NULL_CLARIFICATION_ANSWER_INPUT);

        int clarificationId;

        if (clarificationAnswerDto.getClarificationId() == null) throw new TutorException(ErrorMessage.NO_CLARIFICATION_REQUEST);
        else clarificationId = clarificationAnswerDto.getClarificationId();

        String answer = clarificationAnswerDto.getAnswer();

        //Input Validation: request and answer
        Clarification clarification = validateClarification(clarificationId);

        if (answer == null || answer.trim().isEmpty()) throw new TutorException(ErrorMessage.NO_CLARIFICATION_ANSWER);

        User usr = validateUser(clarification, userId);
        ClarificationAnswer clarificationAnswer = getCreateClarificationAnswer(answer, clarification, usr);

        // Register in database
        clarificationAnswerRepository.save(clarificationAnswer);

        return new ClarificationAnswerDto(clarificationAnswer);
    }


    private ClarificationAnswer getCreateClarificationAnswer(String answer, Clarification clarification, User usr) {
        //Create the Answer

        ClarificationAnswer clarificationAnswer = new ClarificationAnswer(answer);
        clarificationAnswer.setUser(usr);
        clarificationAnswer.setClarification(clarification);

        //Link answer to request

        clarification.setClarificationAnswer(clarificationAnswer);
        clarification.setStatus(Clarification.Status.CLOSED);


        return clarificationAnswer;
    }

    private Clarification validateClarification(Integer clarificationId) {

        //Fetch Clarification from database
        Clarification clarification = clarificationRepository.findById(clarificationId).orElseThrow(() -> new TutorException(CLARIFICATION_NOT_FOUND, clarificationId));

        if (clarification.getHasAnswer()) throw new TutorException(ALREADY_HAS_ANSWER);
        return clarification;
    }

    private User validateUser(Clarification clarification, Integer userId) {
        //User Validation is done here

        if (userId == null) throw new TutorException(CANNOT_ANSWER_CLARIFICATION);


        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if( user.getRole() != User.Role.TEACHER) throw new TutorException(CANNOT_ANSWER_CLARIFICATION);

        //Get user and quizQuestion from database

        QuestionAnswer questionAnswer = questionAnswerRepository.findById(clarification.getQuestionAnswer().getId()).orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, clarification.getQuestionAnswer().getId()));

        QuizQuestion quizQuest = questionAnswer.getQuizQuestion();

        if (!user.getCourseExecutions().stream().anyMatch(                                                    //Find any courseExec that
                courseExecution -> courseExecution.getQuizzes().stream().anyMatch(                          //Has a quiz whose
                        quiz -> quiz.getQuizQuestions().stream().anyMatch(                                   //Quiz questions matches
                                quizQuestion -> quizQuestion.getId() == quizQuest.getId())                   //The quizQuestion obtained from the request

                ))) {
            // Teacher cannot answer this question, not from the same course
            throw new TutorException(CANNOT_ANSWER_CLARIFICATION);
        }
        return user;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteQuizAnswer(QuizAnswer quizAnswer) {
        List<QuestionAnswer> questionAnswers = new ArrayList<>(quizAnswer.getQuestionAnswers());
        questionAnswers.forEach(questionAnswer ->
        {
            questionAnswer.remove();
            questionAnswerRepository.delete(questionAnswer);
        });
        quizAnswer.remove();
        quizAnswerRepository.delete(quizAnswer);
    }


}
