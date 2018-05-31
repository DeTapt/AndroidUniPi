package com.androidunipi.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
 

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


 
@SuppressWarnings("deprecation")
public class JSONParser {
 
    static InputStream is = null;
    static JSONObject jObj = null;
    static JSONArray jArr = null;
    static String json = "";
 
    // constructor
    public JSONParser() {
 
    }
 
    // function get json from url
    // by making HTTP POST or GET mehtod
	public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params, String type) {
 
        // Making HTTP request
        try {
 
            // check for request method
            if(method == "POST"){
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
                
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
               
                
            }else if(method == "GET"){
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                if(params != null){
                	String paramString = URLEncodedUtils.format(params, "utf-8");
                    url += "?" + paramString;
                }
                
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);

                
                
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }           
            else if(method == "DELETE"){
            	//request method DELETE
            	DefaultHttpClient httpClient = new DefaultHttpClient();
            	String paramString = URLEncodedUtils.format(params, "utf-8");
            	url += "?" + paramString;
            	HttpDelete httpDelete = new HttpDelete(url);

            	
            	HttpResponse httpResponse = httpClient.execute(httpDelete);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }
            else if(method == "PUT"){
            	DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPut httpPut = new HttpPut(url);
                httpPut.setEntity(new UrlEncodedFormEntity(params));
                
                
                HttpResponse httpResponse = httpClient.execute(httpPut);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
            		is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            
            if(type.equals("STRING")){
            	sb.append(reader.readLine());
            	is.close();
            	json = sb.toString();
            	if(json.equals("\"SUCCESS\"")){
            		json = "1";
            	}
            	jObj = new JSONObject();
            	
            	return jObj.put("SUCCESS", json);
            }
            else{
            	while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            	is.close();
                json = sb.toString();
            }
       } catch (Exception e) {
            
        }
 
        
        if(type.equals("OBJECT")){
        	// try parse the string to a JSON object
            try {
                jObj = new JSONObject(json);
            } catch (JSONException e) {
               
            }
         // return JSON String
           
        }
        else if (type.equals("ARRAY")){
        	try {
                jArr = new JSONArray(json);
                
            } catch (JSONException e) {
               
            }
         // return JSON String
        	
            try {
				jObj = jArr.getJSONObject(0);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
			
			}
        }
        
 
        return jObj;
 
    }
   
}