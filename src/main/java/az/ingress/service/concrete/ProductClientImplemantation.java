package az.ingress.service.concrete;


import az.ingress.model.dto.ProductSnapshotDto;
import az.ingress.model.response.ProductResponse;
import az.ingress.model.response.ProductVariantResponse;
import az.ingress.service.abstraction.ProductClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductClientImplemantation implements ProductClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${services.product.base-url}")
    private String productServiceBaseUrl;

    @Override
    public ProductSnapshotDto getVariantSnapshot(Long productVariantId) {

        String url = productServiceBaseUrl + "/api/v1/internal/product-variants?ids=" + productVariantId;

        ResponseEntity<List<ProductResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<List<ProductResponse>>() {}
        );

        List<ProductResponse> body = response.getBody();
        if (body == null || body.isEmpty()) {
            throw new IllegalStateException("Product not found for variantId=" + productVariantId);
        }


        ProductResponse product = body.get(0);


        ProductVariantResponse variant = product.getProductVariants().stream()
                .filter(v -> productVariantId.equals(v.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Variant not found in productVariants for id=" + productVariantId));



        String attributesJson = toJson(variant.getAttributes());

                return ProductSnapshotDto.builder()
                .productId(product.getId())
                .productVariantId(variant.getId())
                .title(product.getTitle())
                .imageUrl(variant.getImageUrl())
                .categoryId(product.getCategoryId())
                .categoryName(product.getCategoryName())
                .supplierId(product.getSupplierId())
                .supplierUserName(product.getSupplierUserName())
                .price(variant.getPrice())
                .salePrice(variant.getSalePrice())
                .onSale(variant.getOnSale())
                .attributesJson(attributesJson)
                .build();
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize attributes to JSON", e);
            return "[]";
        }
    }
}
