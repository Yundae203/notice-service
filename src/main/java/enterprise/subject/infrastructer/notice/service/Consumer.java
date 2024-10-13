package enterprise.subject.infrastructer.notice.service;

import enterprise.subject.infrastructer.notice.model.NotificationStatus;
import enterprise.subject.infrastructer.notice.model.ProductUserNotificationHistory;
import enterprise.subject.infrastructer.notice.repository.ProductUserNotificationHistoryRepository;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Consumer implements Runnable {

    private final BlockingDeque<ProductUserNotificationHistory> queue;
    private final Bucket bucket;
    private final ConcurrentHashMap<Long, Integer> productsId;
    private final ProductUserNotificationHistoryRepository repository;

    public Consumer(Bucket bucket,
                    BlockingDeque<ProductUserNotificationHistory> queue,
                    ConcurrentHashMap<Long, Integer> productsId,
                    ProductUserNotificationHistoryRepository repository
    ) {
        this.bucket = bucket;
        this.queue = queue;
        this.productsId = productsId;
        this.repository = repository;
    }

    @Override
    public void run() {

        while (true) {
            try {
                ProductUserNotificationHistory history = queue.take();

                // 매진 여부 확인하여 처리
                if (isSoldOut(history)) {
                    // SOLD_OUT
                    history.changeStatus(NotificationStatus.CANCELLED_BY_SOLD_OUT);
                    repository.save(history);
                    return;
                }

                // bucket 사용하여 처리율 제한
                if (bucket.tryConsume(1)) {
                    // COMPLETE
                    history.changeStatus(NotificationStatus.COMPLETED);
                    repository.save(history);
                } else {
                    log.info("token is empty");
                    queue.addFirst(history);
                }

            } catch (InterruptedException ignored) {

            }
        }
    }

    private boolean isSoldOut(ProductUserNotificationHistory history) {
        return !productsId.contains(history.getProductId());
    }
}
