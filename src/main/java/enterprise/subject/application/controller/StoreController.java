package enterprise.subject.application.controller;

import enterprise.subject.application.dto.RestockRequest;
import enterprise.subject.application.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/products/{productId}/notifications/re-stock")
    public ResponseEntity<Object> reStock(
            @PathVariable("productId") Long productId,
            @RequestBody RestockRequest request
            ) {
        storeService.restock(productId, request.quantity());
        return ResponseEntity.ok().build();
    }

}
