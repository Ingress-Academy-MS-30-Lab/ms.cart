package az.ingress.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull
    @Column(name = "product_variant_id", nullable = false)
    private Long productVariantId;

    @Min(1)
    @Column(name = "qty", nullable = false)
    private Long qty;

    @NotNull
    @Column(name = "unit_price_snapshot", nullable = false, precision = 25, scale = 5)
    private BigDecimal unitPriceSnapshot;

    @NotNull
    @Column(name = "on_sale_snapshot", nullable = false)
    private Boolean onSaleSnapshot;

    @Column(name = "title_snapshot", length = 1024)
    private String titleSnapshot;

    @Column(name = "image_url_snapshot", length = 1024)
    private String imageUrlSnapshot;

    @Column(name = "category_id_snapshot")
    private Long categoryIdSnapshot;

    @Column(name = "category_name_snapshot", length = 1024)
    private String categoryNameSnapshot;

    @Column(name = "supplier_id_snapshot")
    private Long supplierIdSnapshot;

    @Column(name = "supplier_username_snapshot", length = 1024)
    private String supplierUserNameSnapshot;

    @Column(name = "attributes_snapshot", columnDefinition = "jsonb")
    private String attributesSnapshotJson;
}