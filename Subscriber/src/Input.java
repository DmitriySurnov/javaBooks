import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Input {
    public static String string() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }

    public static int number() throws IOException {
        return Integer.parseInt(string());
    }

    public static int number(int min, int max) throws Exception {
        int Number = number();
        if (!(Number >= min && Number <= max)) throw new Exception("");
        return Number;
    }
}
