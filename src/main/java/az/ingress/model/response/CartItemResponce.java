package az.ingress.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponce {

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
    private Boolean onSale;
    private BigDecimal lineTotal;
    private String attributesJson;

}
