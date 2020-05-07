package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND;

@Service
public class StatsService {

    @Autowired
    private ClarificationRepository clarificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatsDto getStats(int userId, int executionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        StatsDto statsDto = new StatsDto();

        int totalQuizzes = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .count();

        int totalAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .map(QuizAnswer::getQuestionAnswers)
                .mapToLong(Collection::size)
                .sum();

        int uniqueQuestions = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getQuizQuestion)
                .map(QuizQuestion::getQuestion)
                .map(Question::getId)
                .distinct().count();

        int correctAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getOption)
                .filter(Objects::nonNull)
                .filter(Option::getCorrect).count();

        int uniqueCorrectAnswers = (int) user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic(executionId))
                .sorted(Comparator.comparing(QuizAnswer::getAnswerDate).reversed())
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingInt(questionAnswer -> questionAnswer.getQuizQuestion().getQuestion().getId()))),
                        ArrayList::new)).stream()
                .map(QuestionAnswer::getOption)
                .filter(Objects::nonNull)
                .filter(Option::getCorrect)
                .count();

        Course course = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId)).getCourse();

        int totalAvailableQuestions = questionRepository.getAvailableQuestionsSize(course.getId());

        statsDto.setTotalQuizzes(totalQuizzes);
        statsDto.setTotalAnswers(totalAnswers);
        statsDto.setTotalUniqueQuestions(uniqueQuestions);
        statsDto.setTotalAvailableQuestions(totalAvailableQuestions);
        if (totalAnswers != 0) {
            statsDto.setCorrectAnswers(((float)correctAnswers)*100/totalAnswers);
            statsDto.setImprovedCorrectAnswers(((float)uniqueCorrectAnswers)*100/uniqueQuestions);
        }
        return statsDto;
    }

    public TournamentStatsDto getTournamentStats(int userId, int executionId){
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        TournamentStatsDto tournamentStatsDto = new TournamentStatsDto();

        int tournamentsWon = user.getNumberOfTournamentsWon();
        int totalTournaments = user.getStudentTournamentRegistrations().size();

        int totalAnswers = (int) user.getQuizAnswers().stream()
                .filter(QuizAnswer::isTournamentQuizAnswer)
                .map(QuizAnswer::getQuestionAnswers)
                .mapToLong(Collection::size)
                .sum();

        int correctAnswers = (int) user.getQuizAnswers().stream()
                .filter(QuizAnswer::isTournamentQuizAnswer)
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getOption)
                .filter(Objects::nonNull)
                .filter(Option::getCorrect)
                .count();

        tournamentStatsDto.setTournamentsWon(tournamentsWon);
        tournamentStatsDto.setCorrectAnswers(correctAnswers);
        tournamentStatsDto.setTotalAnswers(totalAnswers);
        tournamentStatsDto.setTotalTournaments(totalTournaments);
        tournamentStatsDto.setPrivacyStatus(user.getTournamentsStatsPrivacy());

        return tournamentStatsDto;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationStatsDto getClarificationStats(int userId, int executionId){    //If executionId < 0, return all clarifications
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        ClarificationStatsDto clarificationStatsDto = new ClarificationStatsDto();

        int totalClarifications;
        int publicClarifications;
        int answeredClarifications;
        int reopenedClarifications;
        Map<Integer, Long> clarificationsPerMonth;

        if(executionId > 0) {
            //Filtering for a specific course

            //Check if course exists
            courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

            totalClarifications = (int) user.getClarifications().stream().filter(clarification -> clarification.getQuestionAnswer()
                    .getCourseExecution().getId() == executionId).count();

            publicClarifications = (int) user.getClarifications().stream()
                    .filter(clarification -> clarification.getQuestionAnswer()
                            .getCourseExecution().getId() == executionId)
                    .filter(clarification -> clarification.getPublic())
                    .count();


            answeredClarifications = (int) user.getClarifications().stream()
                    .filter(clarification -> clarification.getQuestionAnswer()
                            .getCourseExecution().getId() == executionId)
                    .filter(clarification -> clarification.getHasAnswer())
                    .count();

            reopenedClarifications = (int) user.getClarifications().stream()
                    .filter(clarification -> clarification.getQuestionAnswer()
                            .getCourseExecution().getId() == executionId)
                    .filter(clarification -> !clarification.getExtraClarificationList().isEmpty())
                    .count();


            clarificationsPerMonth = user.getClarifications().stream()
                    .filter(clarification -> clarification.getQuestionAnswer()
                            .getCourseExecution().getId() == executionId)
                    .map(clarification -> clarification.getCreationDate().getYear() * 100 + clarification.getCreationDate().getMonthValue())    //Mapping local date time into a YYYYMM format, which is easier to sort
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));


        } else {
            //Return all clarifications
            totalClarifications = (int) user.getClarifications().stream().count();

            publicClarifications = (int) user.getClarifications().stream()
                    .filter(clarification -> clarification.getPublic())
                    .count();


            answeredClarifications = (int) user.getClarifications().stream()
                    .filter(clarification -> clarification.getHasAnswer())
                    .count();

            reopenedClarifications = (int) user.getClarifications().stream()
                    .filter(clarification -> !clarification.getExtraClarificationList().isEmpty())
                    .count();


            clarificationsPerMonth = user.getClarifications().stream()
                    .map(clarification -> clarification.getCreationDate().getYear() * 100 + clarification.getCreationDate().getMonthValue())    //Mapping local date time into a YYYYMM format, which is easier to sort
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        }




        clarificationStatsDto.setTotalClarifications(totalClarifications);
        clarificationStatsDto.setPublicClarifications(publicClarifications);
        clarificationStatsDto.setAnsweredClarifications(answeredClarifications);
        clarificationStatsDto.setReopenedClarifications(reopenedClarifications);
        clarificationStatsDto.setClarificationsPerMonth(clarificationsPerMonth);

        return clarificationStatsDto;
    }
    public void setTournamentsStatsPrivacy(Integer userId, User.PrivacyStatus privacyStatus) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        user.setTournamentsStatsPrivacy(privacyStatus);
    }
}
