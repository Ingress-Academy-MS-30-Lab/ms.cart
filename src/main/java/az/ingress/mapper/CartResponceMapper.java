package az.ingress.mapper;

import az.ingress.dao.entity.CartEntity;
import az.ingress.dao.entity.CartItemEntity;
import az.ingress.model.response.CartItemResponce;
import az.ingress.model.response.CartResponce;
import az.ingress.model.response.CartTotalResponce;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.math.BigDecimal;
import java.util.List;


@Mapper(componentModel = "spring")
public interface CartResponceMapper {

    @Mapping(target = "items", source = "items")
    @Mapping(target = "totals", expression = "java(buildTotals(cart))")
    CartResponce toResponce(CartEntity cart);

    @Mapping(target = "unitPrice", source = "unitPriceSnapshot")
    @Mapping(target = "onSale", source = "onSaleSnapshot")
    @Mapping(target = "title", source = "titleSnapshot")
    @Mapping(target = "imageUrl", source = "imageUrlSnapshot")
    @Mapping(target = "categoryId", source = "categoryIdSnapshot")
    @Mapping(target = "categoryName", source = "categoryNameSnapshot")
    @Mapping(target = "supplierId", source = "supplierIdSnapshot")
    @Mapping(target = "supplierUserName", source = "supplierUserNameSnapshot")
    @Mapping(target = "attributesJson", source = "attributesSnapshotJson")
    @Mapping(target = "lineTotal", expression = "java(lineTotal(item))")
    CartItemResponce toItem(CartItemEntity item);


    default BigDecimal lineTotal(CartItemEntity item) {
        if (item.getUnitPriceSnapshot() == null || item.getQty() == null) return BigDecimal.ZERO;
        return item.getUnitPriceSnapshot().multiply(BigDecimal.valueOf(item.getQty()));
    }

    default CartTotalResponce buildTotals(CartEntity cart) {
        List<CartItemEntity> list = cart.getItems() == null ? List.of() : cart.getItems().stream().toList();
        int itemsCount = list.size();
        long totalQty = list.stream().map(CartItemEntity::getQty).filter(q -> q != null).mapToLong(Long::longValue).sum();
        BigDecimal amount = list.stream().map(this::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return CartTotalResponce.builder()
                .itemsCount(itemsCount)
                .totalQty(totalQty)
                .amount(amount)
                .build();
    }
}