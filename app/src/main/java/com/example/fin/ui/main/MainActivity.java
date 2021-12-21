package com.example.fin.ui.main;

import static com.amap.api.maps.model.BitmapDescriptorFactory.getContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.example.fin.HttpUtil;
import com.example.fin.R;
import com.example.fin.dao.WeatherDataDao;
import com.example.fin.db.WeatherData;
import com.example.fin.db.WeatherDataBase;
import com.example.fin.service.impl.GetRequestServiceImpl;
import com.example.fin.ui.history.NotificationsFragment;
import com.example.fin.ui.weather.Weather;
import com.example.fin.ui.weather.p1;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.example.fin.databinding.ActivityMainBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class
MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static String defaultCity = "杭州";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        p1.weatherList.clear();
        for(String x: p1.citys.values()){
            p1.getCityWeather(x);
        }
        init();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_setting)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    /**
     * 初始化设置
     */
    public void init() {
        // 默认城市初始化
        SharedPreferences sp = getSharedPreferences("com.example.fin_preferences", Context.MODE_PRIVATE);
        defaultCity = sp.getString("city", "杭州");
    }

}