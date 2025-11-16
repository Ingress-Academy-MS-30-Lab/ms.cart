package az.ingress.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {
    private Long productId;
    private Long productVariantId;
    private Long qty;

    private String title;
    private String imageUrl;

    private Long categoryId;
    private String categoryName;

    private Long supplierId;
    private String supplierUserName;

    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private Boolean onSale;

    private String attributesJson;
}