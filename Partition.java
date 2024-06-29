public static String makeRequestBypassSSL(String apiUrl, String username, String password, String body) throws Exception {
    String OAuthToken = null;
    final SSLConnectionSocketFactory sslsf;
    try {
        sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(), NoopHostnameVerifier.INSTANCE);
        final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslsf)
                .build();
        final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(100);
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setConnectionManager(cm)
                .build();

        HttpPut httpPut = new HttpPut(apiUrl);
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);
        httpPut.setHeader("Authorization", authHeader);

        // Add the body to the PUT request
        StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
        httpPut.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPut);

        if (null != response && response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
            HttpEntity responseEntity = response.getEntity();
            OAuthToken = EntityUtils.toString(responseEntity);
        } else {
            System.out.println("Error, Unable to retrieve response from the request");
        }
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    return OAuthToken;
}
