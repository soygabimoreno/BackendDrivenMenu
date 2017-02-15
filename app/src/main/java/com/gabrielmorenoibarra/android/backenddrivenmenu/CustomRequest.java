package com.gabrielmorenoibarra.android.backenddrivenmenu;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom request to use through Volley.
 * Created by Gabriel Moreno on 2017-13-02.
 */
public class CustomRequest extends Request<JSONObject> {

    private Listener<JSONObject> listener;
    private Map<String, String> params;
    private String user, password;

    /**
     * Constructor.
     * @param user User for making requests to the protected directory.
     * @param password Password for making requests to the protected directory.
     * @param method Integer with the type of request.<br>
     * DEPRECATED_GET_OR_POST = -1;<br>
     * GET = 0<br>
     * POST = 1<br>
     * PUT = 2<br>
     * DELETE = 3<br>
     * HEAD = 4<br>
     * OPTIONS = 5<br>
     * TRACE = 6<br>
     * PATCH = 7<br>
     * @param url URL service address.
     * @param params Request parameters.
     * @param shouldCache If it is false, never use cache memory. Always make request to the server.
     * @param responseListener Successful response listener. It will be waiting the server.
     * @param errorListener Wrong response listener. When something fails.
     */
    public CustomRequest(String user, String password, int method, String url, Map<String, Object> params,
                         boolean shouldCache, Listener<JSONObject> responseListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.user = user;
        this.password = password;
        this.listener = responseListener;

        Map<String, String> jsonParams = new HashMap<>();
        try { // COMMENT: Here it would be interesting to encrypt the message
            jsonParams.put(Keys.MESSAGE, new JSONObject(params).toString()); // 'new JSONObject(params).toString()' is faster than 'new Gson().toJson(params)'
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.params = jsonParams;

        setShouldCache(shouldCache);
    }

    /**
     * @return the request parameters.
     * @throws AuthFailureError
     */
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + Base64.encodeToString((user + ":" + password).getBytes(), Base64.NO_WRAP));
        return headers;
    }

    /**
     * Put the <code>Listener</code> to listen the deliver response of the server.
     * @param response Response to deliver.
     */
    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }

    /**
     * It parses a <code>NetworkResponse</code> to a <code>JSONObject</code> <code>Response</code>.
     * @param response A NetworkResponse from the server.
     * @return a <code>JSONObject</code> <code>Response</code>.
     */
    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        String charsetName = HttpHeaderParser.parseCharset(response.headers); // Get the charset name. It will be UTF-8
        try {
            String jsonString = new String(response.data, charsetName); // We obtain the serialized JSON (encrypted or not)
            return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}