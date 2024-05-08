import lombok.With;

import java.time.Instant;
import java.util.List;

@With
public record Order(
        String id,
        List<Product> products,
        OrderStatus orderStatus,
        Instant orderTimestamp
) {
    public Order(String id, List<Product> products, Instant orderTimestamp) {
        this(id, products, OrderStatus.PROCESSING, orderTimestamp);
    }
}
