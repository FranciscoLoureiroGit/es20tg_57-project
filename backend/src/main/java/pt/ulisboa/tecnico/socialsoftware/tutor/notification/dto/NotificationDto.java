package pt.ulisboa.tecnico.socialsoftware.tutor.notification.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.notification.domain.Notification;

import java.time.format.DateTimeFormatter;

public class NotificationDto {
    private Integer id;
    private String status;
    private String title;
    private String description;
    private Integer userId;
    private String creationDate;
    private String timeToDeliver;
    private boolean urgent = false;

    public NotificationDto() {}

    public NotificationDto(String title, String description, String status, Integer userId) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.userId = userId;
    }

    public NotificationDto(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.description = notification.getDescription();
        this.userId = notification.getUser().getId();

        if (notification.getStatus() != null)
            this.status = notification.getStatus().name();
        if (notification.getCreationDate() != null)
            this.creationDate = notification.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        if (notification.getTimeToDeliver() != null)
            this.timeToDeliver = notification.getTimeToDeliver().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getTimeToDeliver() {
        return timeToDeliver;
    }

    public void setTimeToDeliver(String timeToDeliver) {
        this.timeToDeliver = timeToDeliver;
    }
}
