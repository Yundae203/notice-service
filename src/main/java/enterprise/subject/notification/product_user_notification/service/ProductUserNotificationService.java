package enterprise.subject.notification.product_user_notification.service;

import enterprise.subject.notification.product_user_notification.repository.ProductUserNotificationRepository;
import enterprise.subject.notification.product_user_notification.model.ProductUserNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductUserNotificationService {

    private final ProductUserNotificationRepository productUserNotificationRepository;

    @Transactional
    public void subscribe(Long productId, Long userId) {
        productUserNotificationRepository.save(
                ProductUserNotification.builder()
                        .productId(productId)
                        .userId(userId)
                        .active(true)
                        .build()
        );
    }

    @Transactional
    public void unsubscribe(Long productId, Long userId) {
        ProductUserNotification model = productUserNotificationRepository.findByProductIdAndUserId(productId, userId);
        model.changeActive();
    }

    public List<Long> findSubscribersByProductId(Long productId) {
        return productUserNotificationRepository.findByProductIdOrderByUpdateAtAsc(productId).stream()
                .map(ProductUserNotification::getUserId)
                .toList();
    }
}
