package pt.ulisboa.tecnico.socialsoftware.tutor.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.dto.NotificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/user/notifications")
    public List<NotificationDto> getUserNotifications(Principal principal) {
        return notificationService.getUserNotifications(((User)((Authentication)principal).getPrincipal()).getId());
    }

    @PutMapping("/notifications/change-status")
    public void changeNotificationStatus(@Valid @RequestBody NotificationDto notificationDto) {
        notificationService.changeNotificationStatus(notificationDto);
    }

    @PostMapping("/notifications/delete")
    public void deleteNotification(@Valid @RequestBody NotificationDto notificationDto) {
        notificationService.deleteNotification(notificationDto);
    }

    @DeleteMapping("/notifications/delete-all")
    public void deleteAllUserNotifications(Principal principal) {
        notificationService.deleteAllUserActiveNotifications(((User)((Authentication)principal).getPrincipal()).getId());
    }


}
