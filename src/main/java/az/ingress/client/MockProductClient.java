package az.ingress.client;

import az.ingress.model.response.ProductResponseDto;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Primary
@Profile("mock")
@Component
public class MockProductClient implements ProductClient {
    @Override
    public ProductResponseDto getVariant(Long variantId) {
        return ProductResponseDto.builder()
                .productId(variantId)
                .productVariantId(variantId)
                .title("MOCK_TITLE")
                .imageUrl("MOCK_IMAGE_URL")
                .categoryId(10L)
                .categoryName("MOCK_CATEGORY")
                .supplierId(5L)
                .supplierUserName("mock_supplier")
                .price(BigDecimal.TEN)
                .salePrice(BigDecimal.TEN)
                .onSale(false)
                .attributesJson("[]")
                .build();
    }

}