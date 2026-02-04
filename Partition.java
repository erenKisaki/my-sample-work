public Optional<LevelProcessingData> getLevelProcessingData(
        LevelProcessingRequest levelProcessingRequest) {

    Optional<LevelProcessingData> levelProcessingOptionalData = Optional.empty();

    var levelEligibility =
            levelEligibilityAdapter.getLevelEligibility(levelProcessingRequest);

    BigDecimal stateTax;

    if (levelEligibility.isPresent()) {
        LOGGER.debug("Not a Level Processing request. Follow standard processing.");
        return levelProcessingOptionalData;
    }

    var level = levelEligibility.get();
    var levelProcessingData = new LevelProcessingData();
    levelProcessingData.setPurchasingLevel(level.toString());
    levelProcessingData.setChannel(levelProcessingRequest.getChannel());

    var levelProcessingAMSData = levelProcessingData.getLevelProcessingAMSData();

    if (isLevel2(level)) {
        stateTax = getStateTaxForLevel2(
                levelProcessingRequest, levelProcessingData, levelProcessingAMSData);
        handleLevel2(stateTax, level, levelProcessingAMSData);
    } else if (isLevel3(level)) {
        handleLevel3(
                levelProcessingRequest, levelProcessingData, levelProcessingAMSData);
    }

    setupLevelProcessTxnObject(level, levelProcessingAMSData);
    return Optional.of(levelProcessingData);
}

private void handleLevel2(BigDecimal stateTax,
                          Long level,
                          LevelProcessingAMSData levelProcessingAMSData) {

    if (Objects.isNull(stateTax)) {
        if (levelProcessingAMSData.getDowngradeReason().isPresent()) {
            levelProcessingAMSData.setDowngradeReason(
                    Optional.of("StateTax not available in AccountManagementService."));
        }
        return;
    }

    if (stateTax.compareTo(BigDecimal.ZERO) > 0) {
        levelProcessingAMSData.setLevelEnabled(true);
        levelProcessingAMSData.setStateTaxAmount(stateTax);
    } else {
        levelProcessingAMSData.setDowngradeReason(
                Optional.of("Negative tax amount in AMS. Downgraded to standard"));
    }
}
private void handleLevel3(LevelProcessingRequest levelProcessingRequest,
                          LevelProcessingData levelProcessingData,
                          LevelProcessingAMSData levelProcessingAMSData) {

    levelProcessingData.setStateTaxAmount(BigDecimal.ZERO);

    Address address =
            amsAdapter.getAddressFromQueryBillingArrangement(
                    levelProcessingRequest.getBillingArrangementId(),
                    Boolean.TRUE.equals(levelProcessingRequest.getLevelProcessing()),
                    levelProcessingAMSData);

    if (isValidAddress(address)) {
        levelProcessingData.setLevelEnabled(true);
        levelProcessingData.setState(address.getState());
        levelProcessingData.setZipCode(address.getZipCode());
        levelProcessingData.setPsCode(address.getPsCode());

        var channel = channelLevelPropertiesDao
                .getChannelLevelProperties(levelProcessingRequest.getChannel());
        levelProcessingData.setCommodityCode(channel.getCommodityCode());
    } else {
        levelProcessingAMSData.setDowngradeReason(
                Optional.of("Call to AccountManagementService failed or address or PsCode not returned in AMS"));
    }
}

private boolean isLevel2(Long level) {
    return Long.valueOf(PaymentServiceConstants.LEVEL_2).equals(level);
}

private boolean isLevel3(Long level) {
    return Long.valueOf(PaymentServiceConstants.LEVEL_3).equals(level);
}

private boolean isValidAddress(Address address) {
    return address != null
            && StringUtils.isNotEmpty(address.getState())
            && StringUtils.isNotEmpty(address.getZipCode())
            && StringUtils.isNotEmpty(address.getPsCode());
}
