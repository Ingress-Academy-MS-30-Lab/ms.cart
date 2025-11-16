package az.ingress.dao.repository;

import az.ingress.dao.entity.CartEntity;
import az.ingress.model.enums.CartStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CartRepository extends CrudRepository<CartEntity, Long> {

    Optional<CartEntity> findByBuyerIdAndStatusNot(Long buyerId, CartStatus status);

    @EntityGraph(attributePaths = "items")
    Optional<CartEntity> findWithItemsByBuyerIdAndStatusNot(Long buyerId, CartStatus status);

}