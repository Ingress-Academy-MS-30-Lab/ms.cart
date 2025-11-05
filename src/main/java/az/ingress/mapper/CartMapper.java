package az.ingress.mapper;

import az.ingress.dao.entity.Cart;
import az.ingress.model.dto.CartCreateDto;
import az.ingress.model.enums.CartStatus;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static az.ingress.model.enums.CartStatus.CREATED;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "items", ignore = true)
    Cart toEntity(CartCreateDto dto);

    @AfterMapping
    default void normalizee(@MappingTarget Cart cart) {

        if (cart.getStatus() == null) {
            cart.setStatus(CREATED);

        }
    }

    default void setOrdered(Cart cart) {

        if (cart != null && cart.getStatus() == CartStatus.CREATED) {

            cart.setStatus(CartStatus.ORDERED);
        }
    }

    default void setDeletedNow(Cart cart, java.time.LocalDateTime now) {

        if (cart != null) {
            cart.setStatus(CartStatus.DELETED);
            cart.setDeletedAt(now);

        }
    }
}