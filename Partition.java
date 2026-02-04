public void checkCustomerBlockedStatus(
        String customerId,
        String billingArrangementId,
        PaymentInstrumentIdentification paymentInstrumentIdentification,
        String lob,
        boolean isBillingIdCheckBlockEnabled,
        boolean isCustGuidCheckBlockEnabled) {

    if (paymentInstrumentIdentification == null) {
        return;
    }

    switch (paymentInstrumentIdentification) {
        case Card:
            handleBlockedReference(
                    getBlockedReference(customerId, billingArrangementId,
                            PaymentServiceConstants.INSTRUMENT_TYPE_CARD,
                            lob, isBillingIdCheckBlockEnabled, isCustGuidCheckBlockEnabled),
                    billingArrangementId,
                    customerId,
                    ErrorCodes.PAYMENT_8589);
            break;

        case Bank:
            handleBlockedReference(
                    getBlockedReference(customerId, billingArrangementId,
                            PaymentServiceConstants.INSTRUMENT_TYPE_BANK,
                            lob, isBillingIdCheckBlockEnabled, isCustGuidCheckBlockEnabled),
                    billingArrangementId,
                    customerId,
                    ErrorCodes.PAYMENT_8688);
            break;

        default:
            LOGGER.info("Payment instrument is not Card or Bank, no block check required");
    }
}


private void handleBlockedReference(
        String blockedReference,
        String billingArrangementId,
        String customerId,
        ErrorCodes errorCode) {

    if (StringUtils.isBlank(blockedReference)) {
        return;
    }

    Map<String, Object> details = new HashMap<>();
    boolean isBillingRef = StringUtils.equals(blockedReference, billingArrangementId);

    details.put(BLOCK_REFERENCE_ID,
            isBillingRef ? billingArrangementId : customerId);
    details.put(BLOCK_REFERENCE_TYPE,
            isBillingRef ? BLOCK_REF_BILLING_ARR_ID : BLOCK_REF_TYPE_CUSTOMER_ID);

    throw new BusinessValidationError(errorCode.getCode(), details);
}
