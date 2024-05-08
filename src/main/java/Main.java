import java.time.Instant;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ProductRepo productRepo = new ProductRepo();
        Product p1 = new Product("1", "Baseball");
        Product p2 = new Product("2", "Basketball");
        Product p3 = new Product("3", "Fußball");

        productRepo.addProduct(p1);
        productRepo.addProduct(p2);
        productRepo.addProduct(p3);

        OrderRepo orderRepo = new OrderMapRepo();
        Order o1 = new Order("1", List.of(p1, p2), Instant.now());
        Order o2 = new Order("2", List.of(p1, p2, p3), Instant.now());
        Order o3 = new Order("3", List.of(p3), Instant.now());

        orderRepo.addOrder(o1);
        orderRepo.addOrder(o2);
        orderRepo.addOrder(o3);

        ShopService shopService = new ShopService(productRepo, orderRepo);
        System.out.println(shopService);
    }
}
