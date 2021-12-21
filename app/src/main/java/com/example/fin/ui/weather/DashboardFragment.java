package com.example.fin.ui.weather;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.fin.HttpUtil;
import com.example.fin.R;

import com.example.fin.databinding.FragmentDashboardBinding;
import com.example.fin.ui.map.HomeViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    private HomeViewModel univViewModel;
    private String city = null ;
    private Fragment f;
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("1", "onCreate: ");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Log.i("1", "onCreate: kk");
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar mToolBar = (Toolbar) activity.findViewById(R.id.tb);
        mToolBar.setTitle("天气");
        mToolBar.setNavigationIcon(null);
        TabLayout t1 =binding.tb2;
        ViewPager vp = binding.vp;
        t1.setTabMode(TabLayout.MODE_FIXED);
        t1.addTab(t1.newTab().setText("今日"));
        t1.addTab(t1.newTab().setText("推荐"));
        vp.setAdapter(new FragmentPagersAdapter(getChildFragmentManager()));
        t1.setupWithViewPager(vp);
        return root;
    }
    public class FragmentPagersAdapter extends FragmentPagerAdapter{
        private String [] listTitle = {"今日","推荐"};
        public FragmentPagersAdapter(FragmentManager fm){
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 :
                    f = new p1();
                    return f;
                case 1 :
                    f = new p2();
                    return f;
            }
            return null;
        }

        @Override
        public int getCount() {
            return listTitle.length;
        }

        public CharSequence getPageTitle(int postion){
            return listTitle[postion];
        }
    }


}