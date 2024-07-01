private int addIdsToSelector(List<String> list, String excelHeader, String selectorId) {
        // Bypass SSL certificate validation
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(X509Certificate[] certs, String authType) { }
            }}, new java.security.SecureRandom());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Configure the Jersey client
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property("jersey.config.client.ssl.context", sslContext);

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("username", "password");
        clientConfig.register(feature);

        Client client = ClientBuilder.newBuilder().withConfig(clientConfig).sslContext(sslContext).hostnameVerifier((s1, s2) -> true).build();

        WebTarget target = client.target("https://your-service-url.com/api");

        // Create the request
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.post(javax.ws.rs.client.Entity.entity(list, MediaType.APPLICATION_JSON));

        return response.getStatus();
    }
