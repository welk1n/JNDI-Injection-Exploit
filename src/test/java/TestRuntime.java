import org.junit.Test;

/**
 * @Classname TestRuntime
 * @Description For testing your command
 * @Author Welkin
 */
public class TestRuntime {
    @Test
    public void testRuntime() throws Exception{
        Runtime.getRuntime().exec("id");
    }
}
