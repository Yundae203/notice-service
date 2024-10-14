package enterprise.subject.notification.product_notification.controller;

import enterprise.subject.application.dto.NotificationRequest;
import enterprise.subject.notification.product_notification.dto.ProductNotification;
import enterprise.subject.notification.product_notification.model.NotificationStatus;
import enterprise.subject.notification.product_notification.service.Consumer;
import enterprise.subject.notification.product_notification.service.Producer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class NotificationManager {

    private final Consumer consumer;
    private final Producer producer;
    private final ConcurrentHashMap<Long, NotificationStatus> productIds;

    public NotificationManager(
            Consumer consumer,
            Producer producer,
            ConcurrentHashMap<Long, NotificationStatus> productIds
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
        productIds.put(request.productId(), NotificationStatus.IN_PROGRESS);

        // 알람 객체를 생성하여 Producer 를 통해 Consumer 에게 전달
        toModel(request).forEach(producer::produce);
    }

    public List<ProductNotification> toModel(NotificationRequest request) {
        List<ProductNotification> models = request.users().stream()
                .map(userId -> ProductNotification.builder()
                        .productId(request.productId())
                        .reStockVersion(request.restockVersion())
                        .userId(userId)
                        .build())
                .collect(Collectors.toList());

        models.getLast().endElement(); // 마지막 요소 표시

        return models;
    }

    public void soldOut(Long productId) {
        if (productIds.contains(productId)) {
            productIds.put(productId, NotificationStatus.CANCELLED_BY_SOLD_OUT); // SOLD_OUT 처리
        }
    }
}
