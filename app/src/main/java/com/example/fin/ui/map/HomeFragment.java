package com.example.fin.ui.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.fin.R;
import com.example.fin.databinding.FragmentHomeBinding;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;

public class HomeFragment extends Fragment implements LocationSource, CompoundButton.OnCheckedChangeListener, AMapLocationListener {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private MapView mapView;
    private AMap aMap;
    private ToggleButton btn_mapchange;
    private MyLocationStyle myLocationStyle;
    private OnLocationChangedListener mListener;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption clientOption;
    private HomeViewModel univViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar mToolBar = activity.findViewById(R.id.tb);
        mToolBar.setTitle("地图");
        mToolBar.setNavigationIcon(null);
        AMapLocationClient.updatePrivacyAgree(getActivity().getApplicationContext(), true);
        AMapLocationClient.updatePrivacyShow(getActivity().getApplicationContext(), true, true);
        initview(savedInstanceState,root);
        initlistener();
        return root;
    }

    private void initview( Bundle savedInstanceState,View view){
        mapView= view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if (aMap==null)
        {
            aMap=mapView.getMap();
        }
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        aMap.setMyLocationStyle(myLocationStyle);

        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
        btn_mapchange= view.findViewById(R.id.btn_mapchange);
    }

    private void initlistener(){

        btn_mapchange.setOnCheckedChangeListener(this);
    }

    /**
     * 激活定位
     */
    public void activate(OnLocationChangedListener listener) {
        mListener=listener;
        Log.i("TAG", "activate: ");
        if(locationClient==null){
            try {
                locationClient=new AMapLocationClient(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }
            clientOption=new AMapLocationClientOption();
            clientOption.setInterval(15000);//设置定位间隔,单位毫秒,默认为2000ms
            locationClient.setLocationListener(this);
            clientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//高精度定位
            clientOption.setOnceLocationLatest(true);//设置单次精确定位
            locationClient.setLocationOption(clientOption);
            locationClient.startLocation();

        }

    }
    /**
     * 停止定位
     */
    public void deactivate() {
        mListener=null;
        if(locationClient!=null){
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
        locationClient=null;
    }

    public void onLocationChanged(AMapLocation aMapLocation) {
        univViewModel = new ViewModelProvider(getActivity(),new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);
//        System.out.println("所在城市："+aMapLocation.getCity());
        univViewModel.gem().setValue(aMapLocation.getCity());
        if (mListener != null&&aMapLocation != null) {
            if (aMapLocation != null
                    &&aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
        }
        else {
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        }
    }

    /**
     * 必须重写以下方法
     */
    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("TAG", "onPause: ");
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(locationClient!=null){
            locationClient.onDestroy();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}