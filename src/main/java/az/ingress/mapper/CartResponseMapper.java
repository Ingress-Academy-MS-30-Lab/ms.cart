package az.ingress.mapper;

import az.ingress.dao.entity.CartEntity;
import az.ingress.dao.entity.CartItemEntity;
import az.ingress.model.enums.EnumMapper;
import az.ingress.model.response.CartResponse;
import az.ingress.model.response.CartTotalResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring", uses = EnumMapper.class)
public interface CartResponseMapper {

    @Mapping(target = "items",  source = "items")
    @Mapping(target = "totals", expression = "java(buildTotals(cart))")
    CartResponse toResponse(CartEntity cart);

    @Mapping(target = "unitPrice",     source = "unitPriceSnapshot")
    @Mapping(target = "onSale",        source = "onSaleSnapshot")
    @Mapping(target = "title",         source = "titleSnapshot")
    @Mapping(target = "imageUrl",      source = "imageUrlSnapshot")
    @Mapping(target = "categoryId",    source = "categoryIdSnapshot")
    @Mapping(target = "categoryName",  source = "categoryNameSnapshot")
    @Mapping(target = "supplierId",    source = "supplierIdSnapshot")
    @Mapping(target = "supplierUserName", source = "supplierUserNameSnapshot")
    @Mapping(target = "attributesJson",   source = "attributesSnapshotJson")
    @Mapping(target = "lineTotal", expression = "java(lineTotal(item))")
    CartTotalResponse toItem(CartItemEntity item);
}