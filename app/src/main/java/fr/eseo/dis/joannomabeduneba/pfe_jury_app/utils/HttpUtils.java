package fr.eseo.dis.joannomabeduneba.pfe_jury_app.utils;

import android.util.Log;

import com.google.common.io.ByteStreams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import fr.eseo.dis.joannomabeduneba.pfe_jury_app.Application;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.R;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.PFEDatabase;
import fr.eseo.dis.joannomabeduneba.pfe_jury_app.data.User;

/**
 * Create and maintain the connexion to the API SoManager.
 * Provide functions to easily send request.
 *
 * @author Baptiste Beduneau (baptiste.beduneau@reseau.eseo.fr)
 */

public class HttpUtils {

    private static final Logger LOGGER = Logger.getLogger(HttpUtils.class.getName());
    public static final String URL = "https://192.168.4.248/pfe/webservice.php?";
    private static final String PATHFILE = "src/main//res/drawable/";

    private static HttpsURLConnection connection = null;
    public static String token = null;


    public static boolean requestOK(JSONObject json) {
        try {
            return json.get("result").equals("OK");
        } catch (JSONException e) {
            LOGGER.log(Level.SEVERE, "Cannot parse json response", e);
            return false;
        }
    }

    public static String requestFromJson(JSONObject json, String request) {
        try {
            return (String) json.get(request);
        } catch (JSONException e) {
            LOGGER.log(Level.SEVERE, "Cannot parse json response", e);
            return null;
        }
    }

    public static String requestFromJsonInfo(JSONObject json, String request) {
        try {
            return (String) ((JSONArray) json.get("info")).getJSONObject(0).get(request);
        } catch (JSONException e) {
            LOGGER.log(Level.SEVERE, "Cannot parse json response", e);
            return "default";
        }
    }


    public static String revalidateToken(User u) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("q", "LOGON");
        params.put("user", u.getName());
        params.put("pass", u.getPassword());

        JSONObject res = executeRequest("GET", URL, params);

        u.setToken(requestFromJson(res, "token"));

        PFEDatabase.getInstance(Application.getAppContext())
                .getUserDao()
                .update(u);

        return requestFromJson(res, "token");
    }

    /**
     * Execute a HTTPS request to the url provided.
     *
     * @param type       Type of the request (GET, POST,..)
     * @param targetURL  URL of the request
     * @param parameters Parameters for the request
     * @return The result of the request from the server.
     */

    public static JSONObject executeRequest(String type, String targetURL, LinkedHashMap<String, String> parameters, boolean isPoster) {

        try {
            connect(type, targetURL, parameters);

            final InputStream input = connection.getInputStream();

            if (isPoster) {
                try {
                    parsePng(input, "poster.png");
                } catch (IOException e) {
//                  This probably means that there is no png and that the token was invalid
                    User u = PFEDatabase.getInstance(Application.getAppContext())
                            .getUserDao()
                            .getUserFromName(parameters.get("user")).get(0);
                    String token = revalidateToken(u);
                    parameters.put("token", token);
                    return executeRequest(type, targetURL, parameters);
                }
                return new JSONObject().put("result", "OK").put("api", "POSTR");
            } else {

                final JSONObject res = parseJson(input);

                if(res.get("result").equals("KO")) {

                    User u = PFEDatabase.getInstance(Application.getAppContext())
                            .getUserDao()
                            .getUserFromName(parameters.get("user")).get(0);
                    String token = revalidateToken(u);
                    parameters.put("token", token);
                    return executeRequest(type, targetURL, parameters);
                }

                return res;
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot execute request", e);
            return null;
        } catch (JSONException e) {
            LOGGER.log(Level.SEVERE, "Cannot generate json response", e);
            return null;
        }
    }

    /**
     * Execute a HTTPS request to the url provided.
     *
     * @param type      Type of the request (GET, POST,..)
     * @param targetURL URL of the request
     * @return The result of the request from the server.
     */
    public static JSONObject executeRequest(String type, String targetURL) {
        return executeRequest(type, targetURL, null, false);
    }

    /**
     * Execute a HTTPS request to the url provided.
     *
     * @param type      Type of the request (GET, POST,..)
     * @param targetURL URL of the request
     * @param isPoster  Request the poster
     * @return The result of the request from the server.
     */
    public static JSONObject executeRequest(String type, String targetURL, boolean isPoster) {
        return executeRequest(type, targetURL, null, isPoster);
    }

    /**
     * Execute a HTTPS request to the url provided.
     *
     * @param type       Type of the request (GET, POST,..)
     * @param targetURL  URL of the request
     * @param parameters Parameters for the request
     * @return The result of the request from the server.
     */
    public static JSONObject executeRequest(String type, String targetURL, LinkedHashMap<String, String> parameters) {
        return executeRequest(type, targetURL, parameters, false);
    }


    /**
     * Parse the request and generate a png image from the bytes.
     *
     * @param input The input stream
     * @param name  Name of the file, should contain the extension
     */
    private static void parsePng(final InputStream input, String name) throws IOException {
        File file = new File(PATHFILE + name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(ByteStreams.toByteArray(input));
            fos.flush();
            fos.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot generate png", e);
            throw new IOException();
        }
    }

    /**
     * Parse the request and generate the json response.
     *
     * @param input The input stream
     * @return The json response
     */
    private static JSONObject parseJson(final InputStream input) {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(input));

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            JSONObject jsonObject = new JSONObject(response.toString());

            if (requestOK(jsonObject) && jsonObject.get("api").equals("LOGON"))
                token = jsonObject.get("token").toString();

            return jsonObject;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot execute request", e);
            return null;
        } catch (JSONException e) {
            LOGGER.log(Level.SEVERE, "Cannot parse json response", e);
            return null;
        }
    }


    /**
     * Open a connection to the server
     *
     * @param type       Type of the request (GET,POST,..)
     * @param targetURL  Url of the server
     * @param parameters Parameters for the request
     */
    private static void connect(final String type, final String targetURL,
                                final LinkedHashMap<String, String> parameters) {

        LOGGER.log(Level.INFO, "Opening connection to the server");

        // SSL Factory for the https certificate
        SSLSocketFactory socketFactory = trustCA().getSocketFactory();

        if (socketFactory == null) {
            return;
        }


        // Regroup params
        String parameterUrl = "";

        if (parameters != null) {
            // Add parameters to the url
            for (Map.Entry<String, String> mapString : parameters.entrySet()) {
                parameterUrl += mapString.getKey() + "=" + mapString.getValue() + "&";
            }

            parameterUrl = parameterUrl.substring(0, parameterUrl.length() - 1);
        }

        // Open the connexion
        String finalUrl = targetURL + parameterUrl;

        URL url;
        try {
            url = new URL(finalUrl);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(socketFactory);
            connection.setRequestProperty("Content-Type",
                    "application/json");

            connection.setRequestMethod(type);
            connection.setDoOutput(true);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Cannot connect to the target : %s ",
                    targetURL), e);
        }
    }


    /**
     * Trust the certificate from soManager
     * From exemple : https://www.washington.edu/itconnect/security/ca/load-der.crt
     *
     * @return The context for the certificate, should be used during the connection
     */
    private static SSLContext trustCA() {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = Application.getAppContext().getResources().openRawResource(R.raw.chain);
            Certificate ca;
            ca = cf.generateCertificate(caInput);
            LOGGER.log(Level.INFO, "Using certificate : " + ((X509Certificate) ca).getSubjectDN());
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            return context;

        } catch (IOException | NoSuchAlgorithmException | KeyStoreException |
                KeyManagementException | CertificateException e) {
            LOGGER.log(Level.SEVERE, "Cannot trust the certificate", e);
            return null;
        }
    }


}
