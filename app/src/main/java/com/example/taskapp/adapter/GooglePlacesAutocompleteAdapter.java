package com.example.taskapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.taskapp.BaseApp;
import com.example.taskapp.R;
import com.example.taskapp.map_server.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${hlink} on 18/3/16.
 */
public class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {

    private static String LOG_TAG = "GooglePlacesAutoc";
    private ArrayList<String> resultList;
    public static ArrayList<String> mainTextList;
     public static ArrayList<String> secondoryTextList;
    private static Context mContext;
    int layout;
    TextView txtTitle;
    Activity activity;


    public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId, Activity activity) {
        super(context, textViewResourceId);
        mContext = context;
        layout=textViewResourceId;
        this.activity=activity;
    }

    @Override
    public int getCount() {
        if(resultList != null)
            return resultList.size();
        else
            return 0;
    }

    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(layout, parent, false);

        txtTitle = (TextView) view.findViewById(R.id.txtAddress);
        TextView txtArea= (TextView) view.findViewById(R.id.txtarea);

        if(mainTextList.size()>position)
        {
            if(mainTextList!=null && secondoryTextList!=null && mainTextList.size()>0 && secondoryTextList.size()>0)
            {
                try {

                       txtTitle.setText(mainTextList.get(position));
                       txtArea.setText(secondoryTextList.get(position));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return view;
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }


    public void callapi(String s)
    {
        resultList = autocomplete(s.toString());
    }

    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;


        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(Constant.PLACES_API_BASE + Constant.TYPE_AUTOCOMPLETE + Constant.OUT_JSON);
            sb.append("?key=AIzaSyB2tihwL918R-w2ahZuRQSCZBW1pkq523s");
            //sb.append("&components=country:au");
            if(BaseApp.currentRide.getCurrentLatitude()!=null && BaseApp.currentRide.getCurrentLOngitude()!=null)
            {
                sb.append("&location=" + BaseApp.currentRide.getCurrentLatitude()+ "," + BaseApp.currentRide.getCurrentLOngitude());
            }

            sb.append("&input=" + URLEncoder.encode(input, "utf8"));


            URL url = new URL(sb.toString());

            System.out.println("URL: " + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            mainTextList=new ArrayList<String>(predsJsonArray.length());
            secondoryTextList=new ArrayList<String>(predsJsonArray.length());


            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                mainTextList.add(predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text"));

                if(predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").has("secondary_text"))
                {
                    secondoryTextList.add(predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("secondary_text"));
                }
                else{
                    secondoryTextList.add("");
                }




            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    public static String getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        String data = "";

        try {
            // address = coder.getFromLocationName(strAddress, 5);
            address = coder.getFromLocationName(strAddress, 15);
            if (address == null) {
                return data;
            }
            Address location = address.get(0);

            System.out.println("============" + location.getLatitude() + "===================" + location.getLongitude() + "====================");
            System.out.println("=====Local=======" + location.getSubLocality());
            System.out.println("=====City=======" + location.getLocality());
            System.out.println("=====State=======" + location.getAdminArea());
            System.out.println("=====PinCode=======" + location.getPostalCode());
            System.out.println("=====Country=======" + location.getCountryName());

            data = location.getLatitude() + ",,," + location.getLongitude() + ",,," + location.getSubLocality() + ",,," + location.getLocality() + ",,," + location.getAdminArea() + ",,," +
                    location.getPostalCode() + ",,," + location.getCountryName();


        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return data;

    }



}
