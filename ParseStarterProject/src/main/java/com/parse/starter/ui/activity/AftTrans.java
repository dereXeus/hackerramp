package com.parse.starter.ui.activity;

import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import java.security.cert.Certificate;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class AftTrans {
    private static final String TAG = "MyActivity";
    private static String API_KEY = "D702LX0KMBVK6UDPKE4121SPkRCx-itsDKwg3oHOljBBOk6DY";
    private static String SHARED_SECRET = "NTUO58Dl5WL91+OR3Y+/l18keLwoqS7PMRJyYYZ-";

    public static void doTrans(){

        trustEveryone();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //String body = readBody("AFT.json");

        //StringBuilder sb = new StringBuilder();

        //String target = "https://qaperf.api.visa.com/pm/ft/AccountFundingTransactions?apiKey=" + API_KEY;
        String target = "https://google.com/pm/ft/AccountFundingTransactions?apiKey=" + API_KEY;

        HttpsURLConnection urlConnection=null;
        try {

            JSONObject jsonParam = createAftDefaultJson();
            String body = jsonParam.toString();

            URL url = new URL(target);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

            urlConnection.setRequestProperty("content-type", "application/json");
            urlConnection.setRequestProperty("accept", "application/vnd.visa.FundsTransfer.v1+json");
            String xPayToken = getXPayToken("ft/AccountFundingTransactions",
                    "apiKey=" + API_KEY,
                    body);
            urlConnection.setRequestProperty("x-pay-token", xPayToken);

            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            urlConnection.connect();



            OutputStreamWriter wr = new OutputStreamWriter (
                    urlConnection.getOutputStream ());
            wr.write(body);
            wr.flush ();
            wr.close ();

            //Get Response
            InputStream is = urlConnection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            Log.i(TAG, response.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } finally{
            if(urlConnection!=null)
                urlConnection.disconnect();
        }

    }

    private static JSONObject createAftDefaultJson() {

        JSONObject temp = new JSONObject();
        try {
            temp.put("SystemsTraceAuditNumber", Integer.valueOf(300259));
            temp.put("RetrievalReferenceNumber", "407509300259");
            temp.put("DateAndTimeLocalTransaction", "2021-10-26T21:32:52");
            temp.put("AcquiringBin", Integer.valueOf(409999));
            temp.put("AcquirerCountryCode", "101");
            temp.put("SenderPrimaryAccountNumber", "4005520000011126");
            temp.put("SenderCardExpiryDate", "2018-06");
            temp.put("SenderCurrencyCode", "USD");
            temp.put("Amount", "112.00");
            temp.put("Surcharge", "2.00");
            temp.put("Cavv", "0000010926000071934977253000000000000000");
            temp.put("ForeignExchangeFeeTransaction", "10.00");
            temp.put("BusinessApplicationID", "PP");
            temp.put("MerchantCategoryCode",  Integer.valueOf(6012));

                JSONObject cardAccceptor = new JSONObject();
                cardAccceptor.put("Name","Acceptor 1");
                cardAccceptor.put("TerminalId","365539");
                cardAccceptor.put("IdCode","VMT200911026070");

                    JSONObject address = new JSONObject();
                    address.put("State","CA");
                    address.put("County","081");
                    address.put("Country","USA");
                    address.put("ZipCode","94404");

                cardAccceptor.put("Address",address);

            temp.put("CardAcceptor",cardAccceptor);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    private static String getXPayToken(String apiNameURI, String queryString, String requestBody) throws SignatureException {
        String timestamp = getTimestamp();
        String sourceString = SHARED_SECRET + timestamp + apiNameURI + queryString + requestBody;
        String hash = sha256Digest(sourceString);
        String token = "x:" + timestamp + ":" + hash;
        return token;
    }
    public static String getTimestamp() {
        Date today = new Date();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yy:HH:mm:SS");
        String date = DATE_FORMAT.format(today);
        long unix_timestamp = strDateToUnixTimestamp(date);
        String unixTs = String.valueOf(unix_timestamp);
        return unixTs;
    }

    private static long strDateToUnixTimestamp(String dt) {
        DateFormat formatter;
        Date date = null;
        long unixtime;
        formatter = new SimpleDateFormat("dd-MM-yy:HH:mm:SS");
        try {
            date = formatter.parse(dt);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        unixtime = date.getTime() / 1000L;
        return unixtime;
    }

    public static String sha256Digest(String data) throws SignatureException {
        return getDigest("SHA-256", data, true);
    }

    private static String getDigest(String algorithm, String data, boolean toLower) throws
            SignatureException {
        try {
            MessageDigest mac = MessageDigest.getInstance(algorithm);
            mac.update(data.getBytes("UTF-8"));
            return toLower ?
                    new String(toHex(mac.digest())).toLowerCase() : new String(toHex(mac.digest()));
        } catch (Exception e) {
            throw new SignatureException(e);
        }
    }

    private static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    private static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }});
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }

    }

}
