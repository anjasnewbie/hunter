/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hunter.Misc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import static org.apache.http.HttpHeaders.USER_AGENT;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 *
 * @author xblux
 */
public class Utils {

    public static void main(String[] args) {
        Utils utils = new Utils();
        utils.CekWaf("http://jagoanmuda.xl.co.id");
    }

    public void CekWaf(String domain) {
        try {
        
            //202.152.224. 112.215.105 112.215.81 112.215.197
            String url = domain + "/?id=x'+or+'x'='x";
            System.out.println(domain);

            int timeout = 5;
RequestConfig config = RequestConfig.custom()
  .setConnectTimeout(timeout * 1000)
  .setConnectionRequestTimeout(timeout * 1000)
  .setSocketTimeout(timeout * 1000).build();
//CloseableHttpClient client = 
//  HttpClientBuilder.create().setDefaultRequestConfig(config).build();

CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(config).
                    setHostnameVerifier(new AllowAllHostnameVerifier()).
                    setSslcontext(new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy()
                    {
                        public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
                        {
                            return true;
                        }
                    }).build()).build();

            HttpGet request = new HttpGet(url);

            // add request header
            request.addHeader("User-Agent", "DirBuster-0.12");
            HttpResponse response = client.execute(request);

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            if (response.getStatusLine().getStatusCode() == 200) {
                //cek apa di block WAF f5
                if (result.toString().contains("rejected")) {
                    //blocked
                    System.out.println(domain + " waf F5");
                    return;
                }
            }
            if (response.getStatusLine().getStatusCode() == 403) {
                if (result.toString().contains("Request blocked")) {
                    System.out.println(domain + " waf cloudfront");
                    return;
                }
            }

            if (response.getStatusLine().getStatusCode() == 500) {
                if (result.toString().contains("rejected")) {
                    System.out.println(domain + " waf fornigate");
                    return;
                }
            }
            System.out.println(domain + " No WAF");

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
