/**
 * 
 */
package org.fabryprog.iota.balancer;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.camel.component.http4.HttpClientConfigurer;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

/**
 * @author Fabrizio Spataro <fabryprog@gmail.com>
 */
@SuppressWarnings("deprecation")
public class FakeHttpsConfigurer implements HttpClientConfigurer {

    @Override 
    public void configureHttpClient(HttpClientBuilder clientBuilder) { 
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] a, String b) throws CertificateException {
                    return true;
                }
            });
            
            SSLContext sslContext = builder.build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new AllowAllHostnameVerifier());

            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                     .register("https", sslsf)
                     .build();

            HttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(registry);

            clientBuilder.setConnectionManager(connectionManager);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to configure TrustingHttpClientConfigurer", e);
        }
    }
}