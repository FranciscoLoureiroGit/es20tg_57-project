package pt.ulisboa.tecnico.socialsoftware.tutor.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.dto.NotificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
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

    @PostMapping("/notifications/create-many")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<NotificationDto> createManyNotifications(@Valid @RequestBody List<NotificationDto> notifications) {
        List<NotificationDto> notificationDtos = new ArrayList<>();
        for (NotificationDto notificationDto : notifications) {
            notificationDtos.add(notificationService.createNotification(notificationDto, notificationDto.getUsername()));
        }
        return notificationDtos;
    }

    @PostMapping("/notifications/create-one")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public NotificationDto createNotification(@Valid @RequestBody NotificationDto notificationDto) {
        return notificationService.createNotification(notificationDto, notificationDto.getUsername());
    }
}
