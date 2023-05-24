package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.Notification;
import pro.sky.telegrambot.repository.NotificationRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private TelegramBot telegramBot;
    private NotificationRepository notificationRepository;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationRepository notificationRepository) {
        this.telegramBot = telegramBot;
        this.notificationRepository = notificationRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            String msg = update.message().text();
            if (msg.equals("/start")) {
                SendMessage message = new SendMessage(
                        update.message().chat().id(),
                        String.format("Привет, %s! Напиши задачу в формате dd.MM.yyyy HH:mm Задача", update.message().chat().firstName())
                );
                telegramBot.execute(message);
            }

            Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W\\w+]+)");
            Matcher matcher = pattern.matcher(msg);
            if (matcher.matches()) {
                String dateTimeStr = msg.substring(0, 16);
                String text = msg.substring(17);
                System.out.println(dateTimeStr);
                LocalDateTime dateTime;
                try {
                    dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                    System.out.println(text);
                    Notification notification = new Notification(update.message().chat().id(), dateTime, text);
                    notificationRepository.save(notification);
                    logger.info("Напоминание {} успешно сохранено", notification);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
