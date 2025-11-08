package az.ingress.dao.repository;

import az.ingress.dao.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {

    Optional<CartItemEntity> findByCartIdAndProductVariantId(Long cartId, Long productVariantId);

}