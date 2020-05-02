package pt.ulisboa.tecnico.socialsoftware.tutor.notification.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.notification.domain.Notification;

public class NotificationDto {
    private Integer id;
    private String status;
    private String title;
    private String description;
    private String userId;
    private String creationDate;
    private String timeToDeliver;

    public NotificationDto() {}

    public NotificationDto(Notification notification) {
        this.id = notification.getId()
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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
