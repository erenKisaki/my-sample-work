public String getInvalidFieldsFromReasonMessage(String reasonMessage) {
    if (StringUtils.isBlank(reasonMessage)) {
        return StringUtils.EMPTY;
    }

    List<String> invalidFieldReasons = extractInvalidFields(reasonMessage);

    if (invalidFieldReasons.isEmpty()) {
        return extractFallbackMessage(reasonMessage);
    }

    return buildInvalidFieldMessage(invalidFieldReasons);
}

private List<String> extractInvalidFields(String reasonMessage) {
    List<String> results = new ArrayList<>();
    String[] tokens = reasonMessage.split(INVALID_FIELD_TEXT);

    for (String token : tokens) {
        if (!token.contains(SYMBOL_EQUAL_TO)) {
            continue;
        }

        String value = token.substring(token.indexOf(SYMBOL_EQUAL_TO) + 1);
        addParsedFields(results, value);
    }
    return results;
}

private void addParsedFields(List<String> results, String value) {
    String[] fields = value.contains(PaymentServiceConstants.EMPTY_SPACE)
            ? value.split(PaymentServiceConstants.EMPTY_SPACE)
            : new String[]{value};

    for (String field : fields) {
        if (StringUtils.isNotBlank(field)) {
            results.add(cleanFieldName(field));
        }
    }
}

private String cleanFieldName(String field) {
    StringBuilder cleaned = new StringBuilder();

    for (int i = 0; i < field.length(); i++) {
        char c = field.charAt(i);
        if (!Character.isAlphabetic(c)) {
            cleaned.append(c);
        }
    }
    return StringUtils.deleteWhitespace(cleaned.toString());
}

private String extractFallbackMessage(String reasonMessage) {
    String[] values = reasonMessage.contains(SYMBOL_HYPHEN)
            ? reasonMessage.split(SYMBOL_HYPHEN)
            : reasonMessage.split(SYMBOL_COLON);

    return values.length > 1 ? StringUtils.deleteWhitespace(values[1]) : StringUtils.EMPTY;
}

private String buildInvalidFieldMessage(List<String> fields) {
    return SYMBOL_HYPHEN + PaymentServiceConstant.EMPTY_SPACE +
            String.join(CONNECTING_AND, fields);
}


// next class

private String getCardTypeFromNetworkToken(String cardNumber) {
    if (StringUtils.isBlank(cardNumber)) {
        return null;
    }

    for (CardType cardType : CreditCardTypeEnum.getAll()) {
        if (matchesCardType(cardType, cardNumber)) {
            return cardType.getKey();
        }
    }

    logNoCardType(cardNumber);
    return null;
}

private boolean matchesCardType(CardType cardType, String cardNumber) {
    if (CollectionUtils.isEmpty(cardType.getValue())) {
        return false;
    }

    for (String prefix : cardType.getValue()) {
        if (matchesPrefix(cardNumber, prefix)) {
            return true;
        }
    }
    return false;
}

private boolean matchesPrefix(String cardNumber, String prefix) {
    String[] split = prefix.split("-");
    String start = split[0];
    String end = split.length > 1 ? split[1] : null;

    if (cardNumber.startsWith(start)) {
        return true;
    }

    return end != null && isBetween(cardNumber, start, end);
}

private boolean isBetween(String cardNumber, String start, String end) {
    return cardNumber.substring(0, start.length()).compareTo(start) >= 0
            && cardNumber.substring(0, end.length()).compareTo(end) <= 0;
}

private void logNoCardType(String cardNumber) {
    LOGGER.error("No card type found for card number {}",
            cardNumber.substring(0, Math.min(6, cardNumber.length())));
}


// next class

private Level2And3ProcessTransaction buildProcessTxn(LevelAuditTxn auditTxn) {
    Level2And3ProcessTransaction txn = new Level2And3ProcessTransaction();
    mapRequest(txn, auditTxn);
    mapResponse(txn, auditTxn);
    mapCardFeatures(txn, auditTxn);
    return txn;
}

private void mapRequest(Level2And3ProcessTransaction txn, LevelAuditTxn auditTxn) {
    if (auditTxn.getRequest() != null) {
        txn.setApiRequest(loggingUtil.getObfuscatedJsonObject(auditTxn.getRequest()));
    }
}

private void mapResponse(Level2And3ProcessTransaction txn, LevelAuditTxn auditTxn) {
    if (auditTxn.getResponse() != null) {
        txn.setApiResponse(loggingUtil.getObfuscatedJsonObject(auditTxn.getResponse()));
    }
}

private void mapCardFeatures(Level2And3ProcessTransaction txn, LevelAuditTxn auditTxn) {
    CardFeatures features = auditTxn.getCardFeatures();
    if (features == null) {
        return;
    }

    txn.setCommercialCardIndicator(
            StringUtils.defaultIfBlank(features.getCardCommerceIndicator(), null));

    txn.setHealthcare(
            StringUtils.defaultIfBlank(features.getCardHealthcare(), null));

    txn.setPayroll(
            StringUtils.defaultIfBlank(features.getCardPayroll(), null));
}


// next class
private Level2And3ProcessTransaction buildProcessTxn(LevelAuditTxn auditTxn) {
    Level2And3ProcessTransaction txn = new Level2And3ProcessTransaction();
    mapRequest(txn, auditTxn);
    mapResponse(txn, auditTxn);
    mapCardFeatures(txn, auditTxn);
    return txn;
}

private void mapRequest(Level2And3ProcessTransaction txn, LevelAuditTxn auditTxn) {
    if (auditTxn.getRequest() != null) {
        txn.setApiRequest(loggingUtil.getObfuscatedJsonObject(auditTxn.getRequest()));
    }
}

private void mapResponse(Level2And3ProcessTransaction txn, LevelAuditTxn auditTxn) {
    if (auditTxn.getResponse() != null) {
        txn.setApiResponse(loggingUtil.getObfuscatedJsonObject(auditTxn.getResponse()));
    }
}

private void mapCardFeatures(Level2And3ProcessTransaction txn, LevelAuditTxn auditTxn) {
    CardFeatures features = auditTxn.getCardFeatures();
    if (features == null) {
        return;
    }

    txn.setCommercialCardIndicator(
            StringUtils.defaultIfBlank(features.getCardCommerceIndicator(), null));

    txn.setHealthcare(
            StringUtils.defaultIfBlank(features.getCardHealthcare(), null));

    txn.setPayroll(
            StringUtils.defaultIfBlank(features.getCardPayroll(), null));
}
