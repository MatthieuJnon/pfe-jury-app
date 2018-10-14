package fr.eseo.dis.joannomabeduneba.pfe_jury_app.utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import java.util.LinkedHashMap;

public class HttpUtilsTest {

    private static final String URL = "https://192.168.4.248/pfe/webservice.php?";

    @BeforeClass
    public static void setup() throws JSONException {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("q","LOGON");
        params.put("user","alberpat");
        params.put("pass","w872o32HkYAO");
        String type = "GET";

        JSONObject response = HttpUtils.executeRequest(type,URL,params);

        System.out.println(response.toString());

        Assert.assertNotNull(null, response);
        Assert.assertEquals("OK", response.get("result"));
        Assert.assertEquals("LOGON", response.get("api"));
    }


    @Test
    public void requestLIPRJTest() throws JSONException {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("q","LIPRJ");
        params.put("user","alberpat");
        params.put("token",HttpUtils.token);
        String type = "GET";

        JSONObject response = HttpUtils.executeRequest(type,URL,params);

        System.out.println(response.toString());

        Assert.assertNotNull(null, response);
        Assert.assertEquals("OK", response.get("result"));
        Assert.assertEquals("LIPRJ", response.get("api"));
    }

    @Test
    public void requestMYPRJTest() throws JSONException {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("q","MYPRJ");
        params.put("user","alberpat");
        params.put("token",HttpUtils.token);
        String type = "GET";

        JSONObject response = HttpUtils.executeRequest(type,URL,params);

        System.out.println(response.toString());

        Assert.assertNotNull(null, response);
        Assert.assertEquals("OK", response.get("result"));
        Assert.assertEquals("MYPRJ", response.get("api"));
    }

    @Test
    public void requestLIJURTest() throws JSONException {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("q","LIJUR");
        params.put("user","alberpat");
        params.put("token",HttpUtils.token);
        String type = "GET";

        JSONObject response = HttpUtils.executeRequest(type,URL,params);

        System.out.println(response.toString());

        Assert.assertNotNull(null, response);
        Assert.assertEquals("OK", response.get("result"));
        Assert.assertEquals("LIJUR", response.get("api"));
    }


    @Test
    public void requestMYJURTest() throws JSONException {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("q","MYJUR");
        params.put("user","alberpat");
        params.put("token",HttpUtils.token);
        String type = "GET";

        JSONObject response = HttpUtils.executeRequest(type,URL,params);

        System.out.println(response.toString());

        Assert.assertNotNull(null, response);
        Assert.assertEquals("OK", response.get("result"));
        Assert.assertEquals("MYJUR", response.get("api"));
    }

    @Test
    public void requestJYINFTest() throws JSONException {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("q","JYINF");
        params.put("user","alberpat");
        params.put("jury","0");
        params.put("token",HttpUtils.token);
        String type = "GET";

        JSONObject response = HttpUtils.executeRequest(type,URL,params);

        System.out.println(response.toString());

        Assert.assertNotNull(null, response);
        Assert.assertEquals("OK", response.get("result"));
        Assert.assertEquals("JYINF", response.get("api"));
    }

    @Test
    public void requestPOSTRTest() throws JSONException {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("q","POSTR");
        params.put("user","alberpat");
        params.put("proj","0");
        params.put("token",HttpUtils.token);
        String type = "GET";

        JSONObject response = HttpUtils.executeRequest(type,URL,params, true);

        Assert.assertNotNull(null, response);
        Assert.assertEquals("OK", response.get("result"));
        Assert.assertEquals("POSTR", response.get("api"));
    }

    @Test
    @Ignore
    public void requestPORTETest() throws JSONException {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("q","PORTE");
        params.put("user","alberpat");
        params.put("token",HttpUtils.token);
        String type = "GET";

        JSONObject response = HttpUtils.executeRequest(type,URL,params);

        System.out.println(response.toString());

        Assert.assertNotNull(null, response);
        Assert.assertEquals("OK", response.get("result"));
        Assert.assertEquals("PORTE", response.get("api"));
    }
}
