@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(15))
                .build();
    }
}


@Slf4j
@Service
public class ProductServiceClient {

    private final RestTemplate restTemplate;
    private final String serviceUrl;

    public ProductServiceClient(
            RestTemplate restTemplate,
            @Value("${wires.service.url:http://localhost:8080/digital-bulk-recipients}") String serviceUrl) {
        this.restTemplate = restTemplate;
        this.serviceUrl = serviceUrl;
    }

    public boolean saveFileMetaData(ProductRequestBody request) {
        try {
            // Headers are optional; Jackson sets application/json automatically.
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ProductRequestBody> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> resp = restTemplate.postForEntity(serviceUrl, entity, String.class);
            // If we got here, it's a 2xx (non-2xx would have thrown already)
            log.info("POST {} ok: {}", serviceUrl, resp.getStatusCode());
            return true;
        } catch (Exception e) {
            log.error("POST {} failed", serviceUrl, e);
            return false;
        }
    }
}
