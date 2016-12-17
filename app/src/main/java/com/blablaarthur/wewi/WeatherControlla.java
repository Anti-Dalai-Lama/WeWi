package com.blablaarthur.wewi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Артур on 16.12.2016.
 */

public class WeatherControlla extends AsyncTask<String, Void, String> {
    StringBuilder res = new StringBuilder();

    String temp_C;
    Bitmap bitmap;
    String desc;

    URL url;
    URLConnection urlConnect;
    //http://api.worldweatheronline.com/premium/v1/weather.ashx?key=9c118fa8f39b44ae88b190557161612&q=50,36.25&format=json

    @Override
    protected String doInBackground(String[] urls) {
        try{
            Log.d("A_R_T", urls[0]);
            url = new URL(urls[0]);
            urlConnect = url.openConnection();
            InputStream in = urlConnect.getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = r.readLine()) != null){
                res.append(line);
            }
            Log.d("A_R_T", res.toString());
        }
        catch (Exception e){
            Log.d("A_R_T", e.toString());
        }

        try {
            String data = (new JSONObject(res.toString())).getString("data");
            String currentWeather = (new JSONObject(data)).getString("current_condition");
            JSONObject ob = new JSONArray(currentWeather).getJSONObject(0);

            temp_C = ob.getString("temp_C");
            String icon_url = new JSONArray(ob.getString("weatherIconUrl")).getJSONObject(0).getString("value");
            desc = new JSONArray(ob.getString("weatherDesc")).getJSONObject(0).getString("value");
            Log.d("A_R_T", temp_C + " " + icon_url + " " + desc);

            URL url =new URL(icon_url);
            URLConnection connection= url.openConnection();
            InputStream inputStream= connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);

        }
        catch (Exception e){
            Log.d("A_R_T", e.toString());
        }
        return res.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            if(MainActivity.temp != null) {
                MainActivity.temp.setText(temp_C);
                MainActivity.weth.setText(desc);
                MainActivity.icon.setImageBitmap(bitmap);
            }
            if(WeatherWidget.views != null){
                WeatherWidget.views.setTextViewText(R.id.widgetTemp, temp_C);
                WeatherWidget.views.setTextViewText(R.id.widgetWeth, desc);
                WeatherWidget.views.setImageViewBitmap(R.id.widgetIcon, bitmap);
            }
        }
        catch (Exception e){
            Log.d("A_R_T", e.toString());
        }
    }
}
