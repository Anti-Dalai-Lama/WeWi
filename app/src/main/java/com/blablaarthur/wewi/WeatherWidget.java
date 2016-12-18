package com.blablaarthur.wewi;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;


public class WeatherWidget extends AppWidgetProvider {

    public static final String WEATHER_UPDATE = "WEATHER_UPDATE";
    static AppWidgetManager myWidgetManager;
    static String temp = "?";
    static String des = "?";
    static RemoteViews views;
    static int widgetId;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Log.d("A_R_T", "update");
        views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);


        Intent intentSync = new Intent(context, WeatherWidget.class);
        intentSync.setAction(WEATHER_UPDATE);
        PendingIntent pendingSync = PendingIntent.getBroadcast(context,0, intentSync, 0);
        views.setOnClickPendingIntent(R.id.widgetIcon,pendingSync);

        myWidgetManager = appWidgetManager;

        widgetId = appWidgetId;
        if(InternetReceiver.isOnline(context)) {
            WeatherControlla wc = new WeatherWidget.WeatherControlla();
            wc.execute("http://api.worldweatheronline.com/premium/v1/weather.ashx?key=9c118fa8f39b44ae88b190557161612&q=50,36.25&format=json");
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent.getAction().equals(WEATHER_UPDATE)){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context.getApplicationContext(), WeatherWidget.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            if (appWidgetIds != null && appWidgetIds.length > 0) {
                onUpdate(context, appWidgetManager, appWidgetIds);
            }
        }
    }

    public static class WeatherControlla extends AsyncTask<String, Void, String> {
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
                e.printStackTrace();
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
                e.printStackTrace();
            }
            return res.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                temp = temp_C + "\u00B0C";
                des = desc;

                views.setTextViewText(R.id.widgetTemp, temp);
                views.setTextViewText(R.id.widgetWeth, des);
                views.setImageViewBitmap(R.id.widgetIcon, bitmap);

                int pixel = bitmap.getPixel(1,1);
                views.setInt(R.id.widgetLayout, "setBackgroundColor", pixel);

                myWidgetManager.updateAppWidget(widgetId, views);
            }
            catch (Exception e){
                Log.d("A_R_T", e.toString());
                e.printStackTrace();
            }
        }
    }
}

