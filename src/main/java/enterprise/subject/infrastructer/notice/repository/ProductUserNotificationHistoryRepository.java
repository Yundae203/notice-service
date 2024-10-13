package enterprise.subject.infrastructer.notice.repository;

import enterprise.subject.infrastructer.notice.model.ProductUserNotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductUserNotificationHistoryRepository extends JpaRepository<ProductUserNotificationHistory, Long> {
}
