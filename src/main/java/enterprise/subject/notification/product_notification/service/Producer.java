package enterprise.subject.notification.product_notification.service;

import enterprise.subject.notification.product_notification.dto.ProductNotification;
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
