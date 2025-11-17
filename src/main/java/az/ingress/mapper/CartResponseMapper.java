package az.ingress.mapper;

import az.ingress.dao.entity.CartEntity;
import az.ingress.dao.entity.CartItemEntity;
import az.ingress.model.dto.CartItemDto;
import az.ingress.model.response.CartResponse;
import az.ingress.model.response.CartTotalResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartResponseMapper {

    @Mapping(target = "cartId", source = "id")
    @Mapping(target = "buyerId", source = "buyerId")
    @Mapping(target = "status",  expression = "java(cart.getStatus() == null ? null : cart.getStatus().name())")
    @Mapping(target = "items",   source = "items")
    @Mapping(target = "total",   expression = "java(buildTotals(cart))")
    CartResponse toResponse(CartEntity cart);

    @Mapping(target = "productId",        source = "productId")
    @Mapping(target = "productVariantId", source = "productVariantId")
    @Mapping(target = "qty",              source = "qty")

    @Mapping(target = "title",            source = "titleSnapshot")
    @Mapping(target = "imageUrl",         source = "imageUrlSnapshot")
    @Mapping(target = "categoryId",       source = "categoryIdSnapshot")
    @Mapping(target = "categoryName",     source = "categoryNameSnapshot")
    @Mapping(target = "supplierId",       source = "supplierIdSnapshot")
    @Mapping(target = "supplierUserName", source = "supplierUserNameSnapshot")

    @Mapping(target = "unitPrice",        source = "unitPriceSnapshot")
    @Mapping(target = "onSale",           source = "onSaleSnapshot")
    @Mapping(target = "attributesJson",   source = "attributesSnapshotJson")
    @Mapping(target = "lineTotal",        expression = "java(lineTotal(item))")
    CartItemDto toItemDto(CartItemEntity item);


    default BigDecimal lineTotal(CartItemEntity item) {
        if (item == null || item.getUnitPriceSnapshot() == null || item.getQty() == null) {
            return BigDecimal.ZERO;
        }
        return item.getUnitPriceSnapshot().multiply(BigDecimal.valueOf(item.getQty()));
    }

    default CartTotalResponse buildTotals(CartEntity cart) {
        List<CartItemEntity> items = cart.getItems() == null
                ? List.of()
                : cart.getItems().stream().toList();

        int itemsCount = items.size();

        long totalQty = items.stream()
                .map(CartItemEntity::getQty)
                .filter(q -> q != null)
                .mapToLong(Long::longValue)
                .sum();

        BigDecimal amount = items.stream()
                .map(this::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartTotalResponse.builder()
                .itemsCount(itemsCount)
                .totalQty(totalQty)
                .amount(amount)
                .build();
    }
}