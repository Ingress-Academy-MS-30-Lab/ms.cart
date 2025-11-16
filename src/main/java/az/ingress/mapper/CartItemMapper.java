package az.ingress.mapper;

import az.ingress.dao.entity.CartItemEntity;
import az.ingress.model.request.AddCartItemRequest;
import org.mapstruct.*;
import org.mapstruct.MappingConstants;

import java.math.BigDecimal;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, imports = {BigDecimal.class})
public interface CartItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)

    @Mapping(target = "productId",         source = "variant.productId")
    @Mapping(target = "productVariantId",  source = "variant.productVariantId")
    @Mapping(target = "qty",               source = "req.qty")

    @Mapping(target = "unitPriceSnapshot",        expression = "java(calcUnitPrice(variant))")
    @Mapping(target = "onSaleSnapshot",           source = "variant.onSale")
    @Mapping(target = "titleSnapshot",            source = "variant.title")
    @Mapping(target = "imageUrlSnapshot",         source = "variant.imageUrl")
    @Mapping(target = "categoryIdSnapshot",       source = "variant.categoryId")
    @Mapping(target = "categoryNameSnapshot",     source = "variant.categoryName")
    @Mapping(target = "supplierIdSnapshot",       source = "variant.supplierId")
    @Mapping(target = "supplierUserNameSnapshot", source = "variant.supplierUserName")
    @Mapping(target = "attributesSnapshotJson",   source = "variant.attributesJson")
    CartItemEntity toEntity(AddCartItemRequest req, ProductSnapshotDto variant);

    @AfterMapping
    default void normalize(@MappingTarget CartItemEntity e) {
        if (e.getQty() == null || e.getQty() < 1) e.setQty(1L);
        if (e.getUnitPriceSnapshot() == null) e.setUnitPriceSnapshot(BigDecimal.ZERO);
    }

    default BigDecimal calcUnitPrice(ProductSnapshotDto v) {
        if (v == null) return BigDecimal.ZERO;
        if (Boolean.TRUE.equals(v.getOnSale()) && v.getSalePrice() != null) return v.getSalePrice();
        return v.getPrice() != null ? v.getPrice() : BigDecimal.ZERO;
    }
}