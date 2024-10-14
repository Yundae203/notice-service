package enterprise.subject.notification.product_notification.config;

import enterprise.subject.notification.product_notification.controller.NotificationManager;
import enterprise.subject.notification.product_notification.dto.ProductNotification;
import enterprise.subject.notification.product_notification.model.NotificationStatus;
import enterprise.subject.notification.product_notification.repository.ProductNotificationHistoryRepository;
import enterprise.subject.notification.product_notification.service.Consumer;
import enterprise.subject.notification.product_notification.service.Producer;
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
    private final ProductNotificationHistoryRepository productNotificationHistoryRepository;

    @Bean
    public NotificationManager notificationManager() {
        return new NotificationManager(consumer(), producer(), productsId());
    }

    @Bean
    public Producer producer() {
        return new Producer(noticeDeque());
    }

    @Bean
    public Consumer consumer() {
        return new Consumer(notificationBucket(), noticeDeque(), productsId(), productNotificationHistoryRepository);
    }

    @Bean
    public Bucket notificationBucket() {
        Bandwidth limit = Bandwidth.classic(REQUESTS_PER_SECOND, Refill.intervally(REQUESTS_PER_SECOND, Duration.ofSeconds(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Bean
    public BlockingDeque<ProductNotification> noticeDeque() {
        return new LinkedBlockingDeque<>();
    }

    @Bean
    public ConcurrentHashMap<Long, NotificationStatus> productsId() {
        return new ConcurrentHashMap<>();
    }
}
