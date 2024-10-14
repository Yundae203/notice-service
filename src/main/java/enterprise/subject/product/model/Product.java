package enterprise.subject.product.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    Integer reStockVersion = 0;
    @NotNull
    Long stock = 0L;

    @Builder
    public Product(
            Long id,
            Long stock,
            Integer reStockVersion
    ) {
        this.id = id;
        this.stock = stock;
        this.reStockVersion = reStockVersion;
    }

    public void reStock(int stock) {
        this.stock += stock;
        this.reStockVersion++;
    }

    public void sell(int stock) {
        if (this.stock - stock < 0) {
            throw new IllegalArgumentException("Stock amount exceeds stock limit");
        }
        this.stock -= stock;
    }

    public boolean isSoldOUt() {
        return this.stock == 0;
    }
}