package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

public enum ErrorMessage {

    INVALID_ACADEMIC_TERM_FOR_COURSE_EXECUTION("Invalid academic term for course execution"),
    INVALID_ACRONYM_FOR_COURSE_EXECUTION("Invalid acronym for course execution"),
    INVALID_CONTENT_FOR_OPTION("Invalid content for option"),
    INVALID_CONTENT_FOR_QUESTION("Invalid content for question"),
    INVALID_NAME_FOR_COURSE("Invalid name for course"),
    INVALID_NAME_FOR_TOPIC("Invalid name for topic"),
    INVALID_SEQUENCE_FOR_OPTION("Invalid sequence for option"),
    INVALID_SEQUENCE_FOR_QUESTION_ANSWER("Invalid sequence for question answer"),
    INVALID_TITLE_FOR_ASSESSMENT("Invalid title for assessment"),
    INVALID_TITLE_FOR_QUESTION("Invalid title for question"),
    INVALID_URL_FOR_IMAGE("Invalid url for image"),
    INVALID_TYPE_FOR_COURSE("Invalid type for course"),
    INVALID_TYPE_FOR_COURSE_EXECUTION("Invalid type for course execution"),
    INVALID_AVAILABLE_DATE_FOR_QUIZ("Invalid available date for quiz"),
    INVALID_CONCLUSION_DATE_FOR_QUIZ("Invalid conclusion date for quiz"),
    INVALID_RESULTS_DATE_FOR_QUIZ("Invalid results date for quiz"),
    INVALID_TITLE_FOR_QUIZ("Invalid title for quiz"),
    INVALID_TYPE_FOR_QUIZ("Invalid type for quiz"),
    INVALID_QUESTION_SEQUENCE_FOR_QUIZ("Invalid question sequence for quiz"),

    ASSESSMENT_NOT_FOUND("Assessment not found with id %d"),
    COURSE_EXECUTION_NOT_FOUND("Course execution not found with id %d"),
    OPTION_NOT_FOUND("Option not found with id %d"),
    QUESTION_ANSWER_NOT_FOUND("Question answer not found with id %d"),
    QUESTION_NOT_FOUND("Question not found with id %d"),
    QUIZ_ANSWER_NOT_FOUND("Quiz answer not found with id %d"),
    QUIZ_NOT_FOUND("Quiz not found with id %d"),
    QUIZ_QUESTION_NOT_FOUND("Quiz question not found with id %d"),
    TOPIC_CONJUNCTION_NOT_FOUND("Topic Conjunction not found with id %d"),
    TOPIC_NOT_FOUND("Topic not found with id %d"),
    USER_NOT_FOUND("User not found with id %d"),
    COURSE_NOT_FOUND("Course not found with name %s"),

    CANNOT_DELETE_COURSE_EXECUTION("The course execution cannot be deleted %s"),
    USERNAME_NOT_FOUND("Username %d not found"),

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
    USER_NOT_ENROLLED("%s - Not enrolled in any available course"),
    QUIZ_NO_LONGER_AVAILABLE("This quiz is no longer available"),
    QUIZ_NOT_YET_AVAILABLE("This quiz is not yet available"),

    QUESTIONSTOURNAMENT_NOT_CONSISTENT("Field %s of questions tournament is not consistent"),
    TOPIC_IN_COURSE_NOT_FOUND("Topic in specific course not found with id %d"),

    NO_CORRECT_OPTION("Question does not have a correct option"),
    NOT_ENOUGH_QUESTIONS("Not enough questions to create a quiz"),
    ONE_CORRECT_OPTION_NEEDED("Questions need to have 1 and only 1 correct option"),
    CANNOT_CHANGE_ANSWERED_QUESTION("Can not change answered question"),
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
    USER_NOT_TOURNAMENT_CREATOR("User can't delete tournament which didn't create"),
    TOURNAMENT_ENDED("Tournament already ended"),
    TOURNAMENT_ALREADY_STARTED("Tournament already started"),
    TOURNAMENT_NOT_EXIST("Tournament doesn't exist"),
    TOURNAMENT_NOT_FOUND("Tournament not found with id %d"),
    TOURNAMENT_NOT_AVAILABLE("Tournament not started or ended"),
    DUPLICATED_REGISTRATION("User already registered in tournament"),
    NULLID("null userId or questionsTournamentId"),
    NO_CLARIFICATION_REQUEST("No such Clarification Request"),
    NO_CLARIFICATION_ANSWER("Clarification answer is empty"),
    CANNOT_ANSWER_CLARIFICATION("User is not allowed to answer this request"),
    NO_SUCH_USER("No such user"),
    ALREADY_HAS_ANSWER("Clarification already has an answer"),
    NULL_CLARIFICATION_ANSWER_INPUT("Clarification answer details are empty");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}
