package az.ingress.mapper;

import az.ingress.dao.entity.CartItemEntity;
import az.ingress.model.dto.ProductSnapshotDto;
import az.ingress.model.request.AddCartItemRequest;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "productId", source = "variant.productId")
    @Mapping(target = "productVariantId", source = "variant.productVariantId")
    @Mapping(target = "qty", source = "req.qty")
    @Mapping(target = "unitPriceSnapshot", expression = "java(effectivePrice(variant))")
    @Mapping(target = "onSaleSnapshot", source = "variant.onSale")
    @Mapping(target = "titleSnapshot", source = "variant.title")
    @Mapping(target = "imageUrlSnapshot", source = "variant.imageUrl")
    @Mapping(target = "categoryIdSnapshot", source = "variant.categoryId")
    @Mapping(target = "categoryNameSnapshot", source = "variant.categoryName")
    @Mapping(target = "supplierIdSnapshot", source = "variant.supplierId")
    @Mapping(target = "supplierUserNameSnapshot", source = "variant.supplierUserName")
    @Mapping(target = "attributesSnapshotJson", source = "variant.attributesJson")
    CartItemEntity toEntity(AddCartItemRequest req, ProductSnapshotDto variant);


    default BigDecimal effectivePrice(ProductSnapshotDto v) {
        if (Boolean.TRUE.equals(v.getOnSale()) && v.getSalePrice() != null) return v.getSalePrice();
        return v.getPrice() != null ? v.getPrice() : BigDecimal.ZERO;
    }

    @AfterMapping
    default void clampQty(AddCartItemRequest req, @MappingTarget CartItemEntity item) {
        if (item.getQty() == null || item.getQty() < 1) item.setQty(1L);
    }
}