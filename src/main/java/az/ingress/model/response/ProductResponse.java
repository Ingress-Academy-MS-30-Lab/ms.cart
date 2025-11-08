package az.ingress.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private Long supplierId;
    private String supplierUserName;
    private Long categoryId;
    private String categoryName;
    private String title;
    private List<ProductVariantResponse> productVariants;
}
