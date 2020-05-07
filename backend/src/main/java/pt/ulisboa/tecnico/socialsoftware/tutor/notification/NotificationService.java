package pt.ulisboa.tecnico.socialsoftware.tutor.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.domain.Notification;
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.dto.NotificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.repository.NotificationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class NotificationService {
    private static final String EMAIL_HEADER = "=========== QUIZZES TUTOR NOTIFICATION SYSTEM ========== \n\n";
    private static final String SUBJECT_HEADER = "Quizzes Tutor - ";
    private List<Notification> pendingEmails = new ArrayList<>();

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void sendEmail(NotificationDto notificationDto) {
        Notification notification = notificationRepository.findById(notificationDto.getId()).orElseThrow(() ->
                new TutorException(NOTIFICATION_NOT_FOUND, notificationDto.getId()));

        SimpleMailMessage message = new SimpleMailMessage();

        try {
            message.setTo(notification.getUser().getEmail());
            message.setText(EMAIL_HEADER + notification.getDescription());
            message.setSubject(SUBJECT_HEADER + notification.getTitle());
            mailSender.send(message);
        } catch (NullPointerException e) {
            throw new TutorException(MAIL_ERROR);
        }
    }

    /**
     * Method is used for checking pending emails and sending them to the user,
     * it can also call other services by itself
     * @apiNote  This is a highly cpu-bound process and should be done every 15min
     * @see pt.ulisboa.tecnico.socialsoftware.tutor.config.ScheduledTasks
     * */
    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void sendAllEmails() {
        // checks for pending emails ands sends them
        for (Notification notification: this.pendingEmails) {
                sendEmail(new NotificationDto(notification));
        }
        this.pendingEmails = new ArrayList<>();
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void sendNotification(Notification notification) {
        // adds notification to user and sends email
        User user = notification.getUser();
        user.addNotification(notification);
        notification.setStatus(Notification.Status.DELIVERED);
        notification.setCreationDate(LocalDateTime.now());

        // If is urgent notification, sends email
        if (notification.getUser().getEmail() != null && notification.getUrgent())
            this.pendingEmails.add(notification);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public NotificationDto createNotification(NotificationDto notificationDto, String username) {
        if (username != null) {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new TutorException(USER_NOT_FOUND, username);
            }
            checkInput(notificationDto);
            Notification notification = setNotificationValues(notificationDto, user);
            notificationRepository.save(notification);
            sendNotification(notification);
            return new NotificationDto(notification);
        }
        throw new TutorException(NOTIFICATION_MISSING_DATA);

    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteNotification(NotificationDto notificationDto) {
        if (notificationDto.getUsername() == null)
            throw new TutorException(NOTIFICATION_MISSING_DATA);

        User user = userRepository.findByUsername(notificationDto.getUsername());
        if (user == null) {
            throw new TutorException(USER_NOT_FOUND, notificationDto.getUsername());
        }
        Notification notification = notificationRepository.findById(notificationDto.getId()).orElseThrow(() ->
                new TutorException(NOTIFICATION_NOT_FOUND, notificationDto.getId()));

        user.removeNotification(notification);
        notificationRepository.delete(notification);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteAllUserActiveNotifications(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new TutorException(USER_NOT_FOUND, userId));

        List<Notification> notifications = new ArrayList<>(user.getNotifications());

        for (Notification notification : notifications) {
            this.deleteNotification(new NotificationDto(notification));
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void changeNotificationStatus(NotificationDto notificationDto) {
        Notification notification = notificationRepository.findById(notificationDto.getId()).orElseThrow(() ->
                new TutorException(NOTIFICATION_NOT_FOUND, notificationDto.getId()));
        notification.setStatus(Notification.Status.valueOf(notificationDto.getStatus()));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<NotificationDto> getUserNotifications(Integer userId) {
        return notificationRepository.findByUserId(userId)
                .stream().map(NotificationDto::new).collect(Collectors.toList());
    }

    private void checkInput(NotificationDto notificationDto) {
        if(notificationDto.getTitle().equals("") || notificationDto.getDescription().equals("")){
            throw new TutorException(NOTIFICATION_MISSING_DATA);
        } else if (!notificationDto.getStatus().equals(Notification.Status.DELIVERED.name())) {
            throw new TutorException(NOTIFICATION_STATUS_NOT_ALLOWED);
        }
    }

    private Notification setNotificationValues(NotificationDto notificationDto, User user) {
        // Creates the notification object and sets its values
        Notification notification = new Notification(notificationDto);
        notification.setCreationDate(LocalDateTime.now());
        notification.setUser(user);
        return notification;
    }

}