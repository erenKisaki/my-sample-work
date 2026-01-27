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

@Test
public void testGetEnhancedBillingInfo_NullBillingIdWithTokenRegexMatches() {

    BillingInfo billingInfo = new BillingInfo(BILLING_ARRANGEMENT_ID_18, "11111");
    AuditCollectorRO.get()
            .getPaymentServiceTransactions().setToken("1234567890123456789")

    when(configuration.getString("token.configuration.regex"))
            .thenReturn("^\\d{19}$");
			
	when(configuration.getString("xbuck.markets.enabled"))
            .thenReturn(List.of("11111");

    AuditCollectorRO.get()
            .getPaymentServiceTransactions()
            .setToken("1234567893456789012");

    BillingInfo updateBillingInfo =
            underTest.getEnhancedBillingInfo(billingInfo, CHANNEL);

    assertNotNull(updateBillingInfo.getBillingArrangementId());
    assertNotNull(updateBillingInfo.getInputBillingArrangementId());
    assertEquals(billingInfo.getMarket(), updateBillingInfo.getMarket());

    verify(configuration).getString("token.configuration.regex");
	verify(configuration).getString("xbuck.markets.enabled");
}
