package az.ingress.dao.entity;

import az.ingress.model.enums.CartStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "carts")
@SQLDelete(sql = "UPDATE carts SET status = 'DELETED', deleted_at = now() WHERE id = ?")
@Where(clause = "status <> 'DELETED'")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 64)
    private CartStatus status = CartStatus.CREATED;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Builder.Default
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CartItem> items = new LinkedHashSet<>();


    public void addOrIncrease(CartItem candidate, long incQty) {
        ensureNotDeleted();

        Optional<CartItem> existing = items.stream()
                .filter(i -> i.getProductVariantId().equals(candidate.getProductVariantId()))
                .findFirst();

        if (existing.isPresent()) {
            CartItem i = existing.get();
            i.setQty(i.getQty() + Math.max(1, incQty));
        } else {
            candidate.setCart(this);
            candidate.setQty(Math.max(1, incQty));
            items.add(candidate);
        }
        touchComposition();
    }

    public void setQtyOrRemove(Long productVariantId, long qty) {
        ensureNotDeleted();

        for (Iterator<CartItem> it = items.iterator(); it.hasNext(); ) {
            CartItem i = it.next();
            if (i.getProductVariantId().equals(productVariantId)) {
                if (qty <= 0) {
                    it.remove();
                } else {
                    i.setQty(qty);
                }
                touchComposition();
                return;
            }
        }
    }

    public void removeItem(Long productVariantId) {
        ensureNotDeleted();
        boolean changed = items.removeIf(i -> i.getProductVariantId().equals(productVariantId));
        if (changed) {
            touchComposition();
        }
    }

    public void markDeleted() {
        if (status != CartStatus.DELETED) {
            status = CartStatus.DELETED;
            deletedAt = Instant.now();
            items.clear();
        }
    }

    private void ensureNotDeleted() {
        if (status == CartStatus.DELETED) {
            throw new IllegalStateException("cart is deleted");
        }
    }

    private void touchComposition() {
        if (items.isEmpty()) {
            markDeleted();
        } else if (status == CartStatus.CREATED) {
            status = CartStatus.ORDERED;
        } else if (status == CartStatus.ORDERED) {

        }
    }
}