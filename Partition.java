    // Handle contribution limits
if (RestrictionConstants.ACCOUNTTYPELISTS.contains(accountTypeCode)) {
    ContributionLimitExceededResponse limitResponse = new ContributionLimitExceededResponse();
    ResponseEntity<ET3Response> et3Entity = et3Service.validateDepositVBALockbox(clientPoid, requestBody.getAmount(), requestBody.getAccount());
    List<VOMRuleDetails> voMRulDetails = new ArrayList<>();
    if (et3Entity.getStatusCode() == HttpStatus.OK) {
        ET3Response et3Response = et3Entity.getBody();
        boolean isDc2ServiceCall = true;
        if (null != et3Response && et3Response.getProcessedTradeTransaction() != null
                && et3Response.getProcessedTradeTransaction().getProcessedTradeTransactionDetail() != null
                && et3Response.getProcessedTradeTransaction().getProcessedTradeTransactionDetail().getTriggeredTradingRules() != null
                && et3Response.getProcessedTradeTransaction().getProcessedTradeTransactionDetail().getTriggeredTradingRules().size() > 0) {
            List<ET3Response.TriggeredTradingRule> triggeredTradingRules = et3Response.getProcessedTradeTransaction().getProcessedTradeTransactionDetail().getTriggeredTradingRules();
            for (ET3Response.TriggeredTradingRule triggeredTradingRule : triggeredTradingRules) {
                VOMRuleDetails voMRulDetail = new VOMRuleDetails();
                voMRulDetail.setVoMRulSeverity(getRuleSeverity(triggeredTradingRule.getRuleAction()));
                voMRulDetail.setVoMRulId(triggeredTradingRule.getRuleId());
                voMRulDetails.add(voMRulDetail);
                
                if(triggeredTradingRule.getRuleAction() == RuleAction.WARNING && triggeredTradingRule.getRuleAction() == RuleAction.ERROR) {
                    isDc2ServiceCall = false;
                }
            }
            if(isDc2ServiceCall) {
                RetirementContributionSummaryResponse dc2Response = dc2ContributionService.getContributionLimits(clientPoid);
                limitResponse = dc2Mapper.mapToContributionLimitResponse(dc2Response, requestBody.getAmount(), requestBody.getTransactionCode(), accountTypeCode, accountId);
            }
            limitResponse.setVoMRulDetails(voMRulDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body(limitResponse);
        }
    } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ET3 service call failed");
    }
