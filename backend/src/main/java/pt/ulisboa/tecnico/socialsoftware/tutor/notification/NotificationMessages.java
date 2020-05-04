package pt.ulisboa.tecnico.socialsoftware.tutor.notification;

public enum NotificationMessages {

    // TODO IMPLEMENT THE CREATION OF THESE NOTIFICATIONS
    
    CLARIFICATION_CREATED_TITLE("New clarification request"),
    CLARIFICATION_CREATED_DESCRIPTION("Student %d has created a clarification request."),
    CLARIFICATION_ANSWERED_TITLE("Teacher answered your clarification request"),
    CLARIFICATION_ANSWERED_DESCRIPTION("The teacher has answered your clarification request."),
    QUESTION_PROPOSED_TITLE("Invalid academic term for course execution"),
    QUESTION_PROPOSED_DESCRIPTION("Invalid academic term for course execution"),
    QUESTION_PROPOSED_ACCEPTED_TITLE("Invalid academic term for course execution"),
    QUESTION_PROPOSED_ACCEPTED_DESCRIPTION("Invalid academic term for course execution"),
    TOURNAMENT_CREATED_TITLE("Invalid academic term for course execution"),
    TOURNAMENT_CREATED_DESCRIPTION("Invalid academic term for course execution"),
    TOURNAMENT_REGISTERED_TITLE("Invalid academic term for course execution"),
    TOURNAMENT_REGISTERED_DESCRIPTION("Invalid academic term for course execution"),
    CLARIFICATION_PUBLIC_TITLE("Invalid academic term for course execution"),
    CLARIFICATION_PUBLIC_DESCRIPTION("Invalid academic term for course execution"),
    CLARIFICATION_PRIVATE_TITLE("Invalid academic term for course execution"),
    CLARIFICATION_PRIVATE_DESCRIPTION("Invalid academic term for course execution");


    public final String label;

    NotificationMessages(String label) {
        this.label = label;
    }
}

