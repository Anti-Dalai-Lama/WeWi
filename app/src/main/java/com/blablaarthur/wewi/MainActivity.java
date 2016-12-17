package com.blablaarthur.wewi;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.widget.ImageView;
        import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static TextView temp;
    static TextView weth;
    static ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        icon = (ImageView) findViewById(R.id.icon);
        temp = (TextView) findViewById(R.id.temp);
        weth = (TextView) findViewById(R.id.weth);

        WeatherControlla wc = new WeatherControlla();
        wc.execute("http://api.worldweatheronline.com/premium/v1/weather.ashx?key=9c118fa8f39b44ae88b190557161612&q=50,36.25&format=json");
    }

}
