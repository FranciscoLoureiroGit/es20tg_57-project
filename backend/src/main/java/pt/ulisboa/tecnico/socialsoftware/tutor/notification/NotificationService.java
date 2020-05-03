package pt.ulisboa.tecnico.socialsoftware.tutor.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.domain.Notification;
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.dto.NotificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.repository.NotificationRepository;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.MAIL_ERROR;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NOTIFICATION_NOT_FOUND;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private static JavaMailSender sender;

    public void sendEmail(NotificationDto notificationDto) {
        Notification notification = notificationRepository.findById(notificationDto.getId()).orElseThrow(() ->
                new TutorException(NOTIFICATION_NOT_FOUND, notificationDto.getId()));

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(notification.getUser().getEmail());
            helper.setText(notification.getDescription());
            helper.setSubject(notification.getTitle());
        } catch (MessagingException e) {
            throw new TutorException(MAIL_ERROR);
        }
        sender.send(message);
    }

    public void sendNotification(NotificationDto notificationDto) {

    }
}
