import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ShopServiceTest {

    @Test
    void addOrderTest() {
        //GIVEN
        ShopService shopService = new ShopService(new ProductRepo(), new OrderMapRepo(), new IdService());
        List<String> productsIds = List.of("1");

        //WHEN
        Order actual = shopService.addOrder(productsIds);

        //THEN
        Order expected = new Order("-1", List.of(new Product("1", "Apfel")), Instant.now());
        assertEquals(expected.products(), actual.products());
        assertNotNull(expected.id());
    }

    @Test
    void addOrderTest_WhenInvalidProductId_TrowsProductDoesNotExistException() {
        //GIVEN
        ShopService shopService = new ShopService(new ProductRepo(), new OrderMapRepo(), new IdService());
        List<String> productsIds = List.of("1", "2");

        //WHEN
        Exception actual = assertThrows(ProductDoesNotExistException.class, () -> shopService.addOrder(productsIds));

        //THEN
        String expectedMessage = "Product mit der Id: 2 konnte nicht bestellt werden!";
        assertTrue(actual.getMessage().contains(expectedMessage));
    }

    @Test
    void getListOfOrdersByOrderStatus_WhenOrderStatusPROCESSING_ReturnsOne() {
        //GIVEN
        ShopService shopService = new ShopService(new ProductRepo(), new OrderMapRepo(), new IdService());
        Order newOrder = shopService.addOrder(List.of("1"));

        //WHEN
        List<Order> actual = shopService.getListOfOrdersByOrderStatus(OrderStatus.PROCESSING);

        //THEN
        List<Order> expected = List.of(newOrder);
        assertEquals(expected, actual);
    }

    @Test
    void getListOfOrdersByOrderStatus_WhenOrderStatusNotPresent_ReturnsEmptyList() {
        //GIVEN
        ShopService shopService = new ShopService(new ProductRepo(), new OrderMapRepo(), new IdService());
        Order newOrder = shopService.addOrder(List.of("1"));

        //WHEN
        List<Order> actual = shopService.getListOfOrdersByOrderStatus(OrderStatus.COMPLETED);

        //THEN
        List<Order> expected = List.of();
        assertEquals(expected, actual);
    }

    @Test
    void updateOrder_WhenOrderDoesNotExist_ThrowOrderDoesNotExistException() {
        //GIVEN
        ShopService shopService = new ShopService(new ProductRepo(), new OrderMapRepo(), new IdService());
        Order newOrder = shopService.addOrder(List.of("1"));

        //WHEN
        Exception actual = assertThrows(OrderDoesNotExistException.class, () -> {
            shopService.updateOrder("2", OrderStatus.COMPLETED);
        });

        //THEN
        String expectedMessage = "Die Bestellung mit der Id: 2 konnte nicht gefunden werden!";
        assertTrue(actual.getMessage().contains(expectedMessage));
    }

    @Test
    void updateOrder_WhenInputValidOrderIdAndOrderStatusCOMPLETED_UpdatesOrderStatusToCOMPLETED() {
        //GIVEN
        ShopService shopService = new ShopService(new ProductRepo(), new OrderMapRepo(), new IdService());
        Order newOrder = shopService.addOrder(List.of("1"));
        String orderId = newOrder.id();

        //WHEN
        shopService.updateOrder(orderId, OrderStatus.COMPLETED);

        //THEN
        List<Order> actual = shopService.getListOfOrdersByOrderStatus(OrderStatus.COMPLETED);
        OrderStatus actualOrderStatus = actual.get(0).orderStatus();
        assertEquals(OrderStatus.COMPLETED, actualOrderStatus);
    }

    @Test
    void getOldestOrderPerStatus_WhenOrderRepoEmpty_ReturnsEmptyMap() {
        // GIVEN
        ShopService shopService = new ShopService(new ProductRepo(), new OrderMapRepo(), new IdService());

        // WHEN
        Map<OrderStatus, Optional<Order>> actual = shopService.getOldestOrderPerStatus();

        // THEN
        Map<OrderStatus, Optional<Order>> expected = new HashMap<>();
        assertEquals(expected, actual);
    }

    @Test
    void getOldestOrderPerStatus_WhenAddingOrdersSequentiallyWithTheSameOrderStatus_ReturnsFirstAddedOrder() {
        // GIVEN
        OrderRepo orderRepo = new OrderMapRepo();
        Product p1 = new Product("1", "Baseball");
        Product p2 = new Product("2", "Basketball");
        Product p3 = new Product("3", "Fußball");
        Order o1 = new Order("1", List.of(p1, p2), Instant.now());
        Order o2 = new Order("2", List.of(p1, p2, p3), Instant.now());
        Order o3 = new Order("3", List.of(p3), Instant.now());
        orderRepo.addOrder(o1);
        orderRepo.addOrder(o2);
        orderRepo.addOrder(o3);
        ShopService shopService = new ShopService(new ProductRepo(), orderRepo, new IdService());

        // WHEN
        Map<OrderStatus, Optional<Order>> actual = shopService.getOldestOrderPerStatus();

        // THEN
        Map<OrderStatus, Optional<Order>> expected = new HashMap<>();
        expected.put(OrderStatus.PROCESSING, Optional.of(o1));
        assertEquals(expected, actual);
    }
}
