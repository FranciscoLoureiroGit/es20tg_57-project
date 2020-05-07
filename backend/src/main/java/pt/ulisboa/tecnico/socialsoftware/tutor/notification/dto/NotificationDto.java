package pt.ulisboa.tecnico.socialsoftware.tutor.notification.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.notification.domain.Notification;

import java.time.format.DateTimeFormatter;

public class NotificationDto {
    private Integer id;
    private String status;
    private String title;
    private String description;
    private String username;
    private String creationDate;
    private boolean urgent = false;

    public NotificationDto() {}

    public NotificationDto(String title, String description, String status, String username) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.username = username;
    }

    public NotificationDto(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.description = notification.getDescription();
        this.username = notification.getUser().getUsername();

        if (notification.getStatus() != null)
            this.status = notification.getStatus().name();
        if (notification.getCreationDate() != null)
            this.creationDate = notification.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

}
