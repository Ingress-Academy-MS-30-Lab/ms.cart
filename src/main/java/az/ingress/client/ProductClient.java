package az.ingress.client;

import az.ingress.model.response.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "ms-product",
        url = "${product.client.base-url:${MS_PRODUCT_URL:http://ms-product:8080}}"
)
public interface ProductClient {
    @GetMapping("/api/v1/internal/product-variants/{variantId}")
    ProductResponseDto getVariant(@PathVariable("variantId") Long variantId);
}