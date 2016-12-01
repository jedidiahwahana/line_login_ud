package com.linecorp.example.testing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import org.json.JSONObject;
import org.json.JSONArray;

import com.linecorp.example.testing.db.DbContract;
import com.linecorp.example.testing.db.PostgresHelper;

@RestController
@RequestMapping(value="/testing")
public class Login
{
    HttpClient c = HttpClientBuilder.create().build();
    
    PostgresHelper client = new PostgresHelper(DbContract.URL);
    
    // Act as server to get code, errorCode, and errorMessage from URL
    
    @RequestMapping(value="/profile", method=RequestMethod.GET)
    public ResponseEntity<String> world(@RequestParam(value = "code", required = false) String param1,
                                        @RequestParam(value = "errorCode", required = false) Integer param2,
                                        @RequestParam(value = "errorMessage", required = false) String param3)
        throws IOException
    {
        String html="";
        
        // Post code to Get access token
        String access_token = postForAccessToken(param1);
        
        // Get profile from JSONObject
        JSONObject jObjGet = getProfile(access_token);
        String display_name = jObjGet.getString("displayName");
        String midGet = jObjGet.getString("mid");
        String pict_url = jObjGet.getString("pictureUrl");
        String status_message = "N/A";
        if (jObjGet.isNull("statusMessage"))
            status_message = "null";
        
        if (checkData(midGet)){
            System.out.println("Data exists");
        } else {
            Map<String,Object> vals = new HashMap<>();
            
            vals.put("name", display_name);
            vals.put("mid", midGet);
            vals.put("status", status_message);
            vals.put("pict", pict_url);
            
            saveData(vals);
        }
        
        JSONObject jsonObjOfUser = new JSONObject();
        jsonObjOfUser.put("name", display_name);
        jsonObjOfUser.put("mid", midGet);
        jsonObjOfUser.put("status", status_message);
        jsonObjOfUser.put ("pict", pict_url);
        
        return new ResponseEntity<String>(jsonObjOfUser.toString(), HttpStatus.OK);
    }
    
    private String postForAccessToken (String code) throws IOException{
        
        HttpPost post = new HttpPost("https://api.line.me/v1/oauth/accessToken");
        
        //  Add header
        post.setHeader("Content-type", "application/x-www-form-urlencoded");
        
        // Insert parameters for request in ArrayList
        List<NameValuePair> content = new ArrayList<NameValuePair>();
        content.add(new BasicNameValuePair("grant_type", "authorization_code"));
        content.add(new BasicNameValuePair("code", code));
        content.add(new BasicNameValuePair("client_id", "1479418979"));
        content.add(new BasicNameValuePair("client_secret", "6c4078d3640c369aff2a43600e62586d"));
        content.add(new BasicNameValuePair("direct_uri", "http://google.com/"));
        
        // Hands ArrayList of parameters to the request
        post.setEntity(new UrlEncodedFormEntity(content));
        
        HttpResponse response = c.execute(post);
        
        // Get the response from the POST request
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null)
        {
            result.append(line);
        }
        
        // Change type of result to JSONObject
        JSONObject jObj = new JSONObject(result.toString());
        
        // Get the data from JSONObject
        String midRespons = jObj.getString("mid");
        String accessToken = jObj.getString("access_token");
        String tokenType = jObj.getString("token_type");
        int expiresIn = jObj.getInt("expires_in");
        String refreshToken = jObj.getString("refresh_token");
        String scopeRespons = "N/A";
        if (jObj.isNull("scope"))
            scopeRespons = "null" + "<br>";
        
        return accessToken;
    }
    
    private JSONObject getProfile(String accessToken) throws IOException{
        // Act as client with GET method
        HttpGet get = new HttpGet("https://api.line.me/v1/profile");
        
        //  Add header
        String getHeader = "Bearer " + accessToken;
        get.addHeader("Authorization", getHeader);
        
        HttpResponse responseGet = c.execute(get);
        
        // Get the response from the GET request
        BufferedReader brd = new BufferedReader(new InputStreamReader(responseGet.getEntity().getContent()));
        
        StringBuffer resultGet = new StringBuffer();
        String lineGet = "";
        while ((lineGet = brd.readLine()) != null) {
            resultGet.append(lineGet);
        }
        
        // Change type of resultGet to JSONObject
        JSONObject jObjGet = new JSONObject(resultGet.toString());
        
        return jObjGet;
    }
    
    private void saveData(Map<String,Object> dataValue){
        try {
            if (client.connect()) {
                System.out.println("DB connected");
                if (client.insert("profile", dataValue) == 1) {
                    System.out.println("Record added");
                }
            }
            
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Exception is raised ");
            e.printStackTrace();
        }
        catch(Exception e)
        {
            System.out.println("Unknown exception occurs");
        }
    }
    
    private boolean checkData(String mid){
        boolean dataExists = false;
        try {
            if (client.connect()) {
                dataExists = client.checkTable("profile", mid);
                System.out.println("Data Exists : " + dataExists);
            }
            
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Exception is raised ");
            e.printStackTrace();
        }
        catch(Exception e)
        {
            System.out.println("Unknown exception occurs");
        }
        
        return dataExists;
    }
    
    
};
