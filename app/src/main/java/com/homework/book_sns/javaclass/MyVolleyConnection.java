package com.homework.book_sns.javaclass;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MyVolleyConnection {

    String TAG = "MyVolleyWebConnection";
    public static final String IP  = "15.164.105.239";
    public static final int CHAT_PORT = 8888;
    public static final int NOTI_PORT = 8889;

    /* --------------------------- */
    RequestQueue requestQueue; // 요청이 담길 queue
    Map<String, String> params = new HashMap<String, String>(); // 요청하면서 보낼 값들을 담아두는 map
    String volleyResponse; // 응답 값

    Response.Listener<String> listener_response;
    Response.ErrorListener listener_error;

    String url;
    int request_method; // 0이면 get, 1이면 post
    /* --------------------------- */



    public MyVolleyConnection(String url, int request_method, Context context) {
        this.url = url;

        if(!(request_method == 0 || request_method == 1)) {
            Log.d(TAG, "WebConnection: request_method가 0 또는 1이 아닙니다.");
            return;
        }
        this.request_method = Request.Method.POST;
        requestQueue = Volley.newRequestQueue(context);
    }

    public MyVolleyConnection(int request_method, Context context) {
        if(!(request_method == 0 || request_method == 1)) {
            Log.d(TAG, "WebConnection: request_method가 0 또는 1이 아닙니다.");
            return;
        }
        this.request_method = Request.Method.POST;
        requestQueue = Volley.newRequestQueue(context);

    }

    public String setURL(String file_name) {
        url = "http://"+this.IP+"/book_sns"+file_name;
        return url;
    }

    public void addParams (String key, String value) {
        params.put(key, value);
    }

    public void setVolley(Response.Listener<String> res, Response.ErrorListener error) {
        listener_response = res;
        listener_error = error;
    }

    public void requestVolley() {

        StringRequest request = new StringRequest(request_method, url, listener_response, listener_error) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        requestQueue.add(request);

    }

    public String getVolleyResponse() {
        return volleyResponse;
    }

}
