import java.util.Random;

public class KeyGenerator {

    private static final Random random = new Random();

    public static String randomPrimaryKey() {
        String chars = "12345";
        int length = 5;
        StringBuilder result = new StringBuilder();

        for (int i = length; i > 0; i--) {
            int index = random.nextInt(chars.length());
            result.append(chars.charAt(index));
        }

        return result.toString();
    }

    public static String randomSecondaryKey() {
        String chars = "123";
        int length = 3;
        StringBuilder result = new StringBuilder();

        for (int i = length; i > 0; i--) {
            int index = random.nextInt(chars.length());
            result.append(chars.charAt(index));
        }

        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println("Primary Key   : " + randomPrimaryKey());
        System.out.println("Secondary Key : " + randomSecondaryKey());
    }
}
