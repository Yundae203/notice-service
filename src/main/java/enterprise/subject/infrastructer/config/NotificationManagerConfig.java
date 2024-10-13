package enterprise.subject.infrastructer.config;

import enterprise.subject.infrastructer.notice.controller.NotificationManager;
import enterprise.subject.infrastructer.notice.model.ProductUserNotificationHistory;
import enterprise.subject.infrastructer.notice.repository.ProductUserNotificationHistoryRepository;
import enterprise.subject.infrastructer.notice.service.Consumer;
import enterprise.subject.infrastructer.notice.service.Producer;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.*;

@Configuration
@RequiredArgsConstructor
public class NotificationManagerConfig {

    private static final int REQUESTS_PER_SECOND = 500;
    private final ProductUserNotificationHistoryRepository productUserNotificationHistoryRepository;

    @Bean
    public NotificationManager notificationManager() {
        return new NotificationManager(consumer(), producer(), productsId());
    }

    @Bean
    public Producer producer() {
        return new Producer(noticeDeque(), productUserNotificationHistoryRepository);
    }

    @Bean
    public Consumer consumer() {
        return new Consumer(notificationBucket(), noticeDeque(), productsId(), productUserNotificationHistoryRepository);
    }

    @Bean
    public Bucket notificationBucket() {
        Bandwidth limit = Bandwidth.classic(REQUESTS_PER_SECOND, Refill.intervally(REQUESTS_PER_SECOND, Duration.ofSeconds(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Bean
    public BlockingDeque<ProductUserNotificationHistory> noticeDeque() {
        return new LinkedBlockingDeque<>();
    }

    @Bean
    public ConcurrentHashMap<Long, Integer> productsId() {
        return new ConcurrentHashMap<>();
    }
}
