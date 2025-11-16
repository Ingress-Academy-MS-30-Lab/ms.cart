package az.ingress.mapper;

import az.ingress.dao.entity.CartEntity;
import az.ingress.model.dto.CartCreateDto;
import az.ingress.model.enums.CartStatus;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "items", ignore = true)
    CartEntity toEntity(CartCreateDto dto);

    @AfterMapping
    default void ensureStatus(@MappingTarget CartEntity cart) {
        if (cart.getStatus() == null) {
            cart.setStatus(CartStatus.CREATED);
        }
    }
}