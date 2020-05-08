package pt.ulisboa.tecnico.socialsoftware.tutor.notification;

public enum NotificationMessages {

    EXTRA_CLARIFICATION_TITLE("Student started a discussion"),
    EXTRA_CLARIFICATION_DESCRIPTION("A student has added additional clarification request on a clarification you've recently answered"),
    CLARIFICATION_PUBLIC_TITLE("Clarification made public"),
    CLARIFICATION_PUBLIC_DESCRIPTION("A teacher has considered your clarification relevant and therefore made it public."),
    CLARIFICATION_PRIVATE_TITLE("Clarification made private"),
    CLARIFICATION_PRIVATE_DESCRIPTION("A teacher has put your clarification request back to private");


    public final String label;

    NotificationMessages(String label) {
        this.label = label;
    }
}

