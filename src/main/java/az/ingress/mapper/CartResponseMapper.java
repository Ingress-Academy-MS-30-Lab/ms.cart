package az.ingress.mapper;

import az.ingress.dao.entity.CartEntity;
import az.ingress.model.dto.CartItemDto;
import az.ingress.model.response.CartResponse;
import az.ingress.model.response.CartTotalResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CartResponseMapper {

    @Mapping(target = "cartId", source = "cart.id")
    @Mapping(target = "buyerId", source = "cart.buyerId")
    @Mapping(target = "status",
            expression = "java(cart.getStatus() != null ? cart.getStatus().name() : null)")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "total", source = "total")
    CartResponse toResponse(CartEntity cart,
                            java.util.List<CartItemDto> items,
                            CartTotalResponse total);


}