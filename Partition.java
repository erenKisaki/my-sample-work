    // =====================================================
    // ORIGINAL METHOD (Sonar fixed, logic untouched)
    // =====================================================
    private void updateServiceAndGatewayTransaction(
            PaymentServiceTransactions paymentServiceTransactions,
            TransactionSearchResponse transactionSearchResponse,
            PaymentGatewayTransactions paymentGatewayTransactions) {

        String confirmationNumber =
                confirmationNumberDao.getConfirmationNumber();

        paymentServiceTransactions
                .setPaymentConfirmationNumber(
                        Long.parseLong(confirmationNumber));

        PaymentSearchTransaction paymentSearchTransaction = null;
        ProcessingData processingData = null;

        for (SearchTransaction searchTransaction
                : transactionSearchResponse.getSearchTransactions()) {

            if (StringUtils.equalsIgnoreCase(
                    TransactionType.BANK_PAYMENT,
                    searchTransaction.getTransactionType())) {

                paymentSearchTransaction = searchTransaction;
                processingData = searchTransaction.getDebitData();

            } else if (StringUtils.equalsIgnoreCase(
                    TransactionType.AUTH_CAPTURE,
                    searchTransaction.getTransactionType())) {

                paymentSearchTransaction = searchTransaction;
                processingData = searchTransaction.getCaptureData();
            }
        }

        populateFromSearchTransaction(
                paymentSearchTransaction,
                paymentServiceTransactions,
                paymentGatewayTransactions);

        populateFromProcessingData(
                processingData,
                paymentServiceTransactions,
                paymentGatewayTransactions);
    }

    // =====================================================
    // SEARCH TRANSACTION HANDLING (extracted, same logic)
    // =====================================================
    private void populateFromSearchTransaction(
            PaymentSearchTransaction paymentSearchTransaction,
            PaymentServiceTransactions paymentServiceTransactions,
            PaymentGatewayTransactions paymentGatewayTransactions) {

        if (paymentSearchTransaction == null) {
            return;
        }

        paymentServiceTransactions
                .setRequestId(
                        paymentSearchTransaction.getPcRequestId());

        paymentGatewayTransactions
                .setTransactionId(
                        paymentSearchTransaction.getTransactionId());

        if (StringUtils.isNotBlank(
                paymentSearchTransaction.getPcResponseId())) {

            paymentGatewayTransactions
                    .setPcResponseId(
                            paymentSearchTransaction.getPcResponseId());
        }

        populateProcessorInformation(
                paymentSearchTransaction,
                paymentGatewayTransactions);
    }

    // =====================================================
    // PROCESSOR INFO (unchanged logic)
    // =====================================================
    private void populateProcessorInformation(
            PaymentSearchTransaction paymentSearchTransaction,
            PaymentGatewayTransactions paymentGatewayTransactions) {

        if (paymentSearchTransaction.getProcessorInformation() == null) {
            return;
        }

        ProcessorInformation processorInfo =
                paymentSearchTransaction.getProcessorInformation();

        paymentGatewayTransactions
                .setAuthorizationCode(
                        processorInfo.getApprovalCode());

        if (StringUtils.isNotBlank(
                processorInfo.getProcessorResponseCode())) {

            paymentGatewayTransactions
                    .setChasePTechResponseCode(
                            processorInfo.getProcessorResponseCode());
        }

        if (StringUtils.isNotBlank(processorInfo.getAvsCode())) {
            paymentGatewayTransactions
                    .setAvsCode(
                            processorInfo.getAvsCode().charAt(0));
        }

        if (StringUtils.isNotBlank(processorInfo.getCvvCode())) {
            paymentGatewayTransactions
                    .setCvvCode(
                            processorInfo.getCvvCode().charAt(0));
        }
    }

    // =====================================================
    // PROCESSING DATA HANDLING (unchanged logic)
    // =====================================================
    private void populateFromProcessingData(
            ProcessingData processingData,
            PaymentServiceTransactions paymentServiceTransactions,
            PaymentGatewayTransactions paymentGatewayTransactions) {

        if (processingData == null) {
            return;
        }

        paymentServiceTransactions
                .setPaymentReconciliationId(
                        processingData.getReconciliationId());

        paymentGatewayTransactions
                .setReconciliationId(
                        processingData.getReconciliationId());
    }
