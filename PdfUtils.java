public class AmountValidator {

    private ViewBillSummary validateMinimumAmount(
            String channel,
            AllowablePaymentType pmtType,
            BigDecimal inputAmount,
            BillingInfo billingInfo) {

        BigDecimal minAmount = pmtType.getMinPayment();
        ViewBillSummary viewBillSummary = null;

        if (inputAmount.compareTo(minAmount) < 0
                || Objects.isNull(billingInfo)
                || StringUtils.isBlank(billingInfo.getBillingArrangementId())) {
            return null;
        }

        validateMarketRule(channel);

        viewBillSummary =
                getViewBillSummary(billingInfo.getBillingArrangementId());

        BigDecimal statementBalance = getAmount(viewBillSummary.getStatementBalance());
        BigDecimal balanceDue = getAmount(viewBillSummary.getBalanceDue());

        validateBalances(inputAmount, minAmount, statementBalance, balanceDue);
        return viewBillSummary;
    }

    private void validateMarketRule(String channel) {
        var marketRule =
                marketLevelRuleDao.getMarket(channel, false);

        if (Objects.isNull(marketRule)
                || marketRule.isMinimumAmountValidationEnabled()) {
            commonError(null, ErrorCodes.PAYMENT_1017.getCode(), null);
        }
    }

    private void validateBalances(
            BigDecimal inputAmount,
            BigDecimal minAmount,
            BigDecimal statementBalance,
            BigDecimal balanceDue) {

        if (balanceDue.compareTo(minAmount) > 0
                && statementBalance.compareTo(minAmount) < 0) {
            commonError(inputAmount, ErrorCodes.PAYMENT_1017.getCode(), null);
        }

        if (balanceDue.compareTo(BigDecimal.ZERO) <= 0
                && statementBalance.compareTo(BigDecimal.ZERO) <= 0) {
            commonError(inputAmount, ErrorCodes.PAYMENT_3024.getCode(), null);
        }

        if (balanceDue.compareTo(inputAmount) < 0
                && inputAmount.compareTo(minAmount) < 0) {
            commonError(inputAmount, ErrorCodes.PAYMENT_1017.getCode(), null);
        }
    }

    private BigDecimal getAmount(String value) {
        return StringUtils.isNotBlank(value)
                ? new BigDecimal(value)
                : BigDecimal.ZERO;
    }
}
