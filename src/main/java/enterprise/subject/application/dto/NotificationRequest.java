package enterprise.subject.application.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class NotificationRequest {
    private Long productId;
    private Integer restockVersion;
    private List<Long> users;
}