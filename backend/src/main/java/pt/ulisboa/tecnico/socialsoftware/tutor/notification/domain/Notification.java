package pt.ulisboa.tecnico.socialsoftware.tutor.notification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.notification.dto.NotificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "notifications")
public class Notification {
    public enum Status {
        PENDING, DELIVERED, READ
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "urgent")
    private Boolean urgent = false;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "time_to_deliver")
    private LocalDateTime timeToDeliver;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public Notification() {}

    public Notification(NotificationDto notificationDto) {
        this.id = notificationDto.getId();
        this.title = notificationDto.getTitle();
        this.description = notificationDto.getDescription();
        this.urgent = notificationDto.isUrgent();
        if (notificationDto.getStatus() != null) {
            this.status = Notification.Status.valueOf(notificationDto.getStatus());
            if (Notification.Status.valueOf(notificationDto.getStatus()) == Status.PENDING && notificationDto.getTimeToDeliver() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                this.timeToDeliver = LocalDateTime.parse(notificationDto.getTimeToDeliver(), formatter);
            }
        }
        else {
            this.status = Status.PENDING;
        }
    }

    // GETTERS AND SETTERS


    public Boolean getUrgent() {
        return urgent;
    }

    public void setUrgent(Boolean urgent) {
        this.urgent = urgent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getTimeToDeliver() {
        return timeToDeliver;
    }

    public void setTimeToDeliver(LocalDateTime timeToDeliver) {
        this.timeToDeliver = timeToDeliver;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}