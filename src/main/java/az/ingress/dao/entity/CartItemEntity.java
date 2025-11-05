package az.ingress.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "cart_items")
public class CartItemEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private CartEntity cartEntity;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_variant_id")
    private Long productVariantId;

    @Column(name = "qty")
    private Long qty;

    @Column(name = "unit_price_snapshot", precision = 25, scale = 5)
    private BigDecimal unitPriceSnapshot;

    @Column(name = "on_sale_snapshot")
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