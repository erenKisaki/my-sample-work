public boolean reverseAuthorization(
        String paymentTransactionId,
        boolean autoRetry,
        GatewayPaymentRequest gatewayPaymentRequest) {

    int retryCount = paymentPropertiesDao.getPositiveIntProperty(
            GatewayConstants.PMT_GTW_REVERSAL_RETRY_COUNT,
            GatewayConstants.PMT_GTW_REVERSAL_RETRY_COUNT_DEFAULT);

    long retryWaitTime = paymentPropertiesDao.getPositiveLongProperty(
            GatewayConstants.PMT_GTW_REVERSAL_RETRY_WAIT_TIME,
            GatewayConstants.PMT_GTW_REVERSAL_RETRY_WAIT_TIME_DEFAULT);

    for (int attempt = 0; attempt <= retryCount; attempt++) {

        ReverseAuthResults results =
                pmService.reverseAuthorization(paymentTransactionId, gatewayPaymentRequest);

        if (results.isReverseSuccess()) {
            auditRequired(gatewayPaymentRequest, true, results);
            return true;
        }

        if (!results.isAuthMissing()) {
            auditRequired(gatewayPaymentRequest, false, results);
            return false;
        }

        if (!autoRetry || attempt == retryCount) {
            auditRequired(gatewayPaymentRequest, false, results);
            return false;
        }

        sleepSafely(retryWaitTime);
    }

    return false;
}
private void sleepSafely(long waitTime) {
    try {
        Thread.sleep(waitTime);
    } catch (InterruptedException e) {
        LOGGER.error("Interrupted during retry wait", e);
        Thread.currentThread().interrupt();
    }
}

