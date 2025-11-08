package az.ingress.service.abstraction;

import az.ingress.model.dto.ProductSnapshotDto;

public interface ProductClient {

    ProductSnapshotDto getVariantSnapshot(Long productVariantId);
}
