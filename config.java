import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

private static int addIdsToSelector(List<String> list, String selectorName, String selector) {
    final String _method_ = "addIdsToSelector";
    int status = 0;
    try {
        // Set the trust store properties
        System.setProperty("javax.net.ssl.trustStore", "path_to_cacerts");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");

        // Load the trust store programmatically
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream trustStoreFis = new FileInputStream("path_to_cacerts");
        trustStore.load(trustStoreFis, "changeit".toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        String id = Config.getConfigValue("SAML_ID");
        String location = Config.getConfigValue("SAML_LOCATION");
        String username = Config.getConfigValue("PF_ADMIN_USER");
        String password = Config.getConfigValue("PF_ADMIN_PASS");
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newBuilder().withConfig(clientConfig).sslContext(sslContext).build();
        WebTarget webTarget = client.target(Config.getConfigValue("PF_ADMIN_API_URI")).path(selector);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        invocationBuilder.header("X-Xsrf-Header", "PingFederate");
        
        // Add basic authentication header
        String auth = username + ":" + password;
        String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
        String authHeader = "Basic " + encodedAuth;
        invocationBuilder.header("Authorization", authHeader);

        List<List<String>> partitions = SelectorUtil.getPartitionedList(list, 100);
        Logger.info("{}: Partition Size: {}", _method_, partitions.size());
        int batchCount = 0;

        for (List<String> connSubLst : partitions) {
            batchCount++;
            String reqObj = SelectorUtil.buildRequestObjForSAML(connSubLst, selector, selectorName, id, location);
            Response stringRsp = invocationBuilder.put(Entity.entity(reqObj, MediaType.APPLICATION_JSON));
            status = stringRsp.getStatus();
            Logger.info("{}: Status: {}", _method_, status);
            if (stringRsp.getStatus() != 200) {
                Logger.debug("{}: Batch update failed", _method_, batchCount);
                Logger.error("{}: Batch update failed", _method_, batchCount);
                return status;
            }
        }
    } catch (Exception e) {
        Logger.debug("{}: Error executing the script. Message: {}", _method_, e.getMessage(), e);
        Logger.error("{}: Error executing the script. Message: {}", _method_, e.getMessage(), e);
    }
    return status;
}
