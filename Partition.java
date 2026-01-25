

public BillingInfo getEnhancedBillingInfo(BillingInfo billingInfo, String channel) {

    if (Objects.isNull(billingInfo)) {
        return createEnhancedBillingInfo(null, null, null);
    }

    // XBUCKS decision
    BillingInfo xbuckResult = handleXbucksFlow(billingInfo);
    if (xbuckResult != null) {
        return xbuckResult;
    }

    // Token configuration decision
    BillingInfo tokenResult = handleTokenConfiguration(billingInfo, channel);
    if (tokenResult != null) {
        return tokenResult;
    }

    // Default enhancement flow
    if (StringUtils.isNotBlank(billingInfo.getBillingArrangementId())) {

        if (paymentDynamicUtils.isAccountCTEnabled(
                channel, billingInfo.getBillingArrangementId())) {

            return verifyAndCreateEnhancedBillingInfo(
                    billingInfo.getBillingArrangementId());
        }

        return createEnhancedBillingInfo(
                billingInfo.getBillingArrangementId(),
                billingInfo.getBillingArrangementId(),
                null);
    }

    return createEnhancedBillingInfo(null, null, billingInfo.getMarket());
}


private BillingInfo handleXbucksFlow(BillingInfo billingInfo) {

    Integer instrumentId =
            AuditCollectorRO.get()
                    .getPaymentServiceTransactions()
                    .getPaymentInstrumentId();

    if (instrumentId != null
            && instrumentId == PaymentServiceConstants.INSTRUMENT_ID_XBUCKS) {

        if (StringUtils.isNotBlank(billingInfo.getMarket())) {
            return billingInfo;
        }

        if (!isXbucksAllowedForBypass(
                billingInfo.getBillingArrangementId())) {
            return billingInfo;
        }
    }

    return null; // continue enhancement
}


private BillingInfo handleTokenConfiguration(
        BillingInfo billingInfo, String channel) {

    String tokenRegex =
            configuration.getString("token.configuration.regex");

    if (StringUtils.isBlank(tokenRegex)) {
        return billingInfo;
    }

    return null; 
}
