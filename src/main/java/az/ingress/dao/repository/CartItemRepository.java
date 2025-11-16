package az.ingress.dao.repository;

import az.ingress.dao.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CartItemRepository extends CrudRepository<CartItemEntity, Long> {

    Optional<CartItemEntity> findByCartIdAndProductVariantId(Long cartId, Long productVariantId);

}