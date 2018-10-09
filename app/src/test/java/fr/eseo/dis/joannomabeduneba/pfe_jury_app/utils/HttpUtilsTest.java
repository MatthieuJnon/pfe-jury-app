package fr.eseo.dis.joannomabeduneba.pfe_jury_app.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpUtilsTest {

    @Test
    public void executeRequestTest(){
        String url = "https://192.168.4.248/pfe/webservice.php?";

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("q","LOGON");
        params.put("user","alberpat");
        params.put("pass","w872o32HkYAO");
        String type = "GET";

        String response = HttpUtils.executeRequest(type,url,params);

        Assert.assertNotNull(null, response);
    }

}
