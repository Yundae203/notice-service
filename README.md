# 핵심 코드

## Bucket과 생산/소비 패턴을 활용한 알림 처리 기능

![image](https://github.com/user-attachments/assets/9c267c9a-814b-4a37-a43e-635691ec7871)


알림 처리는 위와 같은 형식으로 로직이 처리된다.

`NotificationManager`가 요청을 받는 역할을 수행하고

요청을 받게되면 이를 `Producer`에게 전달한다.

`Producer`는 받은 요청을 `BlockingDeque`에 저장한다.

그럼 이를 `Consumer`가 처리하여 요청을 처리하는 로직이다.

상품이 만약 `SOLD_OUT` 되게 되면 `NotificationManager`가 이를 감지하고

`ConcurrentHashMap`에서 알림을 발송 중이던 상품의 상태를 변경하여 알림 발송을 중단하도록 구현했다.

### NotificaitonManager

```java
public class NotificaitonManager {

		// 의존 관계 .... 추가 메서드들

    @PostConstruct
    public void init() {
        new Thread(consumer).start(); // consumer 실행
    }

    public void notifyRestock(NotificationRequest request) {
        // productId와 상태를 conusmer와 공유하는 HashMap에 저장
        productIds.put(request.productId(), NotificationStatus.IN_PROGRESS);

        // 알람 객체를 생성하여 Producer 를 통해 Consumer 에게 전달
        toModel(request).forEach(producer::produce);
    }

    public void soldOut(Long productId) {
		    // SOLD_OUT 처리
        if (productIds.contains(productId)) {
            productIds.put(productId, NotificationStatus.CANCELLED_BY_SOLD_OUT); 
        }
    }
}
```

### Producer

```java
public class Producer {

    private final BlockingDeque<ProductNotification> queue; // consumer와 공유

    public Producer(BlockingDeque<ProductNotification> queue) {
        this.queue = queue;
    }

    public void produce(ProductNotification notice) {
        queue.offer(notice); // consumer 에게 전달
    }
}
```

### Consumer

```java
public class Consumer implements Runnable {

		private final Bucket bucket;
    private final BlockingDeque<ProductNotification> queue;
    private final ConcurrentHashMap<Long, NotificationStatus> productsId;
    
    @Override
    public void run() {

        while (true) {
            try {
                notice = queue.take();
                currentId = notice.getProductId();

                // 매진 여부 확인하여 처리 토큰 소모 X
                if (isSOLD_OUT(currentId)) {
                    // SOLD_OUT
                    log.info("SOLD_OUT");
                    saveHistory(notice, NotificationStatus.CANCELLED_BY_SOLD_OUT); // 매진 처리
                    productsId.put(notice.getProductId(), NotificationStatus.PASS);
                }

                // bucket 사용하여 처리율 제한
                if (bucket.tryConsume(1) && isIN_PROGRESS(currentId)) {
                    // COMPLETE
                    saveUserNotification(notice); // Product_User_Notification 저장

                    if (isLastNotice(notice)) { // 마지막 요청일 경우 요청 완료로 저장
                        log.info("COMPLETE lastNotice = {}", notice);
                        saveHistory(notice, NotificationStatus.COMPLETED);
                    }

                } else if (isIN_PROGRESS(currentId)) { // 토큰이 부족할 시
                    log.info("token is empty");
                    queue.addFirst(notice);
                }

                // 마지막 요청일 경우 HashMap 에서 key 제거
                if (isLastNotice(notice)) {
                    productsId.remove(notice.getProductId());
                }

            } catch (Exception e) {
                log.info("ERROR");
                if (isIN_PROGRESS(currentId)) {
                    productsId.put(notice.getProductId(), NotificationStatus.CANCELLED_BY_ERROR);
                    saveHistory(notice, NotificationStatus.CANCELLED_BY_ERROR);
                }
            }
        }
    }    
```

## 비즈니스 요구 사항

- 재입고 알림을 전송하기 전, 상품의 재입고 회차를 1 증가 시킨다.
    - 실제 서비스에서는 다른 형태로 관리하지만, 과제에서는 직접 관리한다.

<aside>
📢

**분석**

상품은 재입고를 회차별로 관리하며 각 회차별로 알림이 전송된다.

따라서, 알림은 재입고 회차에 대한 정보를 알고 있어야 한다.

**의사 결정**

상품이 재입고 되었을 때, 상품의 재입고 회차를 증가시키고, 알림을 보낼 때 해당 정보를 같이 전송하여 알림 정보에 회차 정보가 같이 포함되게 한다.

</aside>

---

- 상품이 재입고 되었을 때, 재입고 알림을 설정한 유저들에게 알림 메시지를 전달해야 한다.
- 재입고 알림은 재입고 알림을 설정한 유저 순서대로 메시지를 전송한다.

<aside>
📢

**분석**

유저는 상품을 구독할 수 있다. 이는 유저와 상품이 1 대 1 관계를 갖음을 알 수 있다.

상품을 재입고 알림 설정한 유저 순서대로 메시지를 전송해야 한다. 

`상품유저알림` 테이블을 수정 일자 필드를 명시하고 있다.

이는 상품의 알림을 구독하고 취소할 때마다 변경이 일어난다.

따라서, 생성일자가 아닌 업데이트 일자로 알림 설정 순서를 명시함을 알 수 있다.

**의사 결정**

특정 상품을 구독한 유저들을 리스트로 조회할 때, 수정일자를 기준으로 정렬

</aside>

---

- 재입고 알림을 보내던 중 재고가 모두 없어진다면 알림 보내는 것을 중단합니다.

<aside>
📢

**분석**

누군가 상품을 구매해서 `stock`이 0이 되면 알림을 보내던 것을 중단해야 한다.

**의사 결정**

알림을 관리하는 `알림 매니저` 클래스를 만들고 상품이 0이 되는 시점에 이벤트를 받아야 한다.

이벤트를 받기 위해서는 상품의 상태를 관찰/구독 해야 한다.

도메인을 순수하게 유지하고 싶기 때문에 상품 내부에 `알림 매니저`를 넣는 것은 지양하고 상품 서비스를 감싸고 있는 `상위 서비스`를 만들어서 해당 서비스 내부에서 상태를 관찰하고자 한다.

</aside>

---

- 재입고 알림 전송의 상태를 DB 에 저장해야 한다.
    - IN_PROGRESS (발송 중)
    - CANCELED_BY_SOLD_OUT (품절에 의한 발송 중단)
    - CANCELED_BY_ERROR (예외에 의한 발송 중단)
    - COMPLETED (완료)

<aside>
📢

**분석**

알림의 상태는 4가지로 분류된다.

**의사 결정**

알림을 발송하기 전에 최초 DB에 알림의 상태를 발송중으로 저장하고,

이벤트의 결과에 따라 상태를 다르게 업데이트한다.

</aside>

---

- 회차별 재입고 알림을 받은 유저 목록을 저장해야 한다.

<aside>
📢

**분석**

알림을 받았다는 것은 알림의 상태가 `COMPLETE` 되었다는 것을 명시한다.

즉, 상태가 `COPLETE`인 유저들을 모두 저장해야 한다.

**의사 결정**

알림을 보내는 클래스에서 해당 유저의 상태가 `COMPLETE`임이 보장이 되면 유저 목록을 저장한다.

</aside>

---

## 기술적 요구 사항

- 알림 메시지는 1초에 최대 500개의 요청을 보낼 수 있다.
    - 서드 파티 연동을 하진 않고, ProductNotificationHistory 테이블에 데이터를 저장한다.

<aside>
📢

**분석**

전체 어플리케이션은 알림은 1초에 500개로 제한된다.

이는 처리율 제한 장치를 마련해야 한다는 것을 의미한다.

**의사 결정**

처리율 제한 알고리즘은 버켓 방식과 윈도우 방식이 있다.

윈도우 방식은 구현이 어렵고 일반 윈도우는 정확하게 요청을 500개로 제한할 수 없어 윈도우 방식은 제외하였다.

버켓 방식으로 구현되어 있는 처리율 제한 장치는 대표적으로

- Bucket4j
- Guava
- Reslilence4j

정도가 있다.

이 중에서 Bucket4j가 가장 구현 난이도가 낮고 인터페이스가 직관적이며 오로지 Bucket을 위한 라이브러리라고 생각하여 Bucket4j를 선택하게 되었다.

</aside>

---
