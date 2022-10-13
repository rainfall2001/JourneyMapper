package com.example.mapper.location;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mapper.R;
import com.example.mapper.helper.RequestListener;
import com.example.mapper.model.Address;
import com.example.mapper.model.TravelMethod;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is responsible for retrieving the time between two address using the Duration API from google and
 * setting it as the interval.
 * @author Rhane Mercado
 */
public class DirectionRequest {

    private RequestListener listener;
    static private long interval = 0;

    public DirectionRequest(Context context, Address start, Address end, TravelMethod method){
        this.listener = null;
        getRequest(context, start, end, method);
    }

    public void setListener(RequestListener listener){
        this.listener = listener;
    }

    public static long getInterval() {
        return interval;
    }

    /**
     * This method gets the url link by concatenating strings specific to the variables passed in.
     * @param start Start Address
     * @param end End Address
     * @param method Travel Method
     * @return String
     */
    private String getURL(Context context, Address start, Address end, TravelMethod method){
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + start.getLatitude() + "," + start.getLongitude() + "&destinations=" + end.getLatitude() + "," + end.getLongitude() + "&key=" + context.getString(R.string.google_maps_key);
        if(method.equals(TravelMethod.WALK) || method.equals(TravelMethod.SCOOTER)){
            url += "&mode=walking";
        } else if (method.equals(TravelMethod.ESCOOTER) || method.equals(TravelMethod.EBIKE) || method.equals(TravelMethod.BIKE)){
            url += "&mode=bicycling";
        } else if (method.equals(TravelMethod.BUS)){
            url += "&mode=transit";
        }
        return url;
    }

    /**
     * This method executes a volley request to the duration api by google. This will get the time, in minutes, between two points.
     * @param context The context of the class
     * @param start Start Address
     * @param end End Address
     * @param method Travel Method
     */
    private void getRequest(Context context, Address start, Address end, TravelMethod method){
        //queue a new volley request
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getURL(context, start, end, method);
        //use a get request to request from the duration api
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //get the duration
                    JSONObject duration = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration");
                    int value = duration.getInt("value");
                    //minutes to millis
                    interval = value * 1000;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //once the request is done, call the listener
                if(listener != null){
                    listener.success();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(listener != null){
                    //cannot make request, call the listener
                    listener.failed();
                }

            }
        });
        queue.add(request);
    }
}
