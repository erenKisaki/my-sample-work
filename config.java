import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}


import com.chase.digital.payments.wires.model.ProductRequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceClient {

    private final WebClient webClient;

    @Value("${wires.service.url:http://localhost:8080/digital-bulk-recipients}")
    private String serviceUrl;

    public boolean saveFileMetaData(ProductRequestBody request) {
        try {
            String response = webClient.post()
                    .uri(serviceUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)     // async stream
                    .block();                     // block for result (keeps it simple)

            log.info("Successfully sent metadata: {}", response);
            return true;
        } catch (Exception e) {
            log.error("Failed to send ProductRequestBody to {}", serviceUrl, e);
            return false;
        }
    }
}
