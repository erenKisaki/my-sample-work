private void auditPaymentSignalTransactions(EvaluateSignalResponse response) {

    if (response == null) {
        return;
    }

    var paymentSignalTransaction =
            auditCollectorRO.get().getPaymentSignalTransactions();

    paymentSignalTransaction.setPlaidReqId(response.getRequestId());

    var scores = response.getScores();
    if (scores != null) {
        setCustomerInitiatedRisk(scores, paymentSignalTransaction);
        setBankInitiatedRisk(scores, paymentSignalTransaction);
    }

    if (response.getRuleset() != null) {
        paymentSignalTransaction
                .setRulesetResult(response.getRuleset().getRulesetResult());
    }

    auditCollectorRO.get()
            .setPaymentSignalTransactions(paymentSignalTransaction);
}


private void setCustomerInitiatedRisk(Scores scores,
                                      PaymentSignalTransaction txn) {

    var cir = scores.getCustomerInitiatedReturnRisk();
    if (cir == null) {
        return;
    }

    if (cir.getRiskTier() != null) {
        txn.setCirRiskTier(cir.getRiskTier().longValue());
    }

    if (cir.getScore() != null) {
        txn.setCirRiskScore(cir.getScore().longValue());
    }
}

private void setBankInitiatedRisk(Scores scores,
                                  PaymentSignalTransaction txn) {

    var bir = scores.getBankInitiatedReturnRisk();
    if (bir == null) {
        return;
    }

    if (bir.getRiskTier() != null) {
        txn.setBirRiskTier(bir.getRiskTier().longValue());
    }

    if (bir.getScore() != null) {
        txn.setBirRiskScore(bir.getScore().longValue());
    }
}
