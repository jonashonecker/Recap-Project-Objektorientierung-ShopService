import java.util.UUID;

public class IdService {
    public String generateUUID () {
        return UUID.randomUUID().toString();
    }
}
