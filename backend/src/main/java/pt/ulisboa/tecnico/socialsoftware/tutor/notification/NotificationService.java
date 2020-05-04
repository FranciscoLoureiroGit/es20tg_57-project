package pt.ulisboa.tecnico.socialsoftware.tutor.notification;

import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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
    private static JavaMailSender sender;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void sendEmail(NotificationDto notificationDto) {
        Notification notification = notificationRepository.findById(notificationDto.getId()).orElseThrow(() ->
                new TutorException(NOTIFICATION_NOT_FOUND, notificationDto.getId()));

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(notification.getUser().getEmail());
            helper.setText(EMAIL_HEADER + notification.getDescription());
            helper.setSubject(SUBJECT_HEADER + notification.getTitle());
        } catch (MessagingException e) {
            throw new TutorException(MAIL_ERROR);
        }
        sender.send(message);
    }

    /**
     * Method is used for checking pending notifications and sending them to the user if the time is right,
     * it can also call other services by itself
     * @apiNote  This is a highly cpu-bound process and should be done every 10min
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
            if (notification.getTimeToDeliver().isBefore(LocalDateTime.now())) {
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
        sendEmail(new NotificationDto(notification));
        notification.setStatus(Notification.Status.DELIVERED);
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
            if (!notificationDto.getStatus().equals(Notification.Status.PENDING.name())) {
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
    public void deleteNotification(NotificationDto notificationDto, Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new TutorException(USER_NOT_FOUND, userId));

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

        // Converts set to list
        List<Notification> notifications = new ArrayList<>(user.getNotifications());

        // Removes all active user notifications (non-pending)
        user.removeAllNotifications();
        for (Notification notification : notifications) {
            notificationRepository.delete(notification);
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void changeNotificationStatus(Integer notificationId, Notification.Status status) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() ->
                new TutorException(NOTIFICATION_NOT_FOUND, notificationId));
        notification.setStatus(status);
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
        if(notificationDto.getTitle().equals("") || notificationDto.getDescription().equals("") ||
                notificationDto.getTimeToDeliver().equals("")){
            throw new TutorException(NOTIFICATION_MISSING_DATA);
        } else if (LocalDateTime.parse(notificationDto.getTimeToDeliver(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).isBefore(LocalDateTime.now())) {
            throw new TutorException(NOTIFICATION_DELIVER_DATE_INVALID);
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