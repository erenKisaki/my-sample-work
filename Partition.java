String expDate = Stream.of(str1, str2)
        .filter(Objects::nonNull)
        .collect(Collectors.joining());

expDate = expDate.isEmpty() ? null : expDate;
