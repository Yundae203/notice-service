package enterprise.subject.infrastructer.notice.controller;

import enterprise.subject.application.dto.NotificationRequest;
import enterprise.subject.infrastructer.notice.model.NotificationStatus;
import enterprise.subject.infrastructer.notice.model.ProductUserNotificationHistory;
import enterprise.subject.infrastructer.notice.service.Consumer;
import enterprise.subject.infrastructer.notice.service.Producer;
import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class NotificationManager {

    private final Consumer consumer;
    private final Producer producer;
    private final ConcurrentHashMap<Long, Integer> productIds;

    public NotificationManager(
            Consumer consumer,
            Producer producer,
            ConcurrentHashMap<Long, Integer> productIds
    ) {
        this.consumer = consumer;
        this.producer = producer;
        this.productIds = productIds;
    }

    @PostConstruct
    public void init() {
        new Thread(consumer).start(); // consumer 실행
    }

    public void notifyRestock(NotificationRequest request) {
        // SOLD_OUT 여부 판단을 위해 productId 저장
        productIds.put(request.getProductId(), request.getRestockVersion());

        toModel(request).forEach(producer::produce);
    }

    public List<ProductUserNotificationHistory> toModel(NotificationRequest request) {
        return request.getUsers().stream()
                .map(userId -> ProductUserNotificationHistory.builder()
                        .productId(request.getProductId())
                        .userId(userId)
                        .restockVersion(request.getRestockVersion())
                        .status(NotificationStatus.IN_PROGRESS)
                        .build())
                .collect(Collectors.toList());
    }

    public void soldOut(Long productId) {
        productIds.remove(productId); // SOLD_OUT 처리
    }
}
