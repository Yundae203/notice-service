package enterprise.subject.infrastructer.notice.service;

import enterprise.subject.infrastructer.notice.dto.ProductNotification;
import enterprise.subject.infrastructer.notice.model.ProductNotificationHistory;
import enterprise.subject.infrastructer.notice.repository.ProductNotificationHistoryRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingDeque;

@Slf4j
public class Producer {

    private final BlockingDeque<ProductNotification> queue;

    public Producer(BlockingDeque<ProductNotification> queue) {
        this.queue = queue;
    }

    public void produce(ProductNotification notice) {
        queue.offer(notice); // consumer 에게 전달
    }
}
