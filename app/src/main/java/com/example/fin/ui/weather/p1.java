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
    //    public static String[] citys = {"??????"};
    public static HashMap<String, String> citys = new HashMap<String, String>();
    public static String defaultCity = "??????";
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
     * ???????????????
     */
    public void init() {
        // ?????????????????????
        SharedPreferences sp = getActivity().getSharedPreferences("com.example.fin_preferences", Context.MODE_PRIVATE);
        defaultCity = sp.getString("city", "??????");
        kcity = sp.getString("in", "??????");
        Log.i("TAG", "init: "+kcity);
        if (!citys.containsKey(kcity)) {
            citys.put(kcity, kcity);
            p1.getCityWeather(kcity);
            Log.i("TAG",citys.values().toString());
        }
        // recyclerView?????????
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * ?????????????????????
     */
    public void initWeatherList() {
        if(flag == 1) {
            adapter = new CityWeatherAdapter(weatherList);
            recyclerView.setAdapter(adapter);
        }
    }


    /**
     * ??????????????????????????????
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
                    // ???json??????handle???????????????
                    Message msg = Message.obtain();
                    Bundle b = new Bundle();
                    b.putString("jsonStr", jsonStr);
                    msg.setData(b);
                    handlerWeather.sendMessage(msg);
                } catch (IOException e) {
                    Looper.prepare();
                    ToastUtil.missToast(getContext());
                    ToastUtil.showToast(getContext(), "??????????????????????????????", Toast.LENGTH_SHORT);
                    Looper.loop();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * ??????????????????????????????handle
     */
    @SuppressLint("HandlerLeak")
    private Handler handlerWeather = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle dataMsg = msg.getData();
            String jsonStr = dataMsg.getString("jsonStr");
            // Json??????
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONObject data = jsonObject.getJSONObject("data");

                String city = data.getString("city");
                JSONArray forecastArray = data.getJSONArray("forecast");
                // ???????????????????????????
                JSONObject weatherObject = forecastArray.getJSONObject(0);
                Weather weather = new Gson().fromJson(weatherObject.toString(), Weather.class);
                String temperature = weather.getLow().substring(3) + " ~ " + weather.getHigh().substring(3);
                String windPower = "?????????" + weather.getFengli().charAt(9) + "???";
                String windDt = "?????????" + weather.getFengxiang();
                // UI??????
                tvCity.setText(city + "\n" + temperature + "\n" + windPower + "\n" + windDt);
            } catch (JSONException e) {
                ToastUtil.missToast(getContext());
                ToastUtil.showToast(getContext(), "??????????????????????????????", Toast.LENGTH_SHORT);
                e.printStackTrace();
            }
            super.handleMessage(msg);
        }
    };

    /**
     * ??????????????????????????????
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

                    // json??????
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONObject data = jsonObject.getJSONObject("data");
                    // ???????????????????????????
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