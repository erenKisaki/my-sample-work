public Level2And3ProcessTransaction createLevelProcessTransactionObject(
        LevelAuditTxn levelAuditTxn, String downgradeReason) {

    Level2And3ProcessTransaction processTxn = new Level2And3ProcessTransaction();
    AuditCollectorRO auditCollectorRO =
            new AuditCollectorRO(getGatewayTxn());

    processTxn.setApiName(levelAuditTxn.getApiName());

    setShippingDetails(processTxn, levelAuditTxn);
    setApiRequest(processTxn, levelAuditTxn);
    setApiResponse(processTxn, levelAuditTxn);
    processCardFeatures(processTxn, auditCollectorRO, levelAuditTxn);
    setResponseCodes(processTxn, levelAuditTxn);

    processTxn.setDowngradeReason(downgradeReason);
    return processTxn;
}

private void setShippingDetails(Level2And3ProcessTransaction processTxn,
                                LevelAuditTxn levelAuditTxn) {

    if (Objects.nonNull(levelAuditTxn.getShipTo())) {
        processTxn.setShipToPostalCode(levelAuditTxn.getShipTo().getAddress().getZip());
        processTxn.setShipToState(levelAuditTxn.getShipTo().getAddress().getState());
    }
}
private void setApiRequest(Level2And3ProcessTransaction processTxn,
                           LevelAuditTxn levelAuditTxn) {

    if (Objects.nonNull(levelAuditTxn.getRequest())) {
        processTxn.setApiRequest(
                loggingUtil.getObfuscatedJsonObject(levelAuditTxn.getRequest()));
    }
}

private void setApiResponse(Level2And3ProcessTransaction processTxn,
                            LevelAuditTxn levelAuditTxn) {

    if (Objects.nonNull(levelAuditTxn.getResponse())) {
        processTxn.setApiResponse(
                loggingUtil.getObfuscatedJsonObject(levelAuditTxn.getResponse()));
    }
}
private void processCardFeatures(Level2And3ProcessTransaction processTxn,
                                 AuditCollectorRO auditCollectorRO,
                                 LevelAuditTxn levelAuditTxn) {

    CardFeatures cardFeatures = levelAuditTxn.getCardFeatures();
    if (Objects.isNull(cardFeatures)) {
        return;
    }

    auditCollectorRO.setAffluenceIndicator(
            toChar(cardFeatures.getAffluenceIndicator()));

    if (!IPConstants.SUCCESS_REASON_CODE.equals(levelAuditTxn.getReasonCode())) {
        processTxn.setApiErrorCode(String.valueOf(levelAuditTxn.getReasonCode()));
        processTxn.setApiErrorText(levelAuditTxn.getReasonMessage());
    }

    auditCollectorRO.setCardCommercial(toChar(cardFeatures.getCardCommercial()));
    auditCollectorRO.setCardHealthCare(toChar(cardFeatures.getCardHealthCare()));
    auditCollectorRO.setCardIssuerCountry(toChar(cardFeatures.getCardIssuerCountry()));
    auditCollectorRO.setCardLevel3Eligible(toChar(cardFeatures.getCardLevel3Eligible()));
    auditCollectorRO.setCardPayroll(toChar(cardFeatures.getCardPayroll()));
    auditCollectorRO.setCardPinlessDebit(toChar(cardFeatures.getCardPinlessDebit()));
    auditCollectorRO.setCardPrepaid(toChar(cardFeatures.getCardPrepaid()));
    auditCollectorRO.setCardRegulated(toChar(cardFeatures.getCardRegulated()));
    auditCollectorRO.setCardSignatureDebit(toChar(cardFeatures.getCardSignatureDebit()));
}
private void setResponseCodes(Level2And3ProcessTransaction processTxn,
                              LevelAuditTxn levelAuditTxn) {

    if (StringUtils.isNotBlank(levelAuditTxn.getProcessorResponseCode())) {
        processTxn.setProcessorResponseCode(
                levelAuditTxn.getProcessorResponseCode());
        return;
    }

    if (StringUtils.isNotBlank(levelAuditTxn.getCaptureResponseCode())) {
        processTxn.setProcessorResponseCode(
                levelAuditTxn.getCaptureResponseCode());
    }

    processTxn.setLevel3Enabled(
            toChar(levelAuditTxn.getPurchasingLevel3Enabled()));
}
private Character toChar(String value) {
    return StringUtils.isNotEmpty(value) ? value.charAt(0) : null;
}
