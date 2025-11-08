package az.ingress.dao.repository;

import az.ingress.dao.entity.CartEntity;
import az.ingress.model.enums.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Long> {

    Optional<CartEntity> findByBuyerIdAndStatusNot(Long buyerId, CartStatus status);

}
