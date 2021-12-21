package com.example.fin.ui.setting;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.Preference;
import androidx.room.Room;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fin.R;
import com.example.fin.ToastUtil;
import com.example.fin.dao.WeatherDataDao;
import com.example.fin.databinding.FragmentDashboardBinding;
import com.example.fin.databinding.FragmentHomeBinding;
import com.example.fin.databinding.SettingFragmentBinding;
import com.example.fin.db.WeatherData;
import com.example.fin.db.WeatherDataBase;
import com.example.fin.service.impl.GetRequestServiceImpl;
import com.example.fin.ui.map.HomeViewModel;
import com.example.fin.ui.weather.DashboardViewModel;
import com.example.fin.ui.weather.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class setting extends PreferenceFragmentCompat   implements Preference.OnPreferenceChangeListener  {
    private String passwordStr = "123";
    private String oldCity = "杭州";
    private androidx.preference.Preference login;
    private androidx.preference.Preference username;
    private androidx.preference.Preference password;
    private androidx.preference.Preference city;
    private androidx.preference.Preference confirm;
    private androidx.preference.Preference datPast;

    private androidx.preference.Preference incity;

    private SharedPreferences.Editor editor;
    private SharedPreferences sp;

    private SettingViewModel settingViewModel;
    private SettingFragmentBinding binding;
    private SettingViewModel mViewModel;

    /**
     * Preference初始化
     */
    public void init() {
        login = findPreference("login");
        username = findPreference("username");
        password = findPreference("password");
        city = findPreference("city");
        incity=findPreference("in");
        confirm = findPreference("confirm");
        datPast = findPreference("day_past");
        incity.setOnPreferenceChangeListener(this);
        login.setOnPreferenceChangeListener(this);
        username.setOnPreferenceChangeListener(this);
        password.setOnPreferenceChangeListener(this);
        city.setOnPreferenceChangeListener(this);
        confirm.setOnPreferenceChangeListener(this);
        datPast.setOnPreferenceChangeListener(this);
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_preference, rootKey);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar mToolBar = (Toolbar) activity.findViewById(R.id.tb);
        mToolBar.setTitle("设置");
        mToolBar.setNavigationIcon(null);
        spInit();
        init();
    }
    /**
     * sp初始化
     */
    private void spInit() {
        sp = getActivity().getSharedPreferences("com.example.fin_preferences", Context.MODE_PRIVATE);
        passwordStr = sp.getString("password", passwordStr);
        oldCity = sp.getString("city", oldCity);
        editor = sp.edit();
        editor.putString("confirm", "");
        editor.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case "login":
                ToastUtil.missToast(getContext());
                ToastUtil.showToast(getContext(), "自动登录设置修改成功", Toast.LENGTH_SHORT);
                break;
            case "username":
                ToastUtil.missToast(getContext());
                ToastUtil.showToast(getContext(), "用户名修改成功", Toast.LENGTH_SHORT);
                break;
            case "password":
                ToastUtil.missToast(getContext());
                ToastUtil.showToast(getContext(), "密码修改成功", Toast.LENGTH_SHORT);
                break;
            case "city":
                isValidCity(newValue.toString());
                break;
            case "confirm":
                String confirmStr = newValue.toString();
                if (confirmStr.equals(passwordStr)) {
                    username.setEnabled(true);
                    password.setEnabled(true);
                    confirm.setEnabled(false);
                    ToastUtil.missToast(getContext());
                    ToastUtil.showToast(getContext(), "密码验证成功", Toast.LENGTH_SHORT);
                } else {
                    ToastUtil.missToast(getContext());
                    ToastUtil.showToast(getContext(), "密码验证失败", Toast.LENGTH_SHORT);
                }
                break;
            case "day_past":
                ToastUtil.missToast(getContext());
                ToastUtil.showToast(getContext(), "历史天数修改为" + newValue + "天", Toast.LENGTH_SHORT);
                break;
            case "in":
                isValidCity_1(newValue.toString());
                break;
            default:
                break;
        }
        return true;
    }


    /**
     * 判断城市名是否合法
     * @param city
     */
    public void isValidCity(String city) {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                GetRequestServiceImpl getRequestService = new GetRequestServiceImpl();
                Call<ResponseBody> call = getRequestService.getWeather(city);
                try {
                    Response<ResponseBody> response = call.execute();
                    String jsonStr = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    jsonObject.getJSONObject("data");
                    Looper.prepare();
                    ToastUtil.missToast(getActivity());
                    ToastUtil.showToast(getActivity(), "默认城市已修改为" + city, Toast.LENGTH_LONG);
                    Looper.loop();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    Looper.prepare();
                    ToastUtil.missToast(getActivity());
                    ToastUtil.showToast(getActivity(), "城市名无效，请输入正确的城市名！", Toast.LENGTH_SHORT);
                    editor.putString("city", oldCity);
                    editor.commit();
                    Looper.loop();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 判断城市名是否合法
     * @param city
     */
    public void isValidCity_1(String city) {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                GetRequestServiceImpl getRequestService = new GetRequestServiceImpl();
                Call<ResponseBody> call = getRequestService.getWeather(city);
                try {
                    Response<ResponseBody> response = call.execute();
                    String jsonStr = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    jsonObject.getJSONObject("data");
                    Looper.prepare();
                    ToastUtil.missToast(getActivity());
                    WeatherPastInit(city);
                    ToastUtil.showToast(getActivity(), "兴趣城市已添加" + city, Toast.LENGTH_LONG);
                    Looper.loop();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    Looper.prepare();
                    ToastUtil.missToast(getActivity());
                    ToastUtil.showToast(getActivity(), "城市名无效，请输入正确的城市名！", Toast.LENGTH_SHORT);
                    editor.putString("city", oldCity);
                    editor.commit();
                    Looper.loop();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 历史天气数据初始化
     */
    private void WeatherPastInit(String city) {
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
                    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
                    Date date = new Date(System.currentTimeMillis());
                    String today = formatter.format(date);
                    String low = weather.getLow();
                    String high = weather.getHigh();
                    // 截取字符串
                    low = low.substring(3, low.length() - 1);
                    high = high.substring(3, high.length() - 1);
                    WeatherData weatherData = new WeatherData();
                    weatherData.setDate(city+today);
                    weatherData.setLow(low);
                    weatherData.setHigh(high);
                    // sqlite
                    WeatherDataBase weatherDataBase = Room.databaseBuilder(getContext(), WeatherDataBase.class, "WeatherDataBase.db").allowMainThreadQueries().build();
                    WeatherDataDao weatherDataDao = weatherDataBase.weatherDataDao();
                    WeatherData sqlData = weatherDataDao.findByDate(city+today);
                    // 如果今天数据还没记录，则写入数据库
                    if (sqlData == null) {
                        weatherDataDao.insertAll(weatherData);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}