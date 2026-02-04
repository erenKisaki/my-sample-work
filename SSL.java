
    // ======================================================
    // MAIN METHOD (Sonar complexity fixed here)
    // ======================================================
    public AlternativeCardTokenValidationResult validate(
            PaymentHeader header,
            ApplePayTokenPaymentRequest request) {

        String channel = null;

        try {
            channel = header.getChannel();

            channelLevelPropertiesDao
                    .getChannelLevelPropertyByChannel(channel);

            String billingArrangementId =
                    BillingUtil.getBillingArrangementId(request.getBillingInfo());

            performFraudAndBillingChecks(channel, request, billingArrangementId);

            BillingFlowContext billingFlowContext =
                    resolveBillingFlow(channel, request, billingArrangementId);

            TokenContext tokenContext =
                    retrieveAndValidateToken(header, request, channel);

            validatePaymentConfiguration(
                    request, channel, billingFlowContext);

            validateDuplicateAndBlockRules(
                    request, channel, billingFlowContext, tokenContext);

            return buildValidationResult(
                    billingFlowContext, tokenContext);

        } catch (BusinessValidationError bve) {
            handleBusinessValidationError(bve);
            throw bve;
        } catch (Exception exception) {
            handleGenericException(exception);
            throw exception;
        }
    }

    // ======================================================
    // FRAUD + BILLING (unchanged logic)
    // ======================================================
    private void performFraudAndBillingChecks(
            String channel,
            ApplePayTokenPaymentRequest request,
            String billingArrangementId) {

        commonBusinessValidator.performFraudCheck(
                channel, billingArrangementId);

        billingInfoValidator.validateAndPopulateBillingInfo(
                request.getBillingInfo(), channel);
    }

    // ======================================================
    // BILLING FLOW RESOLUTION (EXACT if/else preserved)
    // ======================================================
    private BillingFlowContext resolveBillingFlow(
            String channel,
            ApplePayTokenPaymentRequest request,
            String inputBillingArrangementId) {

        BillingFlowContext context = new BillingFlowContext();

        if (ChannelUtil.isRecurringPayment(channel)
                && ChannelUtil.isScheduledPayment(channel)
                && request.getBillingInfo() != null) {

            String market = resolveMarket(request);
            context.setMarket(market);
            context.setContactHistoryRequired(true);

        } else {
            EnhancedBillingInfo enhancedBillingInfo =
                    billingInfoValidator.getEnhancedBillingInfo(
                            request.getBillingInfo(), channel);

            context.setEnhancedBillingInfo(enhancedBillingInfo);
            context.setContactHistoryRequired(
                    PaymentDynamicUtils.isPaymentPostingEnabled(channel));
        }

        return context;
    }

    private String resolveMarket(ApplePayTokenPaymentRequest request) {
        EnhancedBillingInfo info =
                billingInfoValidator.getEnhancedBillingInfo(
                        request.getBillingInfo(), null);

        if (StringUtils.isNotBlank(info.getMarket())) {
            return info.getMarket();
        }

        return info.getBillingArrangementId() != null
                ? info.getBillingArrangementId()
                : null;
    }

    // ======================================================
    // TOKEN RETRIEVAL + VALIDATION (order preserved)
    // ======================================================
    private TokenContext retrieveAndValidateToken(
            PaymentHeader header,
            ApplePayTokenPaymentRequest request,
            String channel) {

        String customerId =
                customerUtils.messageCustomerId(
                        request.getCustomerId());

        Optional<String> maskedAccountNumber =
                customerUtils.queryTokenMaskedAccountNumber(customerId);

        RetrieveToken retrieveToken =
                retrieveTokenDataAdapter.retrieveTokenDetails(
                        customerId,
                        header.getToken(),
                        channel,
                        maskedAccountNumber.orElse(null));

        Objects.requireNonNull(retrieveToken);
        Objects.requireNonNull(retrieveToken.getTokenId());
        Objects.requireNonNull(retrieveToken.getTokenUpdate());

        paymentTokenDataServiceHelper
                .populateCoastalWalletData(retrieveToken);

        seasoningBusinessValidator
                .validateCardSeasoningState(
                        channel,
                        retrieveToken.getTokenId(),
                        retrieveToken.getTokenUpdate());

        TokenContext context = new TokenContext();
        context.setRetrieveToken(retrieveToken);
        context.setMaskedAccountNumber(maskedAccountNumber.orElse(null));
        context.setInternalCardType(retrieveToken.getInternalCardType());
        context.setCoastalWalletToken(retrieveToken.getCoastalWalletToken());

        return context;
    }

    // ======================================================
    // PAYMENT CONFIGURATION VALIDATION
    // ======================================================
    private void validatePaymentConfiguration(
            ApplePayTokenPaymentRequest request,
            String channel,
            BillingFlowContext billingFlowContext) {

        commonBusinessValidator.validateConfiguration(
                request.getAlternativeCardType(),
                billingFlowContext.getMarket(),
                channel);

        String applePayType = request.getAlternativeCardType();

        AllowablePaymentType allowablePaymentType =
                allowablePaymentTypeDao
                        .getAllowableAlternativeCardPaymentType(
                                applePayType,
                                billingFlowContext.getMarket(),
                                channel);

        billingFlowContext.setAllowablePaymentType(allowablePaymentType);

        tokenBusinessValidator.validateTokenPaymentAllowed(
                ChannelUtil.isScheduledPayment(channel),
                allowablePaymentType);
    }

    // ======================================================
    // DUPLICATE + BIN + BLOCK RULES (order preserved)
    // ======================================================
    private void validateDuplicateAndBlockRules(
            ApplePayTokenPaymentRequest request,
            String channel,
            BillingFlowContext billingFlowContext,
            TokenContext tokenContext) {

        if (StringUtils.isNotBlank(tokenContext.getMaskedAccountNumber())) {
            permittedBinValidator.validatePermittedBin(
                    channel,
                    tokenContext.getMaskedAccountNumber());
        }

        if (StringUtils.isNotBlank(
                billingFlowContext.getBillingArrangementId())) {

            if (request.getOverridePayment() != null) {
                duplicatePaymentValidator
                        .validateOverridePayment(
                                request.getOverridePayment());
            } else {
                duplicatePaymentValidator
                        .validateDuplicatePaymentExistsForToken(
                                billingFlowContext.getBillingArrangementId(),
                                request.getAmount(),
                                request.getAlternativeCardType(),
                                request.getToken(),
                                channel);
            }
        }

        blockManagementAdapter.checkCustomerBlockedStatus(
                channel,
                billingFlowContext.getEnhancedBillingInfo()
                        .getBillingArrangementId(),
                billingFlowContext.getEnhancedBillingInfo()
                        .getAccountType());
    }

    // ======================================================
    // RESULT BUILDER (unchanged)
    // ======================================================
    private AlternativeCardTokenValidationResult buildValidationResult(
            BillingFlowContext billingFlowContext,
            TokenContext tokenContext) {

        return new AlternativeCardTokenValidationResult(
                billingFlowContext.getAllowablePaymentType(),
                billingFlowContext.isContactHistoryRequired(),
                tokenContext.getMaskedAccountNumber(),
                billingFlowContext.getEnhancedBillingInfo(),
                tokenContext.getRetrieveToken(),
                tokenContext.getInternalCardType(),
                tokenContext.getCoastalWalletToken());
    }

    // ======================================================
    // EXCEPTION HANDLING (unchanged)
    // ======================================================
    private void handleBusinessValidationError(
            BusinessValidationError bve) {

        LOGGER.error("Business Validation Error in executor", bve);
        exceptionHelper.setExceptionAuditData(bve);

        AuditCollectorRO.get()
                .getPaymentServiceTransactions()
                .setTransactionStatus(
                        PaymentServiceConstants.TXN_STATUS_FAILURE);
    }

    private void handleGenericException(Exception exception) {

        LOGGER.error("Exception in executor", exception);
        exceptionHelper.setExceptionAuditData(exception);

        AuditCollectorRO.get()
                .getPaymentServiceTransactions()
                .setTransactionStatus(
                        PaymentServiceConstants.TXN_STATUS_FAILURE);
    }
