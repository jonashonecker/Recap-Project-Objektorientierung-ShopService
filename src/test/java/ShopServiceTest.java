import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShopServiceTest {

    @Test
    void addOrderTest() {
        //GIVEN
        ShopService shopService = new ShopService();
        List<String> productsIds = List.of("1");

        //WHEN
        Order actual = shopService.addOrder(productsIds);

        //THEN
        Order expected = new Order("-1", List.of(new Product("1", "Apfel")));
        assertEquals(expected.products(), actual.products());
        assertNotNull(expected.id());
    }

    @Test
    void addOrderTest_whenInvalidProductId_TrowsProductDoesNotExistException() {
        //GIVEN
        ShopService shopService = new ShopService();
        List<String> productsIds = List.of("1", "2");

        //WHEN
        Exception actual = assertThrows(ProductDoesNotExistException.class,() -> shopService.addOrder(productsIds));

        //THEN
        String expectedMessage = "Product mit der Id: 2 konnte nicht bestellt werden!";
        assertTrue(actual.getMessage().contains(expectedMessage));
    }

    @Test
    void getListOfOrdersByOrderStatus_WhenOrderStatusPROCESSING_ReturnsOne() {
        //GIVEN
        ShopService shopService = new ShopService();
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
        ShopService shopService = new ShopService();
        Order newOrder = shopService.addOrder(List.of("1"));

        //WHEN
        List<Order> actual = shopService.getListOfOrdersByOrderStatus(OrderStatus.COMPLETED);

        //THEN
        List<Order> expected = List.of();
        assertEquals(expected, actual);
    }
}
