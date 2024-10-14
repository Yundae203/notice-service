package enterprise.subject.application.relation.subscription;

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
                .build()
        );
    }

    public List<Long> findSubscriberByProductId(Long productId) {
        return productUserNotificationRepository.findByProductId(productId).stream()
                .map(ProductUserNotification::getUserId)
                .toList();
    }
}
