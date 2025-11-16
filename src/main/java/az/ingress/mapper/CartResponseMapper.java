package az.ingress.mapper;

import az.ingress.dao.entity.CartEntity;
import az.ingress.dao.entity.CartItemEntity;
import az.ingress.model.response.CartResponse;
import az.ingress.model.response.CartTotalResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartResponseMapper {

    @Mapping(target = "items",  source = "items")
    @Mapping(target = "totals", expression = "java(buildTotals(cart))")
    CartResponse toResponse(CartEntity cart);

    @Mapping(target = "unitPrice",        source = "unitPriceSnapshot")
    @Mapping(target = "onSale",           source = "onSaleSnapshot")
    @Mapping(target = "title",            source = "titleSnapshot")
    @Mapping(target = "imageUrl",         source = "imageUrlSnapshot")
    @Mapping(target = "categoryId",       source = "categoryIdSnapshot")
    @Mapping(target = "categoryName",     source = "categoryNameSnapshot")
    @Mapping(target = "supplierId",       source = "supplierIdSnapshot")
    @Mapping(target = "supplierUserName", source = "supplierUserNameSnapshot")
    @Mapping(target = "attributesJson",   source = "attributesSnapshotJson")
    @Mapping(target = "lineTotal",        expression = "java(lineTotal(item))")
    CartItemResponse toItem(CartItemEntity item);


    default BigDecimal lineTotal(CartItemEntity item) {
        if (item == null || item.getUnitPriceSnapshot() == null || item.getQty() == null) {
            return BigDecimal.ZERO;
        }
        return item.getUnitPriceSnapshot().multiply(BigDecimal.valueOf(item.getQty()));
    }

    default CartTotalResponse buildTotals(CartEntity cart) {
        List<CartItemEntity> list = (cart == null || cart.getItems() == null)
                ? List.of()
                : cart.getItems().stream().toList();

        int itemsCount = list.size();
        long totalQty = list.stream()
                .map(CartItemEntity::getQty)
                .filter(q -> q != null)
                .mapToLong(Long::longValue)
                .sum();

        BigDecimal amount = list.stream()
                .map(this::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartTotalResponse.builder()
                .itemsCount(itemsCount)
                .totalQty(totalQty)
                .amount(amount)
                .build();
    }
}
