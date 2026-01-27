
		
private BillingInfo validateTokenConfiguration(BillingInfo billingInfo) {

    var tokenRegex =
            configuration.getString("token.configuration.regex");

    String token = AuditCollectorRO.get()
            .getPaymentServiceTransactions()
            .getToken();

    if (StringUtils.isNotBlank(tokenRegex)
            && StringUtils.isNotBlank(token)
            && token.matches(tokenRegex)) {

    //return createEnhancedBillingInfo(billingInfo.getBillingArrangementId(), billingInfo); // Remove this line and add the below code in this method
		if (StringUtils.isNotBlank(billingInfo.getMarket())) {

            var xbuckMarkets =
                    configuration.getStringList("xbuck.bypass.ass.enabled.markets");

            if (CollectionUtils.isNotEmpty(xbuckMarkets)
                    && StringUtils.isNotBlank(billingInfo.getMarket())
                    && xbuckMarkets.contains(billingInfo.getMarket())) {

                return createEnhancedBillingInfo(
                        billingInfo.getBillingArrangementId(),
                        billingInfo
                );
            }
		}
        
    }

    return null;
}
