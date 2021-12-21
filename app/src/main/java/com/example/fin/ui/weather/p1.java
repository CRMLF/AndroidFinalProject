package com.example.fin.ui.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fin.HttpUtil;
import com.example.fin.R;
import com.example.fin.ToastUtil;
import com.example.fin.adapter.CityWeather;
import com.example.fin.adapter.CityWeatherAdapter;
import com.example.fin.service.impl.GetRequestServiceImpl;
import com.example.fin.ui.main.MainActivity;
import com.example.fin.ui.map.HomeViewModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class p1 extends Fragment {
    ImageView ivWeatherImg;
    TextView tvTemperature;
    TextView tvType;
    TextView tvWindPower;
    TextView tvWindDt;
    TextView tvCity;
    RecyclerView recyclerView;
    LinearLayout background;

    public static List<CityWeather> weatherList = new ArrayList<>();
    //    public static String[] citys = {"温州"};
    public static HashMap<String, String> citys = new HashMap<String, String>();
    public static String defaultCity = "杭州";
    public static String kcity;
    public static int flag = 1 ;
    static CityWeatherAdapter adapter ;

    public p1() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_p1, container, false);
        ButterKnife.bind(this, view);
        tvCity = view.findViewById(R.id.city);
        recyclerView = view.findViewById(R.id.today);
        Button button = view.findViewById(R.id.lb);
        init();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getTodayWeather(defaultCity);
        initWeatherList();
    }

    /**
     * 初始化设置
     */
    public void init() {
        // 默认城市初始化
        SharedPreferences sp = getActivity().getSharedPreferences("com.example.fin_preferences", Context.MODE_PRIVATE);
        defaultCity = sp.getString("city", "杭州");
        kcity = sp.getString("in", "厦门");
        Log.i("TAG", "init: "+kcity);
//        for(String x: citys.values()){
//            p1.getCityWeather(x);
//        }

        if (!citys.containsKey(kcity)) {
            citys.put(kcity, kcity);
            p1.getCityWeather(kcity);
            Log.i("TAG",citys.values().toString());
        }
        // recyclerView初始化
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

    }

    /**
     * 天气列表初始化
     */
    public void initWeatherList() {
        if(flag == 1) {
            adapter = new CityWeatherAdapter(weatherList);
            recyclerView.setAdapter(adapter);
        }
    }


    /**
     * 获取默认城市当天天气
     *
     * @param city
     */
    public void getTodayWeather(String city) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                GetRequestServiceImpl getRequestService = new GetRequestServiceImpl();
                retrofit2.Call<ResponseBody> call = getRequestService.getWeather(city);
                try {
                    retrofit2.Response<ResponseBody> response = call.execute();
                    String jsonStr = response.body().string();
                    // 将json传到handle中进行操作
                    Message msg = Message.obtain();
                    Bundle b = new Bundle();
                    b.putString("jsonStr", jsonStr);
                    msg.setData(b);
                    handlerWeather.sendMessage(msg);
                } catch (IOException e) {
                    Looper.prepare();
                    ToastUtil.missToast(getContext());
                    ToastUtil.showToast(getContext(), "网络异常，请检查网络", Toast.LENGTH_SHORT);
                    Looper.loop();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 获取默认城市当天天气handle
     */
    @SuppressLint("HandlerLeak")
    private Handler handlerWeather = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle dataMsg = msg.getData();
            String jsonStr = dataMsg.getString("jsonStr");
            // Json处理
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONObject data = jsonObject.getJSONObject("data");

                String city = data.getString("city");
                JSONArray forecastArray = data.getJSONArray("forecast");
                // 只获取当日天气信息
                JSONObject weatherObject = forecastArray.getJSONObject(0);
                Weather weather = new Gson().fromJson(weatherObject.toString(), Weather.class);
//                Log.i("WEATHER", weather.toString());
                String temperature = weather.getLow().substring(3) + " ~ " + weather.getHigh().substring(3);
                String windPower = "风力：" + weather.getFengli().charAt(9) + "级";
                String windDt = "风向：" + weather.getFengxiang();
                // UI处理
                tvCity.setText(city + "\n" + temperature + "\n" + windPower + "\n" + windDt);

            } catch (JSONException e) {
                ToastUtil.missToast(getContext());
                ToastUtil.showToast(getContext(), "系统异常，请稍后再试", Toast.LENGTH_SHORT);
                e.printStackTrace();
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 获取某个城市天气信息
     *
     * @param city
     */
    public static void getCityWeather(String city) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                GetRequestServiceImpl getRequestService = new GetRequestServiceImpl();
                Call<ResponseBody> call = getRequestService.getWeather(city);
                try {
                    Response<ResponseBody> response = call.execute();
                    String jsonStr = response.body().string();

                    // json处理
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONObject data = jsonObject.getJSONObject("data");
                    // 只获取当日天气信息
                    JSONArray forecastArray = data.getJSONArray("forecast");
                    JSONObject weatherObject = forecastArray.getJSONObject(0);
                    Weather weather = new Gson().fromJson(weatherObject.toString(), Weather.class);
                    String temperature = weather.getLow().substring(3) + " ~ " + weather.getHigh().substring(3);
                    CityWeather cityWeather = new CityWeather(city, weather.getType(), temperature);
                    // debug
                    if( citys.containsValue(city)) {
                        flag = 1 ;
                        Log.i("TAG",citys.values().toString()+city);
                        Log.i("WEATHER", cityWeather.toString() + "=============");
                        weatherList.add(cityWeather);
                    }
                    else {
                        flag = 0 ;
                        Log.i("flag", String.valueOf(flag));
                        weatherList.clear();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}