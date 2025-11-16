package az.ingress.model.enums;

import org.springframework.stereotype.Component;

@Component
public class EnumMapper {
    public String fromCartStatus(CartStatus s) { return s == null ? null : s.name(); }
    public CartStatus toCartStatus(String v)   { return v == null ? null : CartStatus.valueOf(v); }
}