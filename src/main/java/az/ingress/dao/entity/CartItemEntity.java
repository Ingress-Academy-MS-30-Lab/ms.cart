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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;


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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private CartEntity cart;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_variant_id")
    private Long productVariantId;

    @Column(name = "qty")
    private Long qty;

    @Column(name = "unit_price_snapshot", precision = 19, scale = 2)
    private BigDecimal unitPriceSnapshot;

    @Column(name = "on_sale_snapshot")
    private Boolean onSaleSnapshot;

    @Column(name = "title_snapshot", length = 512)
    private String titleSnapshot;

    @Column(name = "image_url_snapshot", length = 1024)
    private String imageUrlSnapshot;

    @Column(name = "category_id_snapshot")
    private Long categoryIdSnapshot;

    @Column(name = "category_name_snapshot", length = 255)
    private String categoryNameSnapshot;

    @Column(name = "supplier_id_snapshot")
    private Long supplierIdSnapshot;

    @Column(name = "supplier_user_name_snapshot", length = 255)
    private String supplierUserNameSnapshot;

    @Column(name = "attributes_snapshot", columnDefinition = "jsonb")
    private String attributesSnapshotJson;
}