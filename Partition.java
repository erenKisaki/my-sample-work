@Test
    public void webhookIsFromUnknownSource_shouldReturnError() throws Exception {
        var headers = createSecureHeaders("channel", "FUSION");
        headers.add("plaid-verification",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");

        var request = new WebhookVerificationRequest();
        var entity = new HttpEntity<>(request, headers);

        // IMPORTANT: stub the downstream call here too,
        // otherwise the test can fail with 500 because no mock response exists.
        mockServer.post(and(byUri(PLAID_WEBHOOK_VERIFICATION_GET)))
                .response(
                        withStatusCode(500),
                        withHeader("Content-Type", "application/json"),
                        withBody("{\"messages\":{\"PBS-1104\":\"Webhook verification failed\"}}")
                );

        ResponseEntity<PaymentWebhookErrorResponse> responseEntity =
                restTemplate.exchange(
                        WEBHOOK_VERIFICATION,
                        HttpMethod.POST,
                        entity,
                        PaymentWebhookErrorResponse.class
                );

        assertNotNull(responseEntity);
        assertTrue(
                responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST ||
                responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
        );
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getMessages());
        assertTrue(responseEntity.getBody().getMessages().containsKey("PBS-1104"));

        hit.verify(byUri(PLAID_WEBHOOK_VERIFICATION_GET), once());
        hit.verify(unexpected(), never());
    }
