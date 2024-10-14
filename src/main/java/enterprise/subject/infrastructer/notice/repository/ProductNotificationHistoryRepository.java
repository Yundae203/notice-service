package enterprise.subject.infrastructer.notice.repository;

import enterprise.subject.infrastructer.notice.model.ProductNotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductNotificationHistoryRepository extends JpaRepository<ProductNotificationHistory, Long> {
}
