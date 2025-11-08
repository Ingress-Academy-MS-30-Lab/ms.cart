package az.ingress.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantResponse {

    private Long id;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal salePrice;
    private Boolean onSale;
    private Long stockQuantity;
    private Boolean inStock;
    private List<ProductAttributeResponse> attributes;
}
