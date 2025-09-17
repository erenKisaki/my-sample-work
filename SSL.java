    public static String generateRandomString(boolean onlyAlphabets, int length) {
        String source = onlyAlphabets ? ALPHABETS : ALPHANUMERIC;
        return RANDOM.ints(length, 0, source.length())
                     .mapToObj(source::charAt)
                     .map(String::valueOf)
                     .collect(Collectors.joining());
    }

    private static final String ALPHABETS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
