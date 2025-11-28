package az.ingress.client;

import az.ingress.model.response.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "ms-product",
        url = "${client.urls.ms-product}/internal"
)
@Profile("!local")
public interface ProductClient {

    @GetMapping("/v1/product-variants/{variantId}")
    ProductResponseDto getVariant(@PathVariable("variantId") Long variantId);
}