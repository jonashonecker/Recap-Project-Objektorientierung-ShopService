import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.minBy;

@ToString
@RequiredArgsConstructor
public class ShopService {
    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;
    private final IdService idService;

    public Order addOrder(List<String> productIds) {
        List<Product> products = new ArrayList<>();
        for (String productId : productIds) {
            Optional<Product> productToOrder = productRepo.getProductById(productId);
            if (productToOrder.isEmpty()) {
                throw new ProductDoesNotExistException("Product mit der Id: " + productId + " konnte nicht bestellt werden!");
            }
            products.add(productToOrder.get());
        }

        Order newOrder = new Order(idService.generateUUID(), products, Instant.now());

        return orderRepo.addOrder(newOrder);
    }

    public List<Order> getListOfOrdersByOrderStatus (OrderStatus orderStatus) {
        return orderRepo.getOrders().stream()
                .filter(order -> orderStatus == order.orderStatus())
                .toList();
    }

    public Map<OrderStatus, Optional<Order>> getOldestOrderPerStatus () {
        return orderRepo.getOrders().stream()
                .collect(groupingBy(
                        Order::orderStatus,
                        minBy(Comparator.comparing(Order::orderTimestamp)))
                );
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
