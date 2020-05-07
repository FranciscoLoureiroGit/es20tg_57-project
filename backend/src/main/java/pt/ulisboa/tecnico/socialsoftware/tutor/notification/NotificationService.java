package pt.ulisboa.tecnico.socialsoftware.tutor.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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
    private static final String EMAIL_HEADER = "==== QUIZZES TUTOR NOTIFICATION SYSTEM ==== \n";
    private static final String SUBJECT_HEADER = "Quizzes Tutor - ";

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
     * Method is used for checking pending notifications and sending them to the user if the time is right,
     * it can also call other services by itself
     * @apiNote  This is a highly cpu-bound process and should be done every 5min
     * @see pt.ulisboa.tecnico.socialsoftware.tutor.config.ScheduledTasks
     * */
    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void checkNotifications() {
        // checks for pending notifications and sees if the time has arrived, if so, sends it
        List<Notification> notifications = notificationRepository.findPending();

        for (Notification notification: notifications) {
            if (notification.getTimeToDeliver() != null && notification.getTimeToDeliver().isBefore(LocalDateTime.now())) {
                // then the user can be notified
                sendNotification(notification);
            }
            // otherwise the notification stays pending
        }
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
            sendEmail(new NotificationDto(notification));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public NotificationDto createNotification(NotificationDto notificationDto, Integer userId) {
        if (userId != null) {
            User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
            checkInput(notificationDto);
            Notification notification = setNotificationValues(notificationDto, user);
            notificationRepository.save(notification);
            if (!notificationDto.getStatus().equals(Notification.Status.PENDING.name()) && notificationDto.getTimeToDeliver() == null) {
                // SendNotification if not pending
                sendNotification(notification);
            }
            return new NotificationDto(notification);
        }
        throw new TutorException(NOTIFICATION_MISSING_DATA);

    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteNotification(NotificationDto notificationDto) {
        if (notificationDto.getUserId() == null)
            throw new TutorException(NOTIFICATION_MISSING_DATA);

        User user = userRepository.findById(notificationDto.getUserId()).orElseThrow(() ->
                new TutorException(USER_NOT_FOUND, notificationDto.getUserId()));

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
                .stream().filter(no -> !no.getStatus().equals(Notification.Status.PENDING)).map(NotificationDto::new).collect(Collectors.toList());
    }

    private void checkInput(NotificationDto notificationDto) {
        if(notificationDto.getTitle().equals("") || notificationDto.getDescription().equals("")){
            throw new TutorException(NOTIFICATION_MISSING_DATA);
        } else if (notificationDto.getTimeToDeliver() != null && LocalDateTime.parse(notificationDto.getTimeToDeliver(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).isBefore(LocalDateTime.now())) {
            throw new TutorException(NOTIFICATION_DELIVER_DATE_INVALID);
        } else if (!(notificationDto.getStatus().equals(Notification.Status.PENDING.name()) ||
                notificationDto.getStatus().equals(Notification.Status.DELIVERED.name()))) {
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