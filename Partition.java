JsonNode root = new ObjectMapper().readTree(response.getBody());

assertEquals("schedule-payment-service", root.path("id").asText());
assertEquals("No records found for future dated schedule Payment",
        root.path("messages").path("PAYMENT-8076").asText());
