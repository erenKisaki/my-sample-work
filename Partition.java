import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@Test
void testResponseEntityJson() throws Exception {



    // Basic assertions
    assertNotNull(response);
    assertNotNull(response.getBody());

    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(response.getBody());

    // Field assertions
    assertEquals(1774617138659L, root.path("timestamp").asLong());
    assertEquals("com.comcast.payment.core.exceptions.ErrorResponse", root.path("type").asText());
    assertEquals("schedule-payment-service", root.path("id").asText());

    // Empty objects
    assertTrue(root.path("actions").isObject());
    assertTrue(root.path("details").isObject());

    // Messages validation
    JsonNode messages = root.path("messages");
    assertNotNull(messages);
    assertFalse(messages.isEmpty());

    assertEquals(
        "No records found for future dated schedule Payment",
        messages.path("PAYMENT-8076").asText()
    );
}
