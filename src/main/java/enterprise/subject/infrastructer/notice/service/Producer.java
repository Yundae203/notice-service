package enterprise.subject.infrastructer.notice.service;

import enterprise.subject.infrastructer.notice.model.ProductUserNotificationHistory;
import enterprise.subject.infrastructer.notice.repository.ProductUserNotificationHistoryRepository;

import java.util.concurrent.BlockingDeque;

public class Producer {

    private final BlockingDeque<ProductUserNotificationHistory> queue;
    private final ProductUserNotificationHistoryRepository repository;

    public Producer(BlockingDeque<ProductUserNotificationHistory> queue, ProductUserNotificationHistoryRepository repository) {
        this.queue = queue;
        this.repository = repository;
    }

    public void produce(ProductUserNotificationHistory history) {
        repository.save(history); // IN_PROGRESS 상태로 저장
        queue.offer(history); // consumer 에게 전달
    }
}
