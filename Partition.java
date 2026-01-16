List<BatchReauthCustomException> sortedList =
        exceptionList.stream()
                .sorted(Comparator.comparingLong(BatchBaseException::getTime))
                .collect(Collectors.toList());
