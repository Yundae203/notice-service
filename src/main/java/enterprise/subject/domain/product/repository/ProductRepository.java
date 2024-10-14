package enterprise.subject.domain.product.repository;

import enterprise.subject.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
