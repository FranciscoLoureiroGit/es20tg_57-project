package pt.ulisboa.tecnico.socialsoftware.tutor.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.domain.Notification;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    @Query(value = "SELECT * FROM notifications n WHERE n.user_id = :userId", nativeQuery = true)
    List<Notification> findByUserId(int userId);

    @Query(value = "SELECT * FROM notifications n WHERE n.status = 'PENDING'", nativeQuery = true)
    List<Notification> findPending();

    @Modifying
    @Query(value = "DELETE FROM notifications n WHERE n.user_id = :userId", nativeQuery = true)
    void removeUserNotifications(int userId);
}
