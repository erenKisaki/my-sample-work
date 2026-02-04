
    private void validateBankRoutingNumber(String routingNumber) {

        // Original outer condition preserved
        if (StringUtils.isNotBlank(routingNumber)) {
            validateNumeric(routingNumber);
            validateLength(routingNumber);
            validateFirstTwoDigits(routingNumber);
            validateCheckDigit(routingNumber);
        }
    }

    // -------------------- Helper Methods --------------------

    private void validateNumeric(String routingNumber) {
        if (!StringUtils.isNumeric(routingNumber)) {
            throw new RequestValidationError(
                    ErrorCodes.PAYMENT_1027.getCode());
        }
    }

    private void validateLength(String routingNumber) {
        // bank routing number numeric, 9-digit
        if (routingNumber.length() != 9) {
            throw new RequestValidationError(
                    ErrorCodes.PAYMENT_1028.getCode());
        }
    }

    private void validateFirstTwoDigits(String routingNumber) {
        // routing number first two digits should be valid
        int firstTwoDigits = Integer.parseInt(routingNumber.substring(0, 2));

        if ((firstTwoDigits <= 0)
                || (firstTwoDigits >= 6 && firstTwoDigits <= 12)
                || (firstTwoDigits >= 21 && firstTwoDigits <= 32)
                || (firstTwoDigits >= 61 && firstTwoDigits <= 72)) {

            throw new RequestValidationError(
                    ErrorCodes.PAYMENT_1250.getCode());
        }
    }

    private void validateCheckDigit(String routingNumber) {
        // Routing number check digit validation
        int checkValue = 0;

        for (int index = 0; index < 3; index++) {
            checkValue += Character.getNumericValue(routingNumber.charAt(index * 3)) * 3;
            checkValue += Character.getNumericValue(routingNumber.charAt(index * 3 + 1)) * 7;
            checkValue += Character.getNumericValue(routingNumber.charAt(index * 3 + 2));
        }

        if (checkValue == 0 || checkValue % 10 != 0) {
            throw new RequestValidationError(
                    ErrorCodes.PAYMENT_1251.getCode());
        }
    }
