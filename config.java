<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>


    @SpringBootApplication
@EnableFeignClients
public class WiresBulkProcessExpApplication {
    public static void main(String[] args) {
        SpringApplication.run(WiresBulkProcessExpApplication.class, args);
    }
}

package com.chase.digital.payments.wires.client;

import com.chase.digital.payments.wires.model.ProductRequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "product-service",
    url = "${product.service.url:http://localhost:8080}"   // configurable
)
public interface ProductServiceClient {

    @PostMapping("/digital-bulk-recipients")
    void saveFileMetaData(@RequestBody ProductRequestBody request);
}


@Service
@RequiredArgsConstructor
public class RecipientImportService {

    private final ProductServiceClient productServiceClient;

    public void process(ProductRequestBody body) {
        productServiceClient.saveFileMetaData(body);
    }
}
