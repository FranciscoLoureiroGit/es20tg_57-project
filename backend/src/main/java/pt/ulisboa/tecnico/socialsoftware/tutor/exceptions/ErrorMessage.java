package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

public enum ErrorMessage {
    QUIZ_NOT_FOUND("Quiz not found with id %d"),
    QUIZ_QUESTION_NOT_FOUND("Quiz question not found with id %d"),
    QUIZ_ANSWER_NOT_FOUND("Quiz answer not found with id %d"),
    QUESTION_ANSWER_NOT_FOUND("Question answer not found with id %d"),
    OPTION_NOT_FOUND("Option not found with id %d"),
    QUESTION_NOT_FOUND("Question not found with id %d"),
    USER_NOT_FOUND("User not found with id %d"),
    TOPIC_NOT_FOUND("Topic not found with id %d"),
    ASSESSMENT_NOT_FOUND("Assessment not found with id %d"),
    TOPIC_CONJUNCTION_NOT_FOUND("Topic Conjunction not found with id %d"),
    COURSE_EXECUTION_NOT_FOUND("Course execution not found with id %d"),

    COURSE_NOT_FOUND("Course not found with name %s"),
    COURSE_NAME_IS_EMPTY("The course name is empty"),
    COURSE_TYPE_NOT_DEFINED("The course type is not defined"),
    COURSE_EXECUTION_ACRONYM_IS_EMPTY("The course execution acronym is empty"),
    COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY("The course execution academic term is empty"),
    CANNOT_DELETE_COURSE_EXECUTION("The course execution cannot be deleted %s"),
    USERNAME_NOT_FOUND("Username %s not found"),

    CLARIFICATION_TITLE_IS_EMPTY("The clarification title is empty %d"),
    CLARIFICATION_DESCRP_IS_EMPTY("The clarification description is empty %d"),
    CLARIFICATION_MISSING_DATA("Clarification data not consistent %d"),
    CLARIFICATION_NOT_FOUND("Clarification id was not found %d"),
    CLARIFICATION_USER_NOT_ALLOWED("The specified user is not allowed %d"),
    CLARIFICATION_EXISTS("User has already created a clarification for this question answer"),

    QUIZ_USER_MISMATCH("Quiz %s is not assigned to student %s"),
    QUIZ_MISMATCH("Quiz Answer Quiz %d does not match Quiz Question Quiz %d"),
    QUESTION_OPTION_MISMATCH("Question %d does not have option %d"),
    COURSE_EXECUTION_MISMATCH("Course Execution %d does not have quiz %d"),

    DUPLICATE_TOPIC("Duplicate topic: %s"),
    DUPLICATE_USER("Duplicate user: %s"),
    DUPLICATE_COURSE_EXECUTION("Duplicate course execution: %s"),

    USERS_IMPORT_ERROR("Error importing users: %s"),
    QUESTIONS_IMPORT_ERROR("Error importing questions: %s"),
    TOPICS_IMPORT_ERROR("Error importing topics: %s"),
    ANSWERS_IMPORT_ERROR("Error importing answers: %s"),
    QUIZZES_IMPORT_ERROR("Error importing quizzes: %s"),

    QUESTION_IS_USED_IN_QUIZ("Question is used in quiz %s"),
    QUIZ_NOT_CONSISTENT("Field %s of quiz is not consistent"),
    USER_NOT_ENROLLED("%s - Not enrolled in any available course"),
    QUIZ_NO_LONGER_AVAILABLE("This quiz is no longer available"),
    QUIZ_NOT_YET_AVAILABLE("This quiz is not yet available"),

    QUESTIONSTOURNAMENT_NOT_CONSISTENT("Field %s of questions tournament is not consistent"),
    TOPIC_IN_COURSE_NOT_FOUND("Topic in specific course not found with id %d"),

    NO_CORRECT_OPTION("Question does not have a correct option"),
    NOT_ENOUGH_QUESTIONS("Not enough questions to create a quiz"),
    QUESTION_MISSING_DATA("Missing information for quiz"),
    QUESTION_MULTIPLE_CORRECT_OPTIONS("Questions can only have 1 correct option"),
    QUESTION_CHANGE_CORRECT_OPTION_HAS_ANSWERS("Can not change correct option of answered question"),
    QUIZ_HAS_ANSWERS("Quiz already has answers"),
    QUIZ_ALREADY_COMPLETED("Quiz already completed"),
    QUIZ_ALREADY_STARTED("Quiz was already started"),
    QUIZ_QUESTION_HAS_ANSWERS("Quiz question has answers"),
    FENIX_ERROR("Fenix Error"),
    AUTHENTICATION_ERROR("Authentication Error"),
    FENIX_CONFIGURATION_ERROR("Incorrect server configuration files for fenix"),

    //NEW
    QUESTION_DISABLED_WITHOUT_JUSTIFICATION("Question Disabled without a justification"),

    ACCESS_DENIED("You do not have permission to view this resource"),
    NOT_ALLOWED_CREATE_QUESTION("You do not have Role enough to create questions"),

    CANNOT_OPEN_FILE("Cannot open file"),

    USER_NOT_STUDENT("User is not a student"),
    STUDENT_NOT_ON_COURSE_EXECUTION("Student is not on the course execution"),
    TOURNAMENT_ENDED("Tournament already ended"),
    TOURNAMENT_ALREADY_STARTED("Tournament already started"),
    TOURNAMENT_NOT_EXIST("Tournament doesn't exist"),
    TOURNAMENT_NOT_FOUND("Tournament not found with id %d"),
    DUPLICATED_REGISTRATION("User already registered in tournament"),
    NULLID("null userId or questionsTournamentId"),
    NO_CLARIFICATION_REQUEST("No such Clarification Request"),
    NO_CLARIFICATION_ANSWER("Clarification answer is empty"),
    CANNOT_ANSWER_CLARIFICATION("User is not allowed to answer this request"),
    NO_SUCH_USER("No such user"),
    ALREADY_HAS_ANSWER("Clarification already has an answer");
    ALREADY_HAS_ANSWER("Clarification already has an answer"),
    NULL_CLARIFICATION_ANSWER_INPUT("Clarification answer details are empty");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}