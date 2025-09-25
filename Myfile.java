public static String generateRandomString(boolean onlyAlphabets, int length) {

        if (onlyAlphabets) {
            return RANDOM.ints(length, 0, ALPHABETS.length())
                    .mapToObj(ALPHABETS::charAt)
                    .map(String::valueOf)
                    .collect(Collectors.joining());
        } else {
            String oneDigit = String.valueOf(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));

            String others = RANDOM.ints(length - 1, 0, ALPHANUMERIC.length())
                    .mapToObj(ALPHANUMERIC::charAt)
                    .map(String::valueOf)
                    .collect(Collectors.joining());

            List<Character> chars = (oneDigit + others)
                    .chars()
                    .mapToObj(c -> (char) c)
                    .collect(Collectors.toList());

            Collections.shuffle(chars);

            return chars.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining());
        }
}
