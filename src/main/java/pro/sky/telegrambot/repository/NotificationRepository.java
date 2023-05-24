package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
