package enterprise.subject.application.service;

import enterprise.subject.application.dto.NotificationRequest;
import enterprise.subject.application.relation.subscription.ProductUserNotificationService;
import enterprise.subject.domain.product.model.Product;
import enterprise.subject.domain.product.service.ProductService;
import enterprise.subject.infrastructer.notice.controller.NotificationManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StoreService {

    private final ProductService productService;
    private final ProductUserNotificationService productUserNotificationService;
    private final NotificationManager notificationManager;

    public void restock(Long productId, Integer quantity) {
        Product product = productService.findById(productId);
        product.reStock(quantity);
        productService.save(product);

        List<Long> subscribers = productUserNotificationService.findSubscriberByProductId(productId);

        NotificationRequest request = new NotificationRequest(productId, product.getReStockVersion(), subscribers);
        notificationManager.notifyRestock(request);
    }

    public void sell(Long productId, Integer quantity) {
        Product product = productService.findById(productId);
        product.sell(quantity);
        productService.save(product);

        if (product.isSoldOUt()) {
            notificationManager.soldOut(productId);
        }
    }
}
