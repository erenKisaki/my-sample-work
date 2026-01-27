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
