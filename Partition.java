import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

public class YourClass {

    private static final String TRUSTSTORE_PATH = "path_to_your/customTrustStore.jks";
    private static final String TRUSTSTORE_PASSWORD = "truststore_password";

    private static int addIdsToSelector(List<String> list, String selectorName, String selector) {
        final String method = "addIdsToSelector";
        int status = 0;
        try {
            // Load the custom trust store
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (InputStream trustStream = new FileInputStream(TRUSTSTORE_PATH)) {
                trustStore.load(trustStream, TRUSTSTORE_PASSWORD.toCharArray());
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

            // Configure the client with the SSL context
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.property("jersey.config.client.sslcontext", sslContext);
            HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("PF_ADMIN_USER", "PF_ADMIN_PASS");

            Client client = ClientBuilder.newBuilder()
                    .withConfig(clientConfig)
                    .sslContext(sslContext)
                    .build();
            client.register(feature);

            String id = Config.getConfigValue("SAML_ID");
            String location = Config.getConfigValue("SAML_LOCATION");

            WebTarget webTarget = client.target(Config.getConfigValue("PF_ADMIN_API_URL")).path(selector);
            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
            invocationBuilder.header("X-ISAM-API", "PingFederate");

            List<List<String>> partitions = SelectorUtil.getPartitionedList(list, 100);
            Logger.info("{} Partition Size: {}", method, partitions.size());
            int batchCount = 0;

            for (List<String> connSubLst : partitions) {
                batchCount++;
                String reqObj = SelectorUtil.buildRequestObjForSAML(connSubLst, selector, selectorName, id, location);
                Response stringRsp = invocationBuilder.post(Entity.entity(reqObj, MediaType.APPLICATION_JSON));
                status = stringRsp.getStatus();
                Logger.info("{} Status: {}", method, status);

                if (stringRsp.getStatus() != 200) {
                    Logger.debug("{} Batch {} update failed", method, batchCount);
                    Logger.error("{} Batch {} update failed", method, batchCount);
                    return status;
                }
            }
        } catch (Exception e) {
            Logger.debug("{} Error executing the script. Message: {}", method, e.getMessage(), e);
            Logger.error("{} Error executing the script. Message: {}", method, e.getMessage(), e);
        }

        return status;
    }
}
