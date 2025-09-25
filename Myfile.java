    private static final String ALPHABETS    = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS       = "0123456789";
    private static final String ALPHANUMERIC = ALPHABETS + DIGITS;

    public static String generateRandomString(boolean onlyAlphabets, int length) {
        if (length <= 0) throw new IllegalArgumentException("length must be > 0");

        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        if (onlyAlphabets) {
            return rnd.ints(length, 0, ALPHABETS.length())
                      .mapToObj(i -> String.valueOf(ALPHABETS.charAt(i)))
                      .collect(Collectors.joining());
        }

        if (length == 1) {
            return String.valueOf(DIGITS.charAt(rnd.nextInt(DIGITS.length())));
        }

        int digitPos = rnd.nextInt(length);
        char requiredDigit = DIGITS.charAt(rnd.nextInt(DIGITS.length()));

        return IntStream.range(0, length)
                .mapToObj(pos -> {
                    if (pos == digitPos) {
                        return String.valueOf(requiredDigit);
                    }
                    int pick = rnd.nextInt(ALPHANUMERIC.length());
                    return String.valueOf(ALPHANUMERIC.charAt(pick));
                })
                .collect(Collectors.joining());
    }
