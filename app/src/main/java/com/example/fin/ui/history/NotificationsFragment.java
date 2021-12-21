package com.example.fin.ui.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.fin.R;
import com.example.fin.ToastUtil;
import com.example.fin.dao.WeatherDataDao;
import com.example.fin.databinding.FragmentNotificationsBinding;
import com.example.fin.db.WeatherData;
import com.example.fin.db.WeatherDataBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    @BindView(R.id.line_chart_past)
    LineChartView lineChartPast;
    @BindView(R.id.backgroundPast)
    LinearLayout backgroundPast;
//    @BindView(R.id.line_chart_future)
//    LineChartView lineChartFuture;
//    @BindView(R.id.backgroundFuture)
//    LinearLayout backgroundFuture;

    private SharedPreferences sp;

    private Integer dayPast;
    private Integer dayFuture;

    public static String[] datePast = new String[7];//X轴的标注
    public static String[] dateFuture = new String[7];
    public static int[] tempLowPast = new int[7];//图表的数据点
    public static int[] tempHighPast = new int[7];//图表的数据点
    public static int[] tempLowFuture = new int[7];//图表的数据点
    public static int[] tempHighFuture = new int[7];//图表的数据点
    private List<PointValue> pointLowPast = new ArrayList<>();
    private List<PointValue> pointHighPast = new ArrayList<>();
    private List<PointValue> pointLowFuture = new ArrayList<>();
    private List<PointValue> pointHighFuture = new ArrayList<>();
    private List<AxisValue> axisXPast = new ArrayList<>();
    private List<AxisValue> axisXFuture = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ButterKnife.bind(this, root);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar mToolBar = (Toolbar) activity.findViewById(R.id.tb);
        mToolBar.setTitle("历史");
        mToolBar.setNavigationIcon(null);

        pastDateInit();
        init();
        getAxisXLables();//获取x轴的标注
        getAxisPoints();//获取坐标点
        HelloChartInit();
        return root;
    }
    /**
     * 初始化
     */
    private void init() {
        // 设置背景透明
        backgroundPast.getBackground().setAlpha(50);
//        backgroundFuture.getBackground().setAlpha(50);
        // sp初始化
        sp = getActivity().getSharedPreferences("com.example.fin_preferences", Context.MODE_PRIVATE);
        dayPast = Integer.valueOf(sp.getString("day_past", "11"));
        dayFuture = Integer.valueOf(sp.getString("day_future", "11"));
    }

    /**
     * HelloChart初始化
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void HelloChartInit() {
        pastInit();
    }

    /**
     * 设置X轴的显示
     */
    private void getAxisXLables() {
        try {
            // Past
            for (int i = 0; i < dayPast; i++) {
                axisXPast.add(new AxisValue(i).setLabel(datePast[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.missToast(getContext());
            ToastUtil.showToast(getContext(), "历史天数不足，无法显示历史天气", Toast.LENGTH_SHORT);
        }


    }

    /**
     * 图表的每个点的显示
     */
    private void getAxisPoints() {
        try {
            // Past
            for (int i = 0; i < dayPast; i++) {
                pointLowPast.add(new PointValue(i, tempLowPast[i]));
            }

            for (int i = 0; i < dayPast; i++) {
                pointHighPast.add(new PointValue(i, tempHighPast[i]));
            }

        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.missToast(getContext());
            ToastUtil.showToast(getContext(), "历史天数不足，无法显示历史天气", Toast.LENGTH_SHORT);
        }

    }

    /**
     * 历史图表初始化
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void pastInit() {
        List<Line> lines = new ArrayList<>();
        // low
        Line lineLow = new Line(pointLowPast).setColor(Color.parseColor("#0099ff"));
        lineLow.setShape(ValueShape.CIRCLE);
        lineLow.setCubic(true);//曲线是否平滑，即是曲线还是折线
        lineLow.setFilled(false);//是否填充曲线的面积
        lineLow.setHasLabels(true);//曲线的数据坐标是否加上备注
        lineLow.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        lineLow.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(lineLow);

        // high
        Line lineHigh = new Line(pointHighPast).setColor(Color.parseColor("#0099ff"));
        lineHigh.setShape(ValueShape.CIRCLE);
        lineHigh.setCubic(true);//曲线是否平滑，即是曲线还是折线
        lineHigh.setFilled(false);//是否填充曲线的面积
        lineHigh.setHasLabels(true);//曲线的数据坐标是否加上备注
        lineHigh.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        lineHigh.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(lineHigh);

        LineChartData data = new LineChartData();
        data.setLines(lines);
        data.setValueLabelBackgroundEnabled(false);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setName("历史" + dayPast + "条天气信息");  //表格名称
        axisX.setTextSize(6);//设置字体大小
        axisX.setTextColor(Color.WHITE);
        axisX.setValues(axisXPast);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        axisX.setHasLines(true); //x 轴分割线

        //设置行为属性，支持缩放、滑动以及平移
        lineChartPast.setInteractive(true);
        lineChartPast.setZoomType(ZoomType.HORIZONTAL);
        lineChartPast.setMaxZoom((float) 2);//最大方法比例
        lineChartPast.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartPast.setLineChartData(data);
        lineChartPast.setVisibility(View.VISIBLE);

        // 视角设置
        Viewport v = new Viewport(lineChartPast.getMaximumViewport());
        v.top = Arrays.stream(tempHighPast).max().getAsInt() + 1; //最高点为最大值+1
        v.bottom = Arrays.stream(tempLowPast).min().getAsInt() - 1; //最低点为最小值-1
        lineChartPast.setMaximumViewport(v);   //给最大的视图设置 相当于原图
        lineChartPast.setCurrentViewport(v);   //给当前的视图设置 相当于当前展示的图
    }

    /**
     * 历史图标初始化
     */
    private void pastDateInit() {
        WeatherDataBase weatherDataBase = Room.databaseBuilder(getContext(), WeatherDataBase.class, "WeatherDataBase.db").allowMainThreadQueries().build();
        WeatherDataDao weatherDataDao = weatherDataBase.weatherDataDao();
        // 获取最近数据
        List<WeatherData> all = weatherDataDao.getRecently();
        int count = 0;
        for (WeatherData data : all) {
            Log.i("WEATHER", data.toString());
            datePast[count] = data.getDate();
            tempLowPast[count] = Integer.parseInt(data.getLow());
            tempHighPast[count] = Integer.parseInt(data.getHigh());
            count++;
        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}