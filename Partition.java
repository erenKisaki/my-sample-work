ps.setString(index++, 
    paymentGatewayTransactions.isNetworkTokenIndicator() ? "TRUE" : "FALSE"
);

String ntIndicator = rs.getString("NT_INDICATOR");

boolean isNtIndicator = "TRUE".equalsIgnoreCase(ntIndicator);
pymtGtwyTxn.setNetworkTokenIndicator(isNtIndicator);
