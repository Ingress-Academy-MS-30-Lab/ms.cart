package az.ingress.mapper;

import az.ingress.dao.entity.CartItemEntity;
import az.ingress.model.request.AddCartItemRequest;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "productId",        source = "productId")
    @Mapping(target = "productVariantId", source = "productVariantId")
    @Mapping(target = "quantity",         source = "quantity")
    CartItemEntity toEntity(AddCartItemRequest req);

}