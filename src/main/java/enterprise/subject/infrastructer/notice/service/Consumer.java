package enterprise.subject.infrastructer.notice.service;

import enterprise.subject.infrastructer.notice.dto.ProductNotification;
import enterprise.subject.infrastructer.notice.model.NotificationStatus;
import enterprise.subject.infrastructer.notice.model.ProductNotificationHistory;
import enterprise.subject.infrastructer.notice.repository.ProductNotificationHistoryRepository;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Consumer implements Runnable {

    private final BlockingDeque<ProductNotification> queue;
    private final Bucket bucket;
    private final ConcurrentHashMap<Long, NotificationStatus> productsId;
    private final ProductNotificationHistoryRepository repository;

    public Consumer(
            Bucket bucket,
            BlockingDeque<ProductNotification> queue,
            ConcurrentHashMap<Long, NotificationStatus> productsId,
            ProductNotificationHistoryRepository repository
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
                ProductNotification notice = queue.take();

                // 매진 여부 확인하여 처리 토큰 소모 X
                if (isSOLD_OUT(notice)) {
                    // SOLD_OUT
                    log.info("SOLD_OUT");
                    saveHistory(notice, NotificationStatus.CANCELLED_BY_SOLD_OUT); // 매진 처리
                    productsId.put(notice.getProductId(), NotificationStatus.PASS);
                    return;
                }

                // bucket 사용하여 처리율 제한
                if (bucket.tryConsume(1) && isIN_PROGRESS(notice)) {
                    // COMPLETE
                    if (isLastNotice(notice)) { // 마지막 요청일 경우 요청 완료로 저장
                        log.info("COMPLETE lastNotice = {}", notice);
                        saveHistory(notice, NotificationStatus.COMPLETED);
                    }

                } else if (isIN_PROGRESS(notice)) {
                    log.info("token is empty");
                    queue.addFirst(notice);
                }

                // 마지막 요청일 경우 HashMap 에서 key 제거
                if (isLastNotice(notice)) {
                    productsId.remove(notice.getProductId());
                }

            } catch (InterruptedException e) {
                log.info("interrupted");
            }
        }
    }

    private boolean isSOLD_OUT(ProductNotification notice) {
        return productsId.get(notice.getProductId()) == NotificationStatus.CANCELLED_BY_SOLD_OUT;
    }

    private boolean isIN_PROGRESS(ProductNotification notice) {
        return productsId.get(notice.getProductId()) == NotificationStatus.IN_PROGRESS;
    }

    private void saveHistory(ProductNotification notice, NotificationStatus status) {
        ProductNotificationHistory history = ProductNotificationHistory.builder()
                        .productId(notice.getProductId())
                        .restockVersion(notice.getReStockVersion())
                        .lastUser(notice.getUserId())
                        .build();

        history.changeStatus(status);

        repository.save(history);
    }

    private boolean isLastNotice(ProductNotification notice) {
        return notice.isLast();
    }
}
