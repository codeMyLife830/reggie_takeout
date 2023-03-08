import org.junit.jupiter.api.Test;

public class UploadFileTest {
    @Test
    public void test1() {
        String filename = "test3675.jpg";
        String suffix = filename.substring(filename.lastIndexOf('.'));
        System.out.println(suffix);
    }
}
