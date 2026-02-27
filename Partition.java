if (StringUtils.isNotBlank(expiryDate) 
            && expiryDate.length() == 4 
            && StringUtils.isNumeric(expiryDate)) {
pymtGtwyTxn.setNetworkTokenExpirationMonth(expiryDate.substring(0, 2));
            pymtGtwyTxn.setNetworkTokenExpirationYear(expiryDate.substring(2, 4));
}
