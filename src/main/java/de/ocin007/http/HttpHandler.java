package de.ocin007.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class HttpHandler {

    private CloseableHttpClient httpClient;
    private List<BasicNameValuePair> params;
    private List<BasicNameValuePair> header;
    private String url;
    private JSONObject lastResponse;

    public HttpHandler(String url) {
        this.httpClient = HttpClients.createDefault();
        this.url = url;
        this.params = new LinkedList<>();
        this.header = new LinkedList<>();
    }

    public void addParam(String key, String value) {
        this.params.add(new BasicNameValuePair(key, value));
    }

    public void addHeader(String key, String value) {
        this.header.add(new BasicNameValuePair(key, value));
    }

    public JSONObject getLastResponse() {
        return this.lastResponse;
    }

    public JSONObject get() {
        try {
            HttpGet httpGet = new HttpGet(this.getQueryURL());
            for (BasicNameValuePair pair: this.header) {
                httpGet.addHeader(pair.getName(), pair.getValue());
            }
            HttpResponse response = this.httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            this.setResponse(entity);
            return this.lastResponse;
        } catch (Exception e) {
            return this.errorMsg(e);
        }
    }

    private void setResponse(HttpEntity entity) throws IOException {
        if (entity != null) {
            InputStream instream = entity.getContent();
            String r = new Scanner( instream ).useDelimiter( "\\Z" ).next();
            this.lastResponse = (JSONObject) JSONValue.parse(r);
        } else {
            this.lastResponse = (JSONObject) JSONValue.parse("{\"error\":\"no response\"}");
        }
    }

    public String getQueryURL() {
        String queryUrl = this.url;
        if(!this.params.isEmpty()) {
            queryUrl += "?" + this.createQuery();
        }
        return queryUrl;
    }

    private String createQuery() {
        StringBuilder query = new StringBuilder();
        Boolean wasFirst = true;
        for (BasicNameValuePair pair: this.params) {
            if(wasFirst) {
                wasFirst = false;
                query.append(pair.getName()).append("=").append(pair.getValue());
            } else {
                query.append("&").append(pair.getName()).append("=").append(pair.getValue());
            }
        }
        return query.toString();
    }

    public JSONObject post() {
        try {
            HttpPost httpPost = new HttpPost(this.url);
            httpPost.setEntity(new UrlEncodedFormEntity(this.params, "UTF-8"));
            for (BasicNameValuePair pair: this.header) {
                httpPost.addHeader(pair.getName(), pair.getValue());
            }
            HttpResponse response = this.httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            this.setResponse(entity);
            return this.lastResponse;
        } catch (Exception e) {
            return this.errorMsg(e);
        }
    }

    private JSONObject errorMsg(Exception e) {
        this.lastResponse = (JSONObject) JSONValue.parse(
                "{\"error\":\""+ e.getMessage() +"\",\"stacktrace\":\""+ Arrays.toString(e.getStackTrace()) +"\"}"
        );
        return this.lastResponse;
    }
}
