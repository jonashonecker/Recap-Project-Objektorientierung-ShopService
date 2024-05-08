import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ToString
@RequiredArgsConstructor
public class ShopService {
    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;

    public Order addOrder(List<String> productIds) {
        List<Product> products = new ArrayList<>();
        for (String productId : productIds) {
            Optional<Product> productToOrder = productRepo.getProductById(productId);
            if (productToOrder.isEmpty()) {
                throw new ProductDoesNotExistException("Product mit der Id: " + productId + " konnte nicht bestellt werden!");
            }
            products.add(productToOrder.get());
        }

        Order newOrder = new Order(UUID.randomUUID().toString(), products, Instant.now());

        return orderRepo.addOrder(newOrder);
    }

    public List<Order> getListOfOrdersByOrderStatus (OrderStatus orderStatus) {
        return orderRepo.getOrders().stream()
                .filter(order -> orderStatus == order.orderStatus())
                .toList();
    }

    public void updateOrder (String orderId, OrderStatus newOrderStatus) {
        Order foundOrder = orderRepo.getOrderById(orderId);
        if (foundOrder == null) {
            throw new OrderDoesNotExistException("Die Bestellung mit der Id: " + orderId + " konnte nicht gefunden werden!");
        }
        Order modifiedOrder = foundOrder.withOrderStatus(newOrderStatus);
        orderRepo.addOrder(modifiedOrder);
    }
}
