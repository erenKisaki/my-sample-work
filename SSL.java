import java.security.SecureRandom;
import java.util.stream.Collectors;

public class RandomAlphaStream {

    private static final String ALPHABETS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static void main(String[] args) {
        String randomString = RANDOM.ints(11, 0, ALPHABETS.length())
                .mapToObj(ALPHABETS::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());

        System.out.println("Random String: " + randomString);
    }
}
