package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ClarificationAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.StudentTournamentRegistration;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.domain.Notification;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User implements UserDetails, DomainEntity {
    public enum Role {STUDENT, TEACHER, ADMIN, DEMO_ADMIN}
    public enum PrivacyStatus {PUBLIC, PRIVATE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private Integer key;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(unique=true)
    private String username;

    @Column
    private String email;

    private String name;
    private String enrolledCoursesAcronyms;

    private Integer numberOfTeacherQuizzes;
    private Integer numberOfStudentQuizzes;
    private Integer numberOfInClassQuizzes;
    private Integer numberOfTeacherAnswers;
    private Integer numberOfInClassAnswers;
    private Integer numberOfStudentAnswers;
    private Integer numberOfCorrectTeacherAnswers;
    private Integer numberOfCorrectInClassAnswers;
    private Integer numberOfCorrectStudentAnswers;
    private Integer numberOfTournamentsWon;
    private Integer numberOfTournamentQuestionsAnswers;
    private Integer numberOfCorrectTournamentQuestionsAnswers;

    @Column(name="stats_privacy")
    private PrivacyStatus dashboardPrivacy;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "last_access")
    private LocalDateTime lastAccess;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval=true)
    private Set<QuizAnswer> quizAnswers = new HashSet<>();

    @ManyToMany
    private Set<CourseExecution> courseExecutions = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval=true)
    private Set<Clarification> clarifications = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<ClarificationAnswer> clarificationAnswers = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch=FetchType.EAGER, orphanRemoval=true)
    private Set<StudentTournamentRegistration> studentTournamentRegistrations = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval=true)
    private Set<Notification> notifications = new HashSet<>();

    public User() {
    }

    public User(String name, String username, Integer key, User.Role role) {
        this.name = name;
        setUsername(username);
        this.key = key;
        this.role = role;
        this.creationDate = DateHandler.now();
        this.numberOfTeacherQuizzes = 0;
        this.numberOfInClassQuizzes = 0;
        this.numberOfStudentQuizzes = 0;
        this.numberOfTeacherAnswers = 0;
        this.numberOfInClassAnswers = 0;
        this.numberOfStudentAnswers = 0;
        this.numberOfCorrectTeacherAnswers = 0;
        this.numberOfCorrectInClassAnswers = 0;
        this.numberOfCorrectStudentAnswers = 0;
        this.numberOfTournamentsWon = 0;
        this.numberOfTournamentQuestionsAnswers = 0;
        this.numberOfCorrectTournamentQuestionsAnswers = 0;
        this.dashboardPrivacy = PrivacyStatus.PRIVATE;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitUser(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public void setQuizAnswers(Set<QuizAnswer> quizAnswers) {
        this.quizAnswers = quizAnswers;
    }

    public Set<Clarification> getClarifications() {
        return clarifications;
    }

    public void setClarifications(Set<Clarification> clarifications) {
        this.clarifications = clarifications;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnrolledCoursesAcronyms() {
        return enrolledCoursesAcronyms;
    }

    public void setEnrolledCoursesAcronyms(String enrolledCoursesAcronyms) {
        this.enrolledCoursesAcronyms = enrolledCoursesAcronyms;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    public Set<QuizAnswer> getQuizAnswers() {
        return quizAnswers;
    }

    public Set<CourseExecution> getCourseExecutions() {
        return courseExecutions;
    }

    public void setCourseExecutions(Set<CourseExecution> courseExecutions) {
        this.courseExecutions = courseExecutions;
    }

    public Integer getNumberOfTeacherQuizzes() {
        if (this.numberOfTeacherQuizzes == null)
            this.numberOfTeacherQuizzes = (int) getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.PROPOSED))
                    .count();

        return numberOfTeacherQuizzes;
    }

    public void setNumberOfTeacherQuizzes(Integer numberOfTeacherQuizzes) {
        this.numberOfTeacherQuizzes = numberOfTeacherQuizzes;
    }

    public Integer getNumberOfStudentQuizzes() {
        if (this.numberOfStudentQuizzes == null)
            this.numberOfStudentQuizzes = (int) getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.GENERATED))
                    .count();

        return numberOfStudentQuizzes;
    }

    public void setNumberOfStudentQuizzes(Integer numberOfStudentQuizzes) {
        this.numberOfStudentQuizzes = numberOfStudentQuizzes;
    }

    public Integer getNumberOfInClassQuizzes() {
        if (this.numberOfInClassQuizzes == null)
            this.numberOfInClassQuizzes = (int) getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.IN_CLASS))
                    .count();

        return numberOfInClassQuizzes;
    }

    public void setNumberOfInClassQuizzes(Integer numberOfInClassQuizzes) {
        this.numberOfInClassQuizzes = numberOfInClassQuizzes;
    }

    public Integer getNumberOfTeacherAnswers() {
        if (this.numberOfTeacherAnswers == null)
            this.numberOfTeacherAnswers = getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.PROPOSED))
                    .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestions().size())
                    .sum();

        return numberOfTeacherAnswers;
    }

    public void setNumberOfTeacherAnswers(Integer numberOfTeacherAnswers) {
        this.numberOfTeacherAnswers = numberOfTeacherAnswers;
    }

    public Integer getNumberOfInClassAnswers() {
        if (this.numberOfInClassAnswers == null)
            this.numberOfInClassAnswers = getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.IN_CLASS))
                    .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestions().size())
                    .sum();
            return numberOfInClassAnswers;
    }

    public void setNumberOfInClassAnswers(Integer numberOfInClassAnswers) {
        this.numberOfInClassAnswers = numberOfInClassAnswers;
    }

    public Integer getNumberOfStudentAnswers() {
        if (this.numberOfStudentAnswers == null) {
            this.numberOfStudentAnswers = getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.GENERATED))
                    .mapToInt(quizAnswer -> quizAnswer.getQuiz().getQuizQuestions().size())
                    .sum();
        }

        return numberOfStudentAnswers;
    }

    public void setNumberOfStudentAnswers(Integer numberOfStudentAnswers) {
        this.numberOfStudentAnswers = numberOfStudentAnswers;
    }

    public Integer getNumberOfCorrectTeacherAnswers() {
        if (this.numberOfCorrectTeacherAnswers == null)
            this.numberOfCorrectTeacherAnswers = (int) this.getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.PROPOSED))
                    .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                    .filter(questionAnswer -> questionAnswer.getOption() != null &&
                            questionAnswer.getOption().getCorrect())
                    .count();

            return numberOfCorrectTeacherAnswers;
    }

    public void setNumberOfCorrectTeacherAnswers(Integer numberOfCorrectTeacherAnswers) {
        this.numberOfCorrectTeacherAnswers = numberOfCorrectTeacherAnswers;
    }

    public Integer getNumberOfCorrectInClassAnswers() {
        if (this.numberOfCorrectInClassAnswers == null)
            this.numberOfCorrectInClassAnswers = (int) this.getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.IN_CLASS))
                    .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                    .filter(questionAnswer -> questionAnswer.getOption() != null &&
                        questionAnswer.getOption().getCorrect())
                    .count();

        return numberOfCorrectInClassAnswers;
    }

    public void setNumberOfCorrectInClassAnswers(Integer numberOfCorrectInClassAnswers) {
        this.numberOfCorrectInClassAnswers = numberOfCorrectInClassAnswers;
    }

    public Integer getNumberOfCorrectStudentAnswers() {
        if (this.numberOfCorrectStudentAnswers == null)
            this.numberOfCorrectStudentAnswers = (int) this.getQuizAnswers().stream()
                    .filter(QuizAnswer::isCompleted)
                    .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.GENERATED))
                    .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                    .filter(questionAnswer -> questionAnswer.getOption() != null &&
                        questionAnswer.getOption().getCorrect())
                    .count();

        return numberOfCorrectStudentAnswers;
    }

    public Integer getNumberOfTournamentsWon() {

        return this.numberOfTournamentsWon = (int) this.getStudentTournamentRegistrations().stream()
                .filter(StudentTournamentRegistration::isWinner)
                .count();
    }

    public Integer getNumberOfTournamentQuestionsAnswers() {
        return Objects.requireNonNullElse(this.numberOfTournamentQuestionsAnswers, 0);
    }

    public Integer getNumberOfCorrectTournamentQuestionsAnswers() {
        return Objects.requireNonNullElse(this.numberOfCorrectTournamentQuestionsAnswers, 0);
    }

    public void setNumberOfCorrectStudentAnswers(Integer numberOfCorrectStudentAnswers) {
        this.numberOfCorrectStudentAnswers = numberOfCorrectStudentAnswers;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", key=" + key +
                ", role=" + role +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", enrolledCoursesAcronyms='" + enrolledCoursesAcronyms + '\'' +
                ", numberOfTeacherQuizzes=" + numberOfTeacherQuizzes +
                ", numberOfStudentQuizzes=" + numberOfStudentQuizzes +
                ", numberOfInClassQuizzes=" + numberOfInClassQuizzes +
                ", numberOfTeacherAnswers=" + numberOfTeacherAnswers +
                ", numberOfInClassAnswers=" + numberOfInClassAnswers +
                ", numberOfStudentAnswers=" + numberOfStudentAnswers +
                ", numberOfCorrectTeacherAnswers=" + numberOfCorrectTeacherAnswers +
                ", numberOfCorrectInClassAnswers=" + numberOfCorrectInClassAnswers +
                ", numberOfCorrectStudentAnswers=" + numberOfCorrectStudentAnswers +
                ", creationDate=" + creationDate +
                ", lastAccess=" + lastAccess +
                '}';
    }

    public void increaseNumberOfQuizzes(Quiz.QuizType type) {
        switch (type) {
            case PROPOSED:
                this.numberOfTeacherQuizzes = getNumberOfTeacherQuizzes() + 1;
                break;
            case IN_CLASS:
                this.numberOfInClassQuizzes = getNumberOfInClassQuizzes() + 1;
                break;
            case GENERATED:
                this.numberOfStudentQuizzes = getNumberOfStudentQuizzes() + 1;
                break;
            default:
                break;
        }
    }

    public void increaseNumberOfAnswers(Quiz.QuizType type) {
        switch (type) {
            case PROPOSED:
                this.numberOfTeacherAnswers = getNumberOfTeacherAnswers() + 1;
                break;
            case IN_CLASS:
                this.numberOfInClassAnswers = getNumberOfInClassAnswers() + 1;
                break;
            case GENERATED:
                this.numberOfStudentAnswers = getNumberOfStudentAnswers() + 1;
                break;
            default:
                break;
        }
    }

    public void increaseNumberOfCorrectAnswers(Quiz.QuizType type) {
        switch (type) {
            case PROPOSED:
                this.numberOfCorrectTeacherAnswers = getNumberOfCorrectTeacherAnswers() + 1;
                break;
            case IN_CLASS:
                this.numberOfCorrectInClassAnswers = getNumberOfCorrectInClassAnswers() + 1;
                break;
            case GENERATED:
                this.numberOfCorrectStudentAnswers = getNumberOfCorrectStudentAnswers() + 1;
                break;
            default:
                break;
        }
    }

    public void increaseNumberOfTournamentsWon() {
        this.numberOfTournamentsWon = getNumberOfTournamentsWon() + 1;
    }

    public void increaseNumberOfTournamentQuestionsAnswers() {
        this.numberOfTournamentQuestionsAnswers = getNumberOfTournamentQuestionsAnswers() + 1;
    }

    public void increaseNumberOfCorrectTournamentQuestionsAnswers() {
        this.numberOfCorrectTournamentQuestionsAnswers = getNumberOfCorrectTournamentQuestionsAnswers() + 1;
    }

    public void addQuizAnswer(QuizAnswer quizAnswer) {
        this.quizAnswers.add(quizAnswer);
    }

    public void addCourse(CourseExecution course) {
        this.courseExecutions.add(course);
    }

    public Set<StudentTournamentRegistration> getStudentTournamentRegistrations() {
        return studentTournamentRegistrations;
    }

    public void addStudentTournamentRegistration(StudentTournamentRegistration studentTournamentRegistration) {
        this.studentTournamentRegistrations.add(studentTournamentRegistration);
    }

    public void addClarification(Clarification clarification) { this.clarifications.add(clarification); }

    public boolean isInCourseExecution(CourseExecution courseExecution) {
        return this.getCourseExecutions().contains(courseExecution);
    }

    public void setNumberOfTournamentsWon(Integer numberOfTournamentsWon) {
        this.numberOfTournamentsWon = numberOfTournamentsWon;
    }

    public void setNumberOfTournamentQuestionsAnswers(Integer numberOfTournamentQuestionsAnswers) {
        this.numberOfTournamentQuestionsAnswers = numberOfTournamentQuestionsAnswers;
    }

    public void setNumberOfCorrectTournamentQuestionsAnswers(Integer numberOfCorrectTournamentQuestionsAnswers) {
        this.numberOfCorrectTournamentQuestionsAnswers = numberOfCorrectTournamentQuestionsAnswers;
    }

    public PrivacyStatus getDashboardPrivacy() {
        return dashboardPrivacy;
    }

    public void setDashboardPrivacy(PrivacyStatus privacyStatus) {
        this.dashboardPrivacy = privacyStatus;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();

        list.add(new SimpleGrantedAuthority("ROLE_" + role));

        return list;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public List<Question> filterQuestionsByStudentModel(Integer numberOfQuestions, List<Question> availableQuestions) {
        List<Question> studentAnsweredQuestions = getQuizAnswers().stream()
                .flatMap(quizAnswer -> quizAnswer.getQuestionAnswers().stream())
                .filter(questionAnswer -> availableQuestions.contains(questionAnswer.getQuizQuestion().getQuestion()))
                .filter(questionAnswer -> questionAnswer.getTimeTaken() != null && questionAnswer.getTimeTaken() != 0)
                .map(questionAnswer -> questionAnswer.getQuizQuestion().getQuestion())
                .collect(Collectors.toList());

        List<Question> notAnsweredQuestions = availableQuestions.stream()
                .filter(question -> !studentAnsweredQuestions.contains(question))
                .collect(Collectors.toList());

        List<Question> result = new ArrayList<>();

        // add 80% of notanswered questions
        // may add less if not enough notanswered
        int numberOfAddedQuestions = 0;
        while (numberOfAddedQuestions < numberOfQuestions * 0.8
                && notAnsweredQuestions.size() >= numberOfAddedQuestions + 1) {
            result.add(notAnsweredQuestions.get(numberOfAddedQuestions++));
        }

        // add notanswered questions if there is not enough answered questions
        // it is ok because the total id of available questions > numberOfQuestions
        while (studentAnsweredQuestions.size() + numberOfAddedQuestions < numberOfQuestions) {
            result.add(notAnsweredQuestions.get(numberOfAddedQuestions++));
        }

        // add answered questions
        Random rand = new Random(System.currentTimeMillis());
        while (numberOfAddedQuestions < numberOfQuestions) {
            int next = rand.nextInt(studentAnsweredQuestions.size());
            if (!result.contains(studentAnsweredQuestions.get(next))) {
                result.add(studentAnsweredQuestions.get(next));
                numberOfAddedQuestions++;
            }
        }

        return result;
    }

    public Set<ClarificationAnswer> getClarificationAnswers() {
        return clarificationAnswers;
    }

    public void setClarificationAnswers(Set<ClarificationAnswer> clarificationAnswers) {
        this.clarificationAnswers = clarificationAnswers;
    }

    public void setStudentTournamentRegistrations(Set<StudentTournamentRegistration> studentTournamentRegistrations) {
        this.studentTournamentRegistrations = studentTournamentRegistrations;
    }

    public void addClarificationAnswer(ClarificationAnswer clarificationAnswer){
        this.clarificationAnswers.add(clarificationAnswer);
    }

    public void removeClarificationAnswer(ClarificationAnswer clarificationAnswer){
        this.clarificationAnswers.remove(clarificationAnswer);
    }

    public Set<Notification> getNotifications() { return notifications; }

    public void setNotifications(Set<Notification> notifications) { this.notifications = notifications; }

    public void addNotification(Notification notification) {
        this.notifications.add(notification);
    }

    public void removeNotification(Notification notification) {
        this.notifications.remove(notification);
    }

    public void removeAllNotifications() {
        this.notifications = new HashSet<>();
    }
}
