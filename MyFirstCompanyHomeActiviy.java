package cn.dongha.ido.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.accloud.service.ACMsg;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
//import com.facebook.AccessToken;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.squareup.okhttp.Response;
import com.umeng.analytics.MobclickAgent;
import com.veryfit.multi.ble.BleManager;
import com.veryfit.multi.ble.BleStatus;
import com.veryfit.multi.config.Constants;
import com.veryfit.multi.entity.SportData;
import com.veryfit.multi.entity.SwitchDataBleEnd;
import com.veryfit.multi.entity.SwitchDataBleEndReply;
import com.veryfit.multi.entity.SwitchDataBleIng;
import com.veryfit.multi.entity.SwitchDataBleIngReply;
import com.veryfit.multi.entity.SwitchDataBleStart;
import com.veryfit.multi.entity.SwitchDataBleStartReply;
import com.veryfit.multi.nativedatabase.BasicInfos;
import com.veryfit.multi.nativedatabase.FindPhoneOnOff;
import com.veryfit.multi.nativedatabase.FunctionInfos;
import com.veryfit.multi.nativedatabase.HealthHeartRate;
import com.veryfit.multi.nativedatabase.HealthHeartRateAndItems;
import com.veryfit.multi.nativedatabase.HealthHeartRateItem;
import com.veryfit.multi.nativedatabase.HealthSport;
import com.veryfit.multi.nativedatabase.HealthSportAndItems;
import com.veryfit.multi.nativedatabase.NoticeOnOff;
import com.veryfit.multi.nativedatabase.RealTimeHealthData;
import com.veryfit.multi.nativedatabase.Units;
import com.veryfit.multi.nativedatabase.UserDeivce;
import com.veryfit.multi.nativedatabase.healthSleep;
import com.veryfit.multi.nativedatabase.healthSleepAndItems;
import com.veryfit.multi.nativeprotocol.ISynConfigCallBack;
import com.veryfit.multi.nativeprotocol.ProtocolEvt;
import com.veryfit.multi.nativeprotocol.ProtocolUtils;
import com.veryfit.multi.share.BleSharedPreferences;
import com.veryfit.multi.util.DebugLog;
import com.veryfit.multi.util.TimeUtils;
import cn.dongha.ido.MyApplication;
import cn.dongha.ido.R;
import cn.dongha.ido.common.base.BaseAppBleListener;
import cn.dongha.ido.common.base.BaseCallBack;
import cn.dongha.ido.common.base.BaseFragment;
import cn.dongha.ido.common.base.BaseShareSelectListener;
import cn.dongha.ido.common.config.BleConfig;
import cn.dongha.ido.common.model.ChangeTimeZoneModel;
import cn.dongha.ido.common.model.GaoDeMapModel;
import cn.dongha.ido.common.model.LocationModel;
import cn.dongha.ido.common.model.LogModel;
import cn.dongha.ido.common.model.PariModel;
import cn.dongha.ido.common.model.UpdateModel;
import cn.dongha.ido.common.model.db.DBModel;
import cn.dongha.ido.common.model.db.LatLngDb;
import cn.dongha.ido.common.model.googlefit.UploadDataGoogleFitModle;
import cn.dongha.ido.common.model.web.LogInfoModel;
import cn.dongha.ido.common.model.web.LoginModel;
import cn.dongha.ido.common.model.web.OtaModel;
import cn.dongha.ido.common.model.web.RankModel;
import cn.dongha.ido.common.model.web.WeatherModel;
import cn.dongha.ido.common.utils.AppSharedPreferencesUtils;
import cn.dongha.ido.common.utils.AppUtil;
import cn.dongha.ido.common.utils.Constant;
import cn.dongha.ido.common.utils.DialogUtil;
import cn.dongha.ido.common.utils.FileUtil;
import cn.dongha.ido.common.utils.FunctionsUnit;
import cn.dongha.ido.common.utils.GpsUtil;
import cn.dongha.ido.common.utils.HttpUtil;
import cn.dongha.ido.common.utils.LogUtil;
import cn.dongha.ido.common.utils.MusicUtil;
import cn.dongha.ido.common.utils.MyConstant;
import cn.dongha.ido.common.utils.MySectorMenu;
import cn.dongha.ido.common.utils.NetUtils;
import cn.dongha.ido.common.utils.NetWorkUtil;
import cn.dongha.ido.common.utils.NormalToast;
import cn.dongha.ido.common.utils.SPUtils;
import cn.dongha.ido.common.utils.ScreenUtil;
import cn.dongha.ido.common.utils.ScreenUtils;
import cn.dongha.ido.common.utils.TimeUtil;
import cn.dongha.ido.common.utils.UnitUtil;
import cn.dongha.ido.common.view.DrawCircleView;
import cn.dongha.ido.ui.bloodpressure.BloodpressureTestReadyActivity;
import cn.dongha.ido.ui.detail.DetailFragment;
import cn.dongha.ido.ui.device.CameraActivity;
import cn.dongha.ido.ui.device.DeviceFragment;
import cn.dongha.ido.ui.device.DeviceUpateActivity;
import cn.dongha.ido.ui.device.MoreActivity;
import cn.dongha.ido.ui.device.camera.PhotoFragment;
import cn.dongha.ido.ui.main.MainFragment;
import cn.dongha.ido.ui.main.timeaxis.model.SaveSwitchDataBleStart;
import cn.dongha.ido.ui.myself.DataSourcesSettingActivity;
import cn.dongha.ido.ui.myself.MyselfFragment;
import cn.dongha.ido.ui.others.DeviceUpdateInfo;
import cn.dongha.ido.ui.others.ScanDeviceActivity;
import cn.dongha.ido.ui.sport.MyLatLng;
import cn.dongha.ido.ui.sport.PathActivity;
import cn.dongha.ido.ui.sport.SportTypeActivity;
import cn.dongha.ido.ui.sport.WeightActivity;
import cn.dongha.ido.ui.sport.weight.BalanceData2Activity;
import cn.dongha.ido.ui.sport.weight.BalanceDataActivity;
import cn.dongha.ido.ui.sport.weight.BalanceSearchActivity;
import cn.dongha.ido.ui.sport.weight.HandAddActivity;
import cn.dongha.ido.ui.sport.weight.WeightControlActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, PhotoFragment.CancelOnClickListener {

    public static HomePageType homePageType = HomePageType.MAIN;
    public static final String DEVICE_NAME = "DEVICE_NAME";
    private MusicUtil musicUtil;
    private RadioGroup mTabThemeRg;
    private boolean authInProgress = false;
    private static final String AUTH_PENDING = "auth_state_pending";
    public BaseFragment mLastFragment;
    public MainFragment mainFragment;
    private DetailFragment detailFragment;
    private DeviceFragment deviceFragment;
    private MyselfFragment myselfFragment;
    private ProtocolUtils protocolUtils = ProtocolUtils.getInstance();
    private AppSharedPreferencesUtils share = AppSharedPreferencesUtils.getInstance();
    private Gson gson = new Gson();
    private Activity activity;
    private long lastOnclickTime = 0L;
    private RadioButton tab_run_rb;
    private RelativeLayout dialog_layout;
    private MySectorMenu mySectorMenu;
    private int fromDegrees = 0;
    private float toDegrees = -45;
    private View morerun, path, weight;
    //    private ImageView booldpressure;
    private ImageView bottomBtn;
    private RelativeLayout add_layout;
    private View content_root;
    private RelativeLayout bottom_layout;
    private LocationManager mLocationManager;
    private ArrayList<SaveSwitchDataBleStart> dataBleStarts;

    public static String ISSYN_DATA = "ISSYN_DATA";
    public static String IS_RESOVLER_CONFIG_KEY = "IS_RESOVLER_CONFIG_KEY";
    //    private PhotoFragment fragment;
    private static RadioButton homeRb, detailRb, deviceRb, userRb;
    public static final String WEATHER_ACTION = "com.action.weather";
    public static final String WEARTHER_STATE = "weartherState";
    public static ExecutorService executorService = Executors.newCachedThreadPool();
    /**
     * 是否是第一次绑定
     */
    public static final String FISRT_BIND = "FISRT_BIND";
    // 定时器
    private static Timer timer;//定时器

    /**
     * 是否发送实时命令
     */
    private boolean isStartTimer = true;

    // 定位
    private GaoDeMapModel gaoDeMapUtil;
    // 手环发起时间戳
    private long time;
    // 时间
    private Time t;
    // 数据库
    private LatLngDb latLngDb;
    public static int currentIndex = 0;
    // 后台定位
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private ArrayList<MyLatLng> myLatLngs = new ArrayList<>();
    private View rlSyncho;
    private TextView tvSynchroProgress;
    private WeatherModel weatherModel = new WeatherModel();
    private ChangeTimeZoneModel changeTimeZoneModel;
    private int setWeatherFaildCount = 0;
    private int activityValue = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://获取天气
                    // 开关打开并且在联网的情况下
                    if (protocolUtils.getWeatherOnOff() && NetUtils.isConnected(activity)) {
                        getWeatherFromService();
                    }
//                getWeatherFromService();//暂时默认开启状态
                    break;
            }
        }
    };
    private Handler handler24 = new Handler();
    public static boolean isSynchoData = true;
    private boolean isSynchOutTime;
    private GoogleFitDataModel googleFitDataModel;
    private UploadDataGoogleFitModle googleFitModle;
    private int googleFit;
    private RelativeLayout main_title_rl;
    private FragmentMessageReceiver fragmentMessageReceiver;
    private LinearLayout mroot,ll_title;
    private ImageButton ib_link_state;
    public CheckBox cb_date;
    private TextView tv_title;
    private ProgressBar freshProgressPb;
    private ImageButton share_btn;
    private Dialog dialog;
    private ImageView animationImageView;//跑步动画
    private DrawCircleView drawCircleView;

    private BroadcastReceiver updateSuccessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            DebugLog.d("action=" + action);
            if (action.equals(UpdateModel.ACTION_UPDATE_SUCEESS)) {//升级成功
                share.setFirstSync(true);
                protocolUtils.reConnect();
                homePageType = HomePageType.MAIN;
                updatePage();
                mTabThemeRg.check(R.id.tab_mainpage_rb);
                FunctionInfos functionInfos = protocolUtils.getFunctionInfosByDb();
                DebugLog.d("functionInfos=" + functionInfos.toString());
                BasicInfos basicInfos = protocolUtils.getDeviceByDb();
                DebugLog.d("basicInfos=" + basicInfos.toString());
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                }
                mainFragment.updateSyncData();
//                mainFragment.mRootView.onStartUpdate2();
            } else if (action.equals(MoreActivity.ACTION_RESTART_SUCEESS)) {//重启成功
//                share.setFirstSync(true);
                homePageType = HomePageType.MAIN;
                updatePage();
                mTabThemeRg.check(R.id.tab_mainpage_rb);
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                }
                mainFragment.rebootSyncData();
            } else if (action.equals(Intent.ACTION_TIME_CHANGED)) {//时间改变的广播
                DebugLog.d("时间改变的广播");

                if (BleManager.getInstance().isDeviceConnected()) {
                    protocolUtils.setClock();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SPUtils.put(ISSYN_DATA, false);
                            setUnits();
                        }
                    }, 100);
                }
                if (MyApplication.dateOffset == 0 && mainFragment != null) {
                    mainFragment.lazyLoad();
                }

            } else if (action.equals(Constant.END_SPORT)) {//运动结束，跳转到主页
                homePageType = HomePageType.MAIN;
                updatePage();
            } else if (action.equals(Intent.ACTION_CONFIGURATION_CHANGED)) {//设备当前设置被改变时发出的广播(包括的改变:界面语言，设备方向等)
               if (!(boolean)SPUtils.get(HomeActivity.IS_RESOVLER_CONFIG_KEY,true)){
                   return;
               }
                DebugLog.d("设备当前设置被改变时发出的广播(包括的改变:界面语言，设备方向等),当前系统语言=" + MyApplication.getInstance().isZh());
                SPUtils.put(ISSYN_DATA, false);
                setUnits();
                setWeather24();
            } else if (action.equals(Constant.STOP_START_TIMER)) {// 暂停实时数据5s钟
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        share.setStartTimer(true);
                    }
                }, 5000);
            } else if (action.equals(Constant.SYNC_FINISH_ACTION)) {//同步完成
                DebugLog.d("同步完成");
                if (mainFragment != null && share.getFirstFinish()) {
                    if (MyApplication.dateOffset == 0) {
                        mainFragment.updateAllData();
                        if (mainFragment.currentFragment == mainFragment.sportFragment) {
                            share.setAnimationSwith(true);
                        }
                    }
                }
//                googleFitDataModel.saveData();
                if (googleFit == 1) {
                    googleFitModle.connect();
                    googleFitModle.saveData();
                }
//                if ((FunctionsUnit.isSupportTimeLine())){
//                    // 开始同步活动数据
////                    protocolUtils.startSyncActivityData();
//                }

            } else if (WEATHER_ACTION.equals(action)) {

                boolean state = intent.getBooleanExtra(WEARTHER_STATE, false);
                DebugLog.d("收到天气状态开关.....state:" + state);
                if (state) {
                    startWeatherTimer();
                } else {
                    closeWeatherTimer();
                }


            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置窗体无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        ScreenUtils.initScreen(this);
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
            mLastFragment = (BaseFragment) getSupportFragmentManager().getFragment(savedInstanceState, "mLastFragment");
        }
        share.setSyncData(false);
        initViews();
        initData();
        initEvent();
        new LocationModel().setSaveCity(true).startLocation(this);

        boolean isDfu = (boolean) SPUtils.get(UpdateModel.DFU_MODEL, false);
        DebugLog.d("isDfu:" + isDfu);
        if (isDfu && ProtocolUtils.getInstance().getDeviceByDb() != null) {

            Intent intent = new Intent(this, DeviceUpateActivity.class);
            intent.putExtra(DeviceUpateActivity.DFU_MODE_KEY, true);
            intent.putExtra(DeviceUpateActivity.DEVICEID_KEY, ProtocolUtils.getInstance().getDeviceByDb().getDeivceId());
            intent.putExtra(DeviceUpateActivity.DEVICEADDR_KEY, ProtocolUtils.getInstance().getDeviceByDb().getMacAddress());
            intent.putExtra(DeviceUpateActivity.DEVICENAME_KEY, "");
            startActivity(intent);
        }

        changeTimeZoneModel = new ChangeTimeZoneModel(this);

        protocolUtils.setISynConfigCallBack(iSynConfigCallBack);
        Intent intent = new Intent();
        intent.setAction(Constant.BACK_TO_FRONT_AUTO_SYN);
        activity.sendBroadcast(intent);
    }

    public void initViews() {
        drawCircleView = (DrawCircleView) findViewById(R.id.drawCircleView);
        animationImageView = (ImageView) findViewById(R.id.run_animation);
        ib_link_state = (ImageButton) this.findViewById(R.id.link_state);
        cb_date = (CheckBox) this.findViewById(R.id.date);
        tv_title = (TextView) this.findViewById(R.id.tv_title);
        freshProgressPb = (ProgressBar) this.findViewById(R.id.fresh_progress_pb);
        share_btn = (ImageButton) this.findViewById(R.id.share_btn);

        mTabThemeRg = (RadioGroup) findViewById(R.id.tab_theme_rg);
        homeRb = (RadioButton) findViewById(R.id.tab_mainpage_rb);
        DebugLog.d("initViews........" + getString(R.string.tab_mainpage));


        detailRb = (RadioButton) findViewById(R.id.tab_details_rb);


        deviceRb = (RadioButton) findViewById(R.id.tab_device_rb);


        userRb = (RadioButton) findViewById(R.id.tab_user_rb);

        detailRb.setText(getString(R.string.tab_details));
        deviceRb.setText(getString(R.string.tab_device));
        homeRb.setText(getString(R.string.tab_mainpage));
        userRb.setText(getString(R.string.tab_user));


        tab_run_rb = (RadioButton) findViewById(R.id.tab_run_rb);
        dialog_layout = (RelativeLayout) findViewById(R.id.dialog_layout);

        morerun = findViewById(R.id.morerun);
        path = findViewById(R.id.path);
        weight = findViewById(R.id.weight);
//        booldpressure = (ImageView)this.findViewById(R.id.btn_item_bloodpressure);

        bottomBtn = (ImageView) findViewById(R.id.bottom_btn);
        add_layout = (RelativeLayout) findViewById(R.id.add_layout);
        content_root = findViewById(R.id.content_root);
        bottom_layout = (RelativeLayout) findViewById(R.id.bottom_layout);

        add_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugLog.d("add_layout.....................");
                if (mySectorMenu.isShown()) {
                    closeSectorMenu(10);
                    mySectorMenu.clear();
                }
            }
        });

        rlSyncho = findViewById(R.id.rlSyncho);
        tvSynchroProgress = (TextView) findViewById(R.id.tvSynchroProgress);

        /*final View rootView = findViewById(R.id.mroot);
        ScreenUtil.rootViewListener(rootView, new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                DebugLog.d("显示或者隐藏下方导航栏,rootViewHeight=" + rootView.getRootView().getHeight() + ",ScreenUtils.getScreenH()=" + ScreenUtils.getScreenH());
            }
        });*/

        main_title_rl = (RelativeLayout) this.findViewById(R.id.main_title_rl);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(main_title_rl.getLayoutParams());
        layoutParams.setMargins(0, ScreenUtil.getStatusBarHeight(getResources()), 0, 0);
        main_title_rl.setLayoutParams(layoutParams);

        mroot = (LinearLayout) this.findViewById(R.id.mroot);
        ll_title = (LinearLayout) this.findViewById(R.id.ll_title);

    }

    private AnimationDrawable animationDrawable;
    /**
     * 同步数据库到服务器
     * 开始同步数据库
     */
    public void startSyncho() {
        rlSyncho.setVisibility(View.VISIBLE);
        animationDrawable = (AnimationDrawable) animationImageView.getDrawable();
        animationDrawable.start();
        DebugLog.d("++-------------++startSyncho()    0%");
        tvSynchroProgress.setText("0%");
        rlSyncho.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }


    /**
     * 同步数据库到服务器
     * 同步数据库中...
     */
    public void synchoProgress(int progress) {
        DebugLog.d("++-------------++synchoProgress    %"+progress);
        tvSynchroProgress.setText(progress + "%");
        drawCircleView.setProgress(progress);
//        tvSynchroProgress.setText(getString(R.string.server_sync_data) + progress + "%");
    }

//    private BleScanTool bleScanTool = BleScanTool.getInstance();


    /**
     * 同步数据库到服务器
     * 结束同步
     */
    public void endSyncho() {
        rlSyncho.setVisibility(View.GONE);
    }

    public void initData() {
        latLngDb = new LatLngDb(this);
        activity = HomeActivity.this;
//        fragment = new PhotoFragment(this);
        mySectorMenu = new MySectorMenu();
        mySectorMenu.init(this);

        musicUtil = new MusicUtil(this, null);

        registerReceiver(updateSuccessReceiver, addIntentFilter());

        checkPermission();

        // 每次默认为false
        share.setSendMusicCmd(false);

        // 中间的 + 设置为不可见
        // 判断是否支持时间轴
        DebugLog.d("判断是否支持时间轴=" + FunctionsUnit.isSupportTimeLine());
        DebugLog.d("判断是否支持登录=" + FunctionsUnit.isSupportLogin());
        if (FunctionsUnit.isSupportTimeLine()) {
            tab_run_rb.setVisibility(View.VISIBLE);
        } else {
            tab_run_rb.setVisibility(View.GONE);
        }

        // 定位
        gaoDeMapUtil = new GaoDeMapModel(this);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // 时间类
        t = TimeUtil.getTime();


        // 开启天气预报开关
        FunctionInfos functionInfos = protocolUtils.getFunctionInfosByDb();
        if (functionInfos != null && functionInfos.weather) {//判断是否支持天气预报
            startWeatherTimer();
        }

        setTimer();
        googleFit = (int) SPUtils.get(UploadDataGoogleFitModle.googleFitKey, 0);
        if (googleFit == 1) {
            googleFitModle = UploadDataGoogleFitModle.getInstance(this);
            googleFitModle.buildFitnessClient();
        }

        registerFragmentMessageReceiver();

    }


    private void checkedFirmwareVersion(final BasicInfos basicInfos) {
        if (basicInfos == null) {
            DebugLog.d("basicInfos nullllllllllllllllll");
            return;
        }
        boolean isFirstBind = (boolean) SPUtils.get(FISRT_BIND, false);
        DebugLog.d("isFirstBind:" + isFirstBind + ",protocolUtils.getDeviceByDb().deivceId:" + basicInfos.deivceId);
        if (isFirstBind) {
            UpdateModel updateModel = new UpdateModel();
            SPUtils.put(HomeActivity.FISRT_BIND, false);
            updateModel.setDeviceId(basicInfos.deivceId);
            updateModel.setIGetDeviceUpdateInfoListener(new UpdateModel.IGetDeviceUpdateInfoListener() {
                @Override
                public void getUpdateDeviceInfo(final DeviceUpdateInfo deviceUpdateInfo) {
                    DebugLog.d("deviceUpdateInfo.getVersion():" + deviceUpdateInfo.getVersion());
                    DebugLog.d("protocolUtils.getDeviceByDb().getFirmwareVersion():" + basicInfos.firmwareVersion);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (deviceUpdateInfo.getVersion() > deviceUpdateInfo.getVersion()) {
                                showUpdateDialog(deviceUpdateInfo);
                            }
                        }
                    });

                }

                @Override
                public void getUpdateDeviceFaild() {
                }
            });

            updateModel.getDeviceUpdateInfo(false);
        }
    }

    public void showUpdateDialog(final DeviceUpdateInfo updateInfo) {
        final Dialog dialog = new Dialog(this, R.style.dialog);
        dialog.setContentView(R.layout.dialog_updata_msg);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCancelable(false);
        ((TextView) dialog.findViewById(R.id.set_header_img_title)).setText(getResources().getString(R.string.updata_dialog_title, updateInfo.version));
        dialog.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateDevice = new Intent(HomeActivity.this, DeviceUpateActivity.class);
                updateDevice.putExtra("deviceInfo", updateInfo);
                startActivity(updateDevice);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private IntentFilter addIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UpdateModel.ACTION_UPDATE_SUCEESS);
        intentFilter.addAction(MoreActivity.ACTION_RESTART_SUCEESS);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Constant.END_SPORT);
        intentFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        intentFilter.addAction(Constant.STOP_START_TIMER);
        intentFilter.addAction(Constant.SYNC_FINISH_ACTION);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        return intentFilter;
    }

    private void checkPermission() {
        //申请写文件的权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
        }
    }

    private void openSectorMenu() {
        mySectorMenu.isShown = false;
        if (mySectorMenu.isAnimating()) {
            return;
        }
        add_layout.setVisibility(View.VISIBLE);
        ll_title.setBackgroundColor(Color.parseColor("#AB000000"));
//        tab_run_rb.setVisibility(View.INVISIBLE);
        content_root.setVisibility(View.VISIBLE);
        content_root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mySectorMenu.startAnimationsOut3(dialog_layout, MySectorMenu.DURATIONMILLIS);
        bottomBtn.startAnimation(mySectorMenu.getRotateAnimation(fromDegrees, toDegrees, MySectorMenu.DURATIONMILLIS, 0));
    }

    private void closeSectorMenu(int durationmillis) {
        if (mySectorMenu.isAnimating()) {
            return;
        }

        content_root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        mySectorMenu.startAnimationsIn(dialog_layout, MySectorMenu.DURATIONMILLIS);
        bottomBtn.startAnimation(mySectorMenu.getRotateAnimation(toDegrees, fromDegrees, durationmillis, 0));

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ll_title.setBackgroundColor(Color.TRANSPARENT);
                add_layout.setVisibility(View.GONE);
                tab_run_rb.setVisibility(View.VISIBLE);
                content_root.setVisibility(View.GONE);
            }
        }, MySectorMenu.DURATIONMILLIS);
    }

    public void initEvent() {
        mTabThemeRg.setOnCheckedChangeListener(this);
        morerun.setOnClickListener(this);
        path.setOnClickListener(this);
        weight.setOnClickListener(this);
//        booldpressure.setOnClickListener(this);
        tab_run_rb.setOnClickListener(this);
        bottomBtn.setOnClickListener(this);

        protocolUtils.setBleListener(baseAppBleListener);
        share_btn.setOnClickListener(this);

        cb_date.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DebugLog.d("-----------------onCheckedChanged+isChecked:isChecked");
                sendBroadcast(new Intent().setAction(MyConstant.HOMEACTIVITY_CHECKBOX_CLICK).putExtra("CHECKBOX_CHECKED", isChecked));
            }
        });
    }

    private boolean isCheckedCenterBtn;
    private List<SportData> sportDatas;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tab_run_rb://
                DebugLog.d("mySectorMenu.isShown()1=" + mySectorMenu.isShown());
                share.setStartTimer(false);
                if (!mySectorMenu.isShown()) {
                    openSectorMenu();
                }
                break;
            case R.id.bottom_btn://
                DebugLog.d("mySectorMenu.isShown()2=" + mySectorMenu.isShown());
                if (mySectorMenu.isShown()) {
                    closeSectorMenu(MySectorMenu.DURATIONMILLIS);
                    mySectorMenu.clear();
                }
                break;
            case R.id.morerun://多运动界面
                DebugLog.d("多运动界面");
                startActivity(new Intent(activity, SportTypeActivity.class));
                if (mySectorMenu.isShown()) {
                    closeSectorMenu(MySectorMenu.DURATIONMILLIS);
                    mySectorMenu.clear();
                }
                break;
            case R.id.path://轨迹界面
                DebugLog.d("轨迹界面");
                Intent intent = new Intent(activity, PathActivity.class);
                /*Bundle bundle = new Bundle();
                bundle.putSerializable("sportDatas", (Serializable) sportDatas);
                intent.putExtras(bundle);*/
                startActivity(intent);
                if (mySectorMenu.isShown()) {
                    closeSectorMenu(MySectorMenu.DURATIONMILLIS);
                    mySectorMenu.clear();
                }
                break;
            case R.id.weight://体重界面
                DebugLog.d("体重界面");
                startActivity(new Intent(activity, WeightActivity.class));
//                String deviceMac = (String) SPUtils.get(BalanceSearchActivity.BALANCE_DEVICE, "");
//                if ((int) (SPUtils.get(WeightControlActivity.WEIGHT_TYPE, 0)) == 1) {
//                    startActivity(new Intent(activity, BalanceDataActivity.class));
//                } else if ((int) (SPUtils.get(WeightControlActivity.WEIGHT_TYPE, 0)) == 2){
//                    startActivity(new Intent(activity, HandAddActivity.class));
//                }else {
//                    startActivity(new Intent(activity, WeightControlActivity.class));
//                }
//                if ((int) (SPUtils.get(WeightControlActivity.WEIGHT_TYPE, 0)) == 0){
//                    startActivity(new Intent(activity, WeightControlActivity.class));
//                }else {
//                    startActivity(new Intent(activity, BalanceData2Activity.class));
//                }

                if (mySectorMenu.isShown()) {
                    closeSectorMenu(MySectorMenu.DURATIONMILLIS);
                    mySectorMenu.clear();
                }
                break;
//
//            case R.id.btn_item_bloodpressure://血压界面
//                startActivity(new Intent(activity, BloodpressureTestReadyActivity.class));
//                if (mySectorMenu.isShown()) {
//                    closeSectorMenu(MySectorMenu.DURATIONMILLIS);
//                    mySectorMenu.clear();
//                }
//                break;

            case R.id.share_btn://分享

                if (!NetUtils.isConnected(HomeActivity.this)) {
                    NormalToast.showToast(HomeActivity.this, getResources().getString(R.string.network_error), 3000);
                    return;
                }

                if (dialog != null && dialog.isShowing()) {
                    return;
                }

                // 应用弹窗
                dialog = DialogUtil.showShareDialog(HomeActivity.this, new BaseShareSelectListener(HomeActivity.this));

                break;
        }

    }

    /**
     * 设备是否连接
     *
     * @return
     */
    public static boolean isDeviceConnected() {
        return ProtocolUtils.getInstance().isAvailable() == BleConfig.SUCCESS;
    }

    @Override
    public void onStart() {
        super.onStart();
        // 设置是否可以自动重连
        protocolUtils.setCanConnect(true);
        // 初始化页面
        updatePage();

        protocolUtils.setProtocalCallBack(baseCallBack);
        protocolUtils.setProtocalCallBack(commonBaseCallBack);

        // 添加监听
        musicUtil.addListener();
//        googleFitDataModel.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        DebugLog.d("HomeActivity的onResume方法,ScreenUtil.getStatusBarHeight(getResources())=" + ScreenUtil.getStatusBarHeight(getResources()) + ",屏幕宽高=" + ScreenUtils.getScreenInfo() + ",测试==" + getResources().getDimension(R.dimen.y160));

        share.setMoveTaskToBack(false);

        /**
         * 启动和铃声有关的服务：来电、寻找手机
         */
        NoticeOnOff noticeOnOff = protocolUtils.getNotice();
        FindPhoneOnOff findPhoneOnOff = protocolUtils.getFindPhone();

        if (protocolUtils != null && (noticeOnOff.getCallonOff() || findPhoneOnOff.getOnOff() || noticeOnOff.getMsgonOff())) {
            DebugLog.d("开启服务---启动和铃声有关的服务");
            startService(new Intent(this, cn.dongha.ido.ui.services.AssistService.class));
        }

        /**
         * 启动智能提醒服务
         */
        if (protocolUtils != null && noticeOnOff != null) {
            if (share.getToggleSwitchState()) {
                if (noticeOnOff.getCalendaronOff() || noticeOnOff.getEmailonOff() || noticeOnOff.getMsgonOff() || noticeOnOff.getFacebookonOff() || noticeOnOff.getWxonOff() || noticeOnOff.getQQonOff() || noticeOnOff.getTwitteronOff() ||
                        noticeOnOff.getWhatsapponOff() || noticeOnOff.getLinkedinonOff() || noticeOnOff.getInstagramonOff() || noticeOnOff.getMessengeronOff()) {
                    DebugLog.d("开启服务---启动智能提醒服务,isNotificationListenerServiceEnabled(this)=" + isNotificationListenerServiceEnabled(this));
                    toggleNotificationListenerService();
                    startService(new Intent(this, cn.dongha.ido.ui.services.IntelligentNotificationService.class));
                }
            }
        }

        /**
         * 判断音乐开关是否开启：开启/启动服务   未开启/不开启服务
         */
        if (protocolUtils != null && protocolUtils.getMusicOnoff()) {
            // 开启服务
            DebugLog.d("开启服务---音乐控制模式");
            musicUtil.openMusicService();
        }

        // GPS
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor(Settings.System.LOCATION_PROVIDERS_ALLOWED), false, mGpsMonitor);

        // 判断GPS是否已经开启
        if (GpsUtil.isOPen(activity)) {
            share.setSportModelGpsSwitch(true);
        } else {
            share.setSportModelGpsSwitch(false);
        }

        // 判断是否切换了时区
        changeTimeZoneModel.handTimeZone();
        if (homePageType == HomePageType.MAIN) {
            share.setStartTimer(true);
            isStartTimer = true;
        }

        isSynchoData = true;
        DebugLog.d("..........." + getString(R.string.tab_details));
        detailRb.setText(getString(R.string.tab_details));
        deviceRb.setText(getString(R.string.tab_device));
        homeRb.setText(getString(R.string.tab_mainpage));
        userRb.setText(getString(R.string.tab_user));
//        googleFitDataModel.onResume();

        MobclickAgent.onResume(this);


        setWeather24();


        LogUtil.login_log("OnResume isLogin:" + MyApplication.getInstance().isLogin());
        new LoginModel().autoLogin();
        googleFit = (int) SPUtils.get(UploadDataGoogleFitModle.googleFitKey, 0);
        if (googleFit == 1 && googleFitModle == null) {
            googleFitModle = UploadDataGoogleFitModle.getInstance(this);
            googleFitModle.buildFitnessClient();
        }


        isBleCameraMode = (boolean) SPUtils.get(BLE_CAMERA_MODE_KEY, false);
        DebugLog.d("............................isBleCameraMode:" + isBleCameraMode);
        if (isBleCameraMode) {
            bleToAppOpenCamera();
        }
    }

    /**
     * 在零点发送天气数据.
     */
    private void setWeather24() {
        final Date date = new Date();
        date.setDate(date.getDate() + 1);
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(1);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        time24.cancel();
        time24 = new Timer();
        time24.schedule(new TimerTask() {
            @Override
            public void run() {
                DebugLog.d(",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,schedule:" + simpleDateFormat.format(new Date()));
                getWeatherFromService();
                new RankModel().setRankData();
            }
        }, date);
        long delay = date.getTime() - System.currentTimeMillis();
        DebugLog.d(".....................delay:" + delay);
        handler24.postDelayed(new Runnable() {
            @Override
            public void run() {
                DebugLog.d(",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,schedule:" + simpleDateFormat.format(new Date()));
                getWeatherFromService();
                new RankModel().setRankData();
            }
        }, delay < 0 ? 0 : delay);
        DebugLog.d(",,,,,,,,,,,,,schedule:" + simpleDateFormat.format(date));
        DebugLog.d(",,,,,,,,,,,,schedule:" + simpleDateFormat.format(new Date()));
    }

    private Timer time24 = new Timer();

    private void  toggleNotificationListenerService() {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this, cn.dongha.ido.ui.services.IntelligentNotificationService.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(this, cn.dongha.ido.ui.services.IntelligentNotificationService.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }

    @Override
    protected void onPause() {
        super.onPause();
        DebugLog.d("进入到后台");
        share.setStartTimer(false);
        isStartTimer = false;
        FileUtil.closeRes();
//        isSynchoData = false;
        MobclickAgent.onPause(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if (isCheckedCenterBtn) {
            isCheckedCenterBtn = false;
            return;
        }

        switch (checkedId) {
            case R.id.tab_mainpage_rb://主页
                homePageType = HomePageType.MAIN;
                if (!share.isSyncData()) {
                    share.setStartTimer(true);
                }
                currentIndex = 0;
                updatePage();
                break;
            case R.id.tab_details_rb://详情
                homePageType = HomePageType.DETAIL;
                share.setStartTimer(false);
                updatePage();
                currentIndex = 1;
                break;
            case R.id.tab_device_rb://设备
                homePageType = HomePageType.DEVICE;
                share.setStartTimer(false);
                updatePage();
                break;
            case R.id.tab_user_rb://我的
                homePageType = HomePageType.USER;
                share.setStartTimer(false);
                updatePage();
                currentIndex = 2;
                break;
            default:
                currentIndex = 3;
                DebugLog.d("main---onCheckedChanged---点击了中间的按钮");
                isCheckedCenterBtn = true;
                switch (homePageType) {
                    case MAIN:
                        ((RadioButton) findViewById(R.id.tab_mainpage_rb)).setChecked(true);
                        break;
                    case DETAIL:
                        ((RadioButton) findViewById(R.id.tab_details_rb)).setChecked(true);
                        break;
                    case DEVICE:
                        ((RadioButton) findViewById(R.id.tab_device_rb)).setChecked(true);
                        break;
                    case USER:
                        ((RadioButton) findViewById(R.id.tab_user_rb)).setChecked(true);
                        break;
                }

                break;
        }

    }

    /**
     * 切换Fragment
     */
    private void updatePage() {
        hideFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (homePageType) {
            case MAIN:

                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                    ft.add(R.id.home_framlayout_content, mainFragment, null);
                    ft.commitAllowingStateLoss();
                } else {
                    ft.show(mainFragment);
                    ft.commitAllowingStateLoss();
                    mainFragment.lazyLoad();
                }
                if (mainFragment.currentFragment == mainFragment.sportFragment && share.getFirstFinish()) {
                    share.setAnimationSwith(true);
                }
                if (mainFragment != null && mainFragment.isSyncing()) {
                    DebugLog.d("切换Fragment=" + mainFragment.isSyncing());
                    mainFragment.setAnimationIsStart();
                }
                if (!share.isSyncData() && mainFragment != null && mainFragment.currentFragment == mainFragment.sleepFragment && !share.getStartTimer()) {
                    share.setStartTimer(true);
                }
//                setTimer();
                isStartTimer = true;
                DebugLog.d("主页主题");
//                changeFragment(this, mainFragment, null);
                break;
            case DETAIL:
                if (detailFragment == null) {
                    detailFragment = new DetailFragment();
                    ft.add(R.id.home_framlayout_content, detailFragment, null);
                    ft.commitAllowingStateLoss();
                } else {
                    ft.show(detailFragment);
                    ft.commitAllowingStateLoss();
                    detailFragment.lazyLoad();
                }
//                closeTimer();
                isStartTimer = false;
                DebugLog.d("详情主题");
//                changeFragment(this, detailFragment, null);
                break;
            case DEVICE:
                if (deviceFragment == null) {
                    deviceFragment = new DeviceFragment();
                    ft.add(R.id.home_framlayout_content, deviceFragment, null);
                    ft.commitAllowingStateLoss();
                } else {
                    ft.show(deviceFragment);
                    ft.commitAllowingStateLoss();
                    deviceFragment.lazyLoad();
                }
//                closeTimer();
                isStartTimer = false;
                DebugLog.d("设备主题");
//                changeFragment(this, deviceFragment, null);
                break;
            case USER:
                if (myselfFragment == null) {
                    myselfFragment = new MyselfFragment();
                    ft.add(R.id.home_framlayout_content, myselfFragment, null);
                    ft.commitAllowingStateLoss();
                } else {
                    ft.show(myselfFragment);
                    ft.commitAllowingStateLoss();
                    myselfFragment.lazyLoad();
                }
//                closeTimer();
                isStartTimer = false;
                DebugLog.d("我的主题");
//                changeFragment(this, myselfFragment, null);
                break;
        }

    }

    //隐藏fragment
    public void hideFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mainFragment != null) {
            ft.hide(mainFragment);
        }
        if (detailFragment != null) {
            ft.hide(detailFragment);
        }
        if (deviceFragment != null) {
            ft.hide(deviceFragment);
        }
        if (myselfFragment != null) {
            ft.hide(myselfFragment);
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeTimer();
        homePageType = HomePageType.MAIN;
        share.setStartTimer(true);

//        if (share.isSyncData()) {
        share.setSyncData(false);
//        }
        musicUtil.removeListener();
        protocolUtils.removeProtocalCallBack(baseCallBack);
        protocolUtils.removeProtocalCallBack(commonBaseCallBack);
        protocolUtils.setUnConnect();
        protocolUtils.removeISynConfigCallBack();
        changeTimeZoneModel.closeResource();
        closeWeatherTimer();
        getContentResolver().unregisterContentObserver(mGpsMonitor);
        unregisterReceiver(updateSuccessReceiver);

        handler24.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
        if (time24 != null) {
            time24.cancel();
        }
//        AccessToken.setCurrentAccessToken(null);

        if (fragmentMessageReceiver != null) {
            unregisterReceiver(fragmentMessageReceiver);
        }

        if (baseAppBleListener != null) {
            protocolUtils.removeListener(baseAppBleListener);
        }
    }

    @Override
    public void cancelTakePhoto() {

    }

    public enum HomePageType {
        MAIN, DETAIL, RUN, DEVICE, USER
    }

    @Override
    public void onBackPressed() {

        if (mySectorMenu.isShown()) {
            closeSectorMenu(MySectorMenu.DURATIONMILLIS);
            return;
        }

        if ((System.currentTimeMillis() - lastOnclickTime) < 2000) {
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(0);
            share.setMoveTaskToBack(true);
            moveTaskToBack(true);

        } else {
            lastOnclickTime = System.currentTimeMillis();
            Toast toast = Toast.makeText(HomeActivity.this, getString(R.string.exit_app), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }

    private BasicInfos basicInfos;
    /**
     * 同步进度
     */
    private int progress;

    private int synchConfigProgress = 0;
    private int synchConfigValue = 0;
    /**
     * 同步失敗的次數
     */
    private int sychOutTime = 0;
    private boolean isBleCameraMode = false;
    public static final String BLE_CAMERA_MODE_KEY = "BLE_CAMERA_MODE_KEY";
    private boolean isQuickSync = true;
    /**
     * 回调监听
     */
    private BaseCallBack commonBaseCallBack = new BaseCallBack() {

        // 设备信息回调方法
        @Override
        public void onDeviceInfo(BasicInfos basicInfos) {
            super.onDeviceInfo(basicInfos);
            DebugLog.d("获取了设备信息");
            // 保存设备名称
            if (basicInfos != null && !basicInfos.getBasicName().equals("")) {
                share.setDeviceName(basicInfos.getBasicName());
            }
            HomeActivity.this.basicInfos = basicInfos;
            LogInfoModel.writePhoneAndDeviceInfo();
        }

        @Override
        public void onSysEvt(final int evt_base, final int evt_type, final int error, final int value) {
            super.onSysEvt(evt_base, evt_type, error, value);
            DebugLog.d("onSysEvt : " + ProtocolEvt.valueOf(evt_base).toString() + " type : " + ProtocolEvt.valueOf(evt_type).toString() + " error : " + error);
            handler.post(new Runnable() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void run() {


                    /**
                     * 在其他界面设置单位时，不需要同步
                     */
                    if (evt_type == ProtocolEvt.SET_CMD_UINT.toIndex()) {
                        synchConfigValue = 0;
                        boolean isSys = (boolean) SPUtils.get(ISSYN_DATA, true);
                        DebugLog.d("是否要同步数据.....isSys:" + isSys);
                        LogUtil.writeUpSynLogInfotoFile("isSys:" + isSys);
                        if (isSys) {
                            LogUtil.writeUpSynLogInfotoFile("SET_CMD_UINT:" + error);
//                            setCmdUnitCallBack(1);
                        }
                        SPUtils.put(ISSYN_DATA, true);
                    }
                    if (evt_type == ProtocolEvt.SYNC_EVT_HEALTH_SYNC_COMPLETE.toIndex()) {
                        LogUtil.writeUpSynLogInfotoFile("SYNC_EVT_HEALTH_SYNC_COMPLETE:" + error);
                        syncHealthComplete(error);

                    }
                    if (evt_type == ProtocolEvt.JSON_GET_ACTIVITY_COUNT.toIndex()) {
                        LogUtil.writeUpSynLogInfotoFile("JSON_GET_ACTIVITY_COUNT:" + error);
                        activityValue = 0;
                        getActivityCount(error);
                    }
                    if (evt_type == ProtocolEvt.SYNC_EVT_HEALTH_PROGRESS.toIndex()) {
                        isSynchOutTime = false;
                        LogUtil.writeUpSynLogInfotoFile("SYNC_EVT_HEALTH_PROGRESS:" + value);
                        synDataProgress(value);
                    }
                    // 同步活动数据完成的回调
                    if (evt_type == ProtocolEvt.ACTIVITY_SYNC_COMPLETE.toIndex() && error == ProtocolEvt.SUCCESS) {
                        LogUtil.writeUpSynLogInfotoFile("ACTIVITY_SYNC_COMPLETE:" + error);
                        syncActivityComplete(error);
                    }

                    //超时
                    if (evt_type == ProtocolEvt.SYNC_EVT_CONFIG_SYNC_COMPLETE.toIndex()) {
                        LogUtil.writeUpSynLogInfotoFile("SYNC_EVT_CONFIG_SYNC_COMPLETE:" + error);
                        syncConfigComplete(error);
                    }
                    // 收到ble打开相机的回调
                    if (evt_type == ProtocolEvt.BLE_TO_APP_OPEN_CAMERA.toIndex() && error == ProtocolEvt.SUCCESS) {
                        //收到打开相机的回调
                        DebugLog.d("收到打开相机的回调");
                        bleToAppOpenCamera();
                    }
                    if (evt_type == ProtocolEvt.BLE_TO_APP_CLOSE_CAMERA.toIndex() && error == ProtocolEvt.SUCCESS) {
                        isBleCameraMode = false;
                        SPUtils.put(BLE_CAMERA_MODE_KEY, false);
                    }
                    if (evt_type == ProtocolEvt.JSON_SET_WEATHER_DATA.toIndex()) {
                        resetWeather(error);
                    }

                    if (evt_type == ProtocolEvt.SYNC_EVT_ACTIVITY_PROCESSING.toIndex() && error == ProtocolEvt.SUCCESS) {
                        DebugLog.d("同步活动数据中.....value:" + value);
                        activityValue++;
                        syncActivityProgress(activityValue);
                    }
                    if (evt_type == ProtocolEvt.SYNC_EVT_CONFIG_FAST_SYNC_COMPLETE.toIndex()) {
                        DebugLog.d("SYNC_EVT_CONFIG_FAST_SYNC_COMPLETE");
                        share.setSyncData(false);
                        isQuickSync = false;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.writeUpSynLogInfotoFile("FAST_SYNC_COMPLETE---isSynchState:" + mainFragment.mainPageRelativeLayout_main.isSynchState());
                                if (mainFragment.mainPageRelativeLayout_main.isSynchState()) {
                                    mainFragment.mainPageRelativeLayout_main.onStartUpdate2();
                                }
                            }
                        }, 1000);

                    }
                    if (evt_type == ProtocolEvt.SWITCH_APP_END_REPLY.toIndex()) {

                        if (mainFragment != null) {
                            mainFragment.updateTimeLineData();
                        }
                    }


                }
                //同步回调
            });
        }

        private void syncActivityComplete(int error) {
            DebugLog.d("同步活动数据完成的回调");
            mainFragment.syncData(100);
            setTopTitlevisibility(0);
            DebugLog.d("++-------------++syncActivityComplete    同步活动数据完成" );
            tv_title.setText("正在同步(" + 100 + "%)");
            freshProgressPb.setProgress(100);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setTopTitlevisibility(1);
                }
            }, 1000);

            SPUtils.put(DeviceFragment.SYNCH_TIME_KEY, new Date().getTime());
            tempProgress = 0;
            if (!changeTimeZoneModel.isTimeZoneChangeding) {
                synSuccessOperate();
            } else {
                if (mainFragment != null) {
                    DebugLog.d("同步活动数据完成的回调刷新操作");
                    mainFragment.updateTimeLineData();
                }
            }
        }

        private void getActivityCount(int error) {
            if (error == ProtocolEvt.SUCCESS) {
                DebugLog.d("获取活动总数成功");
                sychOutTime = 0;
//                if (!share.isFirstSync()) {
                //2 再发送同步健康数据的命令
                protocolUtils.StartSyncHealthData();
//                }
            } else {
                synchConfigProgress = 0;
                synchConfigValue = 0;
//                sychOutTime++;
//                if (sychOutTime>1){
//                BleManager.getInstance().disconnection();
                sychOutTime = 0;
//                }
                DebugLog.d("获取活动总数超时");
                if (mainFragment != null) {
                    mainFragment.mainPageRelativeLayout_main.continueOrCancelSync();
                    setTopTitlevisibility(0);
                    tv_title.setText(R.string.fresh_failed);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setTopTitlevisibility(1);
                        }
                    }, 1000);
                }
            }
        }

        private void syncHealthComplete(int error) {

            synchConfigProgress = 0;
            synchConfigValue = 0;
            activityValue = 0;
            //同步成功后设置是否第一次同步为false
            SPUtils.put(ScanDeviceActivity.FIRST_BIND_SYN, false);
            if (error == ProtocolEvt.SUCCESS) {
                DebugLog.d("同步健康数据完成");
                if ((FunctionsUnit.isSupportTimeLine())) {
                    // 开始同步活动数据
                    protocolUtils.startSyncActivityData();
                }
            } else {
//                BleManager.getInstance().disconnection();
                DebugLog.d("同步健康数据超时");
                if (mainFragment != null && !changeTimeZoneModel.isTimeZoneChangeding) {
                    mainFragment.mainPageRelativeLayout_main.cancelSyncData();
                    setTopTitlevisibility(0);
                    tv_title.setText(R.string.fresh_failed);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setTopTitlevisibility(1);
                        }
                    }, 1000);
                }
            }
        }

        /**
         * 设置单位回调
         */
        private void setCmdUnitCallBack(int error) {
            isSynchoData = false;
            if (error == ProtocolEvt.SUCCESS) {
                DebugLog.d("发送单位设置成功....");
                LogUtil.writeUpSynLogInfotoFile("share.isFirstSync():" + share.isFirstSync());
                if (!share.isFirstSync()) {

                    DebugLog.d("非第一次同步命令");
                    if (!FunctionsUnit.isSupportTimeLine()) {
                        LogUtil.writeUpSynLogInfotoFile("protocolUtils.StartSyncHealthData()");
                        protocolUtils.StartSyncHealthData();
                    } else {
                        LogUtil.writeUpSynLogInfotoFile("protocolUtils.setGetActivityCount()");
                        //1 先发送获取活动总数的命令
                        protocolUtils.setGetActivityCount();
                        activityValue = 0;
                    }

                }
            } else {
                DebugLog.d("发送单位设置超时....");
                if (mainFragment != null) {
                    mainFragment.mainPageRelativeLayout_main.cancelSyncData();
                    setTopTitlevisibility(0);
                    tv_title.setText(R.string.fresh_failed);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setTopTitlevisibility(1);
                        }
                    }, 1000);
                }
            }

        }


        private void syncConfigComplete(int error) {
            SPUtils.put(ScanDeviceActivity.FIRST_BIND_SYN, false);
            if (error == 13 && share.isSyncData()) {
                isSynchOutTime = true;
                /**
                 * 同步配置超時后，如果超過2秒钟未同步健康数据。则视为同步失败
                 */
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isSynchOutTime) {
                            if (mainFragment != null) {
                                mainFragment.mainPageRelativeLayout_main.cancelSyncData();
                                setTopTitlevisibility(0);
                                tv_title.setText(R.string.fresh_failed);
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setTopTitlevisibility(1);
                                    }
                                }, 1000);
                            }
                        }
                    }
                }, 2000);
                /**
                 * 同步配置超时，断线后继续同步
                 */
                synchConfigProgress = 0;
                synchConfigValue = 0;
                sychOutTime = 0;
                if (mainFragment != null) {
                    mainFragment.mainPageRelativeLayout_main.continueOrCancelSync();
                    setTopTitlevisibility(0);
                    tv_title.setText(R.string.fresh_failed);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setTopTitlevisibility(1);
                        }
                    }, 1000);
                }
            }
        }

        private void resetWeather(int error) {
            DebugLog.d("设置天气成功");
            if (error == ProtocolEvt.SUCCESS) {
//                            new LoginModel().autoLogin();
                setWeatherFaildCount = 0;
            } else {
                //如果设置天气失败，则尝试三次
                setWeatherFaildCount++;
                if (ProtocolUtils.getInstance().getWeatherOnOff() && setWeatherFaildCount < 3) {
                    protocolUtils.setWeather(MyApplication.getInstance().weatherBean);
                }
            }
        }

        //同步活动数据中..
        private void syncActivityProgress(final int value) {
            /**
             * 如果活动总量大于50，则计算出百分比
             * 如果活动总量小于50 ，则直接累加
             */
            if (ProtocolUtils.activityCount > 50) {
                float p = value / (ProtocolUtils.activityCount * 1.0f);
                progress += (int) (p * ProtocolUtils.activityCount);
            } else {
                progress += value;
            }
            DebugLog.d("同步活动数据中.....progress:" + progress);
            if (progress > 99) {
                progress = 99;
            }
            mainFragment.syncData(progress);
            setTopTitlevisibility(0);
            DebugLog.d("++-------------++syncActivityProgress    %"+progress);
            tv_title.setText("正在同步(" + progress + "%)");
            freshProgressPb.setProgress(progress);
        }


        private void synDataProgress(int value) {
            DebugLog.d("正在同步信息健康数据value= " + value);
            if (mainFragment != null) {
                if ((FunctionsUnit.isSupportTimeLine())) {
                    int healthCount = 0;
                    if (BleSharedPreferences.getInstance().getIsFirst()) {
                        healthCount = 100 - ProtocolUtils.activityCount - synchConfigProgress;
                    } else {
                        healthCount = 100 - ProtocolUtils.activityCount;
                    }

                    //如果有多于50个活动数据，则前50%显示健康数据，后50%显示活动数据,如果少于50个，healthCount为显示健康数据,
                    if (ProtocolUtils.activityCount > 50) {
                        progress = (value / 2);
                    } else {
                        float p = value / 100f;
                        progress = (int) (p * healthCount);
                        //调用同步活动成功后progress=100;
                        if (progress == 100) {
                            progress = 99;
                        }
                    }
                    if (synchConfigProgress > 0) {
                        //如果是第一次同步。则前面10%同步配置。
                        if (progress < 10) {
                            progress = 10;
                        }
                    }
                    if (progress > 99) {
                        progress = 99;
                    }
                    DebugLog.d("正在同步信息健康数据progress= " + progress + ",synchConfigProgress:" + synchConfigProgress);

                } else {
                    if (synchConfigProgress > 0) {
                        //如果是第一次同步。则前面10%同步配置。
                        if (value < 10) {
                            value = 10;
                        }
                    }
                    DebugLog.d("正在同步信息健康数据progress= " + progress + ",synchConfigProgress:" + synchConfigProgress);
                    progress = value;   //不支持时间轴的还是和以前一样的同步
                    if (progress == 100) {
                        SPUtils.put(DeviceFragment.SYNCH_TIME_KEY, new Date().getTime());
                    }
                }
                /**
                 * 如果支持时间轴，则progress从0到50
                 * 不支持时间轴，则progress 从0到100;
                 */
                mainFragment.syncData(progress);
                setTopTitlevisibility(0);
                DebugLog.d("++-------------++synDataProgress    %" + progress);
                tv_title.setText("正在同步(" + progress + "%)");
                freshProgressPb.setProgress(progress);
                if (progress == 100) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setTopTitlevisibility(1);
                        }
                    }, 1000);

                    if (!changeTimeZoneModel.isTimeZoneChangeding) {// 如果时区改变，则发解绑命令
                        synSuccessOperate();
                    }

                }

            }
        }

        private void synSuccessOperate() {
            // 切换了时区，发送解绑命令
            share.setStartTimer(true);
            /**
             * 如果支持新功能.
             */
            if (FunctionsUnit.isSupportTimeLine()) {
                SPUtils.put(DBModel.isBackUp, true);
                if (ProtocolUtils.getInstance().getWeatherOnOff()) {
//                    protocolUtils.setWeather(MyApplication.getInstance().weatherBean);
                    getWeatherFromService();
                    setWeatherFaildCount = 0;
                }
            }

            checkedFirmwareVersion(basicInfos);
            new RankModel().setRankData();
        }


        /*实时数据回调*/
        @Override
        public void onLiveData(RealTimeHealthData realTimeHealthData) {
            super.onLiveData(realTimeHealthData);
//            DebugLog.d("实时数据回调");
            if (mainFragment != null && MyApplication.dateOffset == 0) {
                mainFragment.setRealTimeHealthData(realTimeHealthData);
            }
        }

        // 运动数据
        @Override
        public void onHealthSport(HealthSport healthSport, HealthSportAndItems healthSportAndItems) {
            super.onHealthSport(healthSport, healthSportAndItems);
            DebugLog.d("运动数据");
            if (mainFragment != null && MyApplication.dateOffset == 0) {
                mainFragment.setHealthSport(healthSport);
            }
        }

        // 睡眠数据
        @Override
        public void onSleepData(healthSleep healthSleep, healthSleepAndItems healthSleepAndItems) {
            super.onSleepData(healthSleep, healthSleepAndItems);
            DebugLog.d("睡眠数据");
            if (mainFragment != null && MyApplication.dateOffset == 0) {
                mainFragment.sethealthSleep(healthSleep);
            }
        }

        // 心率数据
        @Override
        public void onHealthHeartRate(HealthHeartRate healthHeartRate, HealthHeartRateAndItems healthHeartRateAndItems) {
            super.onHealthHeartRate(healthHeartRate, healthHeartRateAndItems);
            DebugLog.d("心率数据");
            if (mainFragment != null && MyApplication.dateOffset == 0) {
                mainFragment.setHealthHeartRate(healthHeartRate);
            }
        }

    };

    @TargetApi(Build.VERSION_CODES.M)
    private void bleToAppOpenCamera() {
        if (share.getMoveTaskToBack()) {
            isBleCameraMode = true;
            SPUtils.put(BLE_CAMERA_MODE_KEY, true);
            return;
        }

        //申请PERMISSION_GRANTED权限
        boolean isPermissions = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED;
        DebugLog.d("isPermissions:" + isPermissions);
        if (isPermissions) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 3);
        } else {
            Intent closeCamera = new Intent(activity, CameraActivity.class);
            closeCamera.putExtra("isSendCmd", false);
            startActivity(closeCamera);
        }
    }

    private int tempProgress = 0;
    private ISynConfigCallBack iSynConfigCallBack = new ISynConfigCallBack() {
        @Override
        public void synConfig() {

            boolean FIRST_BIND_SYN = (boolean) SPUtils.get(ScanDeviceActivity.FIRST_BIND_SYN, false);
            boolean isFirst = share.isFirstSync();
            DebugLog.d("isFirst:" + isFirst + ",isQuickSync:" + isQuickSync);

            if (isFirst) {
                synchConfigValue++;


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        float precent = synchConfigValue / 20.0f;

//                        if (precent * 10>=synchConfigProgress)
                        synchConfigProgress = (int) (precent * 10);
                        if (tempProgress <= synchConfigProgress) {
                            tempProgress = synchConfigProgress;
                        }

                        if (tempProgress > 10) {
                            tempProgress = 10;
                        }
                        synchConfigProgress = tempProgress;
                        mainFragment.syncData(synchConfigProgress);
                        setTopTitlevisibility(0);
                        DebugLog.d("++-------------++iSynConfigCallBack    %" + synchConfigProgress);
                        tv_title.setText("正在同步(" + synchConfigProgress + "%)");
                        freshProgressPb.setProgress(synchConfigProgress);
                        share.setSyncData(true);
                        DebugLog.d("同步配置中.....synchConfigProgress:" + synchConfigProgress + ",synchConfigValue:" + synchConfigValue);
                    }
                });
            }


        }
    };

    private int effectiveIndex;//有效索引值
    private int index;//索引值
    private int distance;// 距离
    private boolean isSetStartPoint = false;

    /**
     * 回调方法
     */
    private BaseCallBack baseCallBack = new BaseCallBack() {

        /*手环发起开始*/
        @Override
        public void onSwitchDataBleStart(SwitchDataBleStart switchDataBleStart, int i) {
            super.onSwitchDataBleStart(switchDataBleStart, i);
            effectiveIndex = -1;
            index = 0;
            distance = 0;
            DebugLog.d("测试手环发起开始switchDataBleStart=" + switchDataBleStart.toString() + ",时间=" + TimeUtil.timeStamp2Date(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
            // 时间戳
            time = TimeUtils.dateToStamp(t.year, t.month + 1, switchDataBleStart.day, switchDataBleStart.hour, switchDataBleStart.minute, switchDataBleStart.second);

            // 关闭实时数据
            isStartTimer = false;

            SwitchDataBleStartReply reply = new SwitchDataBleStartReply();
            reply.setRet_code(0);
            protocolUtils.bleSwitchDataStart(reply);

            // 开启后台定位
            // ①在activity中启动自定义本地服务LocationService
            activity.startService(new Intent(activity, LocationServices.class));
            //在LocationService中启动定位
            mLocationClient = new AMapLocationClient(getApplicationContext());
            mLocationOption = new AMapLocationClientOption();
            // 使用连续定位
            mLocationOption.setOnceLocation(false);
            // 每5秒定位一次
            mLocationOption.setInterval(5 * 1000);
            myLatLngs.clear();
            index = 0;
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    if (aMapLocation != null) {
                        if (aMapLocation.getAccuracy() > 300 || aMapLocation.getAccuracy() == 0) {
                            return;
                        } else {
                            MyLatLng myLatLng = new MyLatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                            myLatLng.time = TimeUtil.timeStamp2Date(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
//                            DebugLog.d("ble发起的后台定位服务---经度=" + myLatLng.latitude + ",维度=" + myLatLng.longitude + ",时间戳=" + myLatLng.time);
                            if (myLatLngs.size() > 0) {
                                MyLatLng lastMyLatLng = myLatLngs.get(myLatLngs.size() - 1);
                                if (lastMyLatLng.latitude != myLatLng.latitude || lastMyLatLng.longitude != lastMyLatLng.longitude) {
                                    if (myLatLng.latitude > 0 && myLatLng.longitude > 0) {
                                        FileUtil.writeLocationInfoToSdCard("ble发起的后台定位服务---经度=" + myLatLng.latitude + ",维度=" + myLatLng.longitude + ",时间戳=" + myLatLng.time);
                                        myLatLngs.add(myLatLng);
                                    }
                                }
                            } else {
                                if (myLatLng.latitude > 0 && myLatLng.longitude > 0) {
                                    FileUtil.writeLocationInfoToSdCard("ble发起的后台定位服务---第一次---经度=" + myLatLng.latitude + ",维度=" + myLatLng.longitude + ",时间戳=" + myLatLng.time);
                                    myLatLngs.add(myLatLng);
                                }
                            }
                        }
                    }
                }
            });
            mLocationClient.startLocation();
            FileUtil.initLocationLog();

        }

        /*手环发起交换中*/
        @Override
        public void onSwitchDataBleIng(SwitchDataBleIng switchDataBleIng, int i) {
            super.onSwitchDataBleIng(switchDataBleIng, i);
            DebugLog.d("测试手环发起交换中switchDataBleIng=" + switchDataBleIng.toString() + ",时间=" + TimeUtil.timeStamp2Date(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
            SwitchDataBleIngReply reply = new SwitchDataBleIngReply();
            if (myLatLngs != null && myLatLngs.size() > 0) {
                if (myLatLngs.size() > index) {
                    MyLatLng myLatLng1 = myLatLngs.get(myLatLngs.size() - 1);
                    LatLng latLng1 = new LatLng(myLatLng1.latitude, myLatLng1.longitude);
                    MyLatLng myLatLng2 = myLatLngs.get(index);
                    LatLng latLng2 = new LatLng(myLatLng2.latitude, myLatLng2.longitude);
                    index = myLatLngs.size() - 1;
                    distance += (int) AMapUtils.calculateLineDistance(latLng1, latLng2);
                    reply.setDistance(distance);
//                    reply.setDistance(100);
                    FileUtil.writeLocationInfoToSdCard("ble发起的后台定位服务---距离----距离---=" + distance);
                }
            } else {
                reply.setDistance(switchDataBleIng.distance);
            }
            DebugLog.d("测试手环发起交换中reply=" + (reply == null));
            protocolUtils.bleSwitchDataIng(reply);
        }

        /*手环发起结束时*/
        @Override
        public void onSwitchDataBleEnd(SwitchDataBleEnd switchDataBleEnd, int i) {
            super.onSwitchDataBleEnd(switchDataBleEnd, i);
            DebugLog.d("测试手环发起结束时switchDataBleEnd=" + switchDataBleEnd.toString());
            SwitchDataBleEndReply reply = new SwitchDataBleEndReply();
            reply.setRet_code(0);
            protocolUtils.bleSwitchDataEnd(reply);

            // 结束保存轨迹的经纬度并后台定位
            /*if (myLatLngs != null && myLatLngs.size() > 0) {
                for (MyLatLng myLatLng : myLatLngs) {
                    myLatLng.rid = time;
                    DebugLog.d("手环发起结束时经度=" + myLatLng.latitude + ",维度=" + myLatLng.longitude);
                }
                latLngDb.insert(myLatLngs);
            }*/
            mLocationClient.stopLocation();
            activity.stopService(new Intent(activity, LocationServices.class));

            // 更新时间轴页面
            if (mainFragment != null) {
//                mainFragment.updateSyncSuccess();

            }

            // 打开实时数据
            isStartTimer = true;

        }

    };


    /**
     * 设置定时器
     */
    private void setTimer() {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isDeviceConnected() && mainFragment != null && !mainFragment.isSyncing() && isStartTimer &&share != null
                        && !share.isFirstSync() &&null != changeTimeZoneModel&& !changeTimeZoneModel.isTimeZoneChangeding) {
                    protocolUtils.getLiveData();
                    DebugLog.d("发送实时命令》。。。");
                }
                if (mainFragment != null) {

                }
            }
        }, 0, 2000);
    }

    /**
     * 关闭定时器
     */
    public static void closeTimer() {
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
//        isStartTimer=false;
    }

    /**
     * 设置
     */
    public void setUnits() {
        Units units = protocolUtils.getUnits();
        if (units == null) {
            units = new Units();
        }
        units.setMode(UnitUtil.getMode());
        units.setStride(share.getStride());
        boolean is24 = TimeUtil.is24Hour(activity);
        if (is24) {
            units.setTimeMode(Constants.TIME_MODE_24);
        } else {
            units.setTimeMode(Constants.TIME_MODE_12);
        }
        boolean isZh = MyApplication.getInstance().isZh();
        if (isZh) {
            units.setLanguage(Constants.LANGUAGE_ZH);
        } else {
            units.setLanguage(Constants.LANGUAGE_EN);
        }
        protocolUtils.setUnit(units, true);
    }

    /**
     * 监听GPS状态
     */
    ContentObserver mGpsMonitor = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            boolean enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            DebugLog.d("监听GPS状态=" + enabled);
            share.setSportModelGpsSwitch(enabled);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        DebugLog.d("onRequestPermissionsResult.....");

        if (requestCode == 3) {
            MyApplication.getInstance().createDir();

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    public static void selectMainPage(Activity activity) {
        if (homePageType == HomePageType.MAIN && homeRb != null) {
            homeRb.setChecked(true);
        } else if (homePageType == HomePageType.DETAIL && detailRb != null) {
            detailRb.setChecked(true);
        } else if (homePageType == HomePageType.DEVICE && deviceRb != null) {
            deviceRb.setChecked(true);
            activity.startActivity(new Intent(activity, HomeActivity.class));
        } else if (homePageType == HomePageType.USER && homePageType != null) {
            userRb.setChecked(true);
            activity.startActivity(new Intent(activity, HomeActivity.class));
        }
        activity.startActivity(new Intent(activity, HomeActivity.class));
        activity.finish();
    }

    /**
     * 各个选项卡之间的切换
     */
    public void changeFragment(FragmentActivity activity, BaseFragment newFragment, String tag) {
        if (newFragment == null || mLastFragment == newFragment) {
            return;
        }
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        if ((mLastFragment != null) && (mLastFragment != newFragment)) {
            transaction.detach(mLastFragment);
        }

        if (!newFragment.isAdded()) {
            transaction.add(R.id.home_framlayout_content, newFragment, tag);
        }

        if (newFragment.isDetached()) {
            transaction.attach(newFragment);
        }

        mLastFragment = newFragment;
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onSaveInstanceState(Bundle paramBundle) {
        if (null != paramBundle) {
            if (mLastFragment != null && mLastFragment.isAdded()) {
                getSupportFragmentManager().putFragment(paramBundle, "mLastFragment", mLastFragment);
            }
        }
        super.onSaveInstanceState(paramBundle);
        DebugLog.d("onSaveInstanceState...........");
        paramBundle.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        DebugLog.d("onRestoreInstanceState...........");
        if (savedInstanceState != null) {
            mLastFragment = (BaseFragment) getSupportFragmentManager().getFragment(savedInstanceState, "mLastFragment");
        }
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        if (mainFragment == null && fragment instanceof MainFragment){
            mainFragment = (MainFragment)fragment;
        }
        if (detailFragment == null && fragment instanceof DetailFragment){
            detailFragment =(DetailFragment)fragment;
        }
        if (deviceFragment == null && fragment instanceof DeviceFragment){
            deviceFragment =(DeviceFragment) fragment;
        }
        if (myselfFragment == null && fragment instanceof MyselfFragment){
            myselfFragment =(MyselfFragment)fragment;
        }
    }




    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    /**
     * 打开天气预报定时器
     */
    private void startWeatherTimer() {
        if (scheduledThreadPoolExecutor == null) {
            scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        } else {
            scheduledThreadPoolExecutor.shutdown();
            scheduledThreadPoolExecutor = null;
            scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        }

        scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                handler.sendEmptyMessage(0);
                                                            }
                                                        }, 0, 30,
                TimeUnit.MINUTES);

    }

    /**
     * 关闭天气预报定时器
     */
    private void closeWeatherTimer() {
        if (scheduledThreadPoolExecutor != null) {
            scheduledThreadPoolExecutor.shutdown();
            scheduledThreadPoolExecutor = null;
        }
    }

    private int errorCount = 0;
    ;
    //天气预报数据的回调
    private PayloadCallback<ACMsg> callback;
    HttpUtil.HttpCallBack httpCallBack;

    /**
     * 从服务器上获取天气信息
     */
    private void getWeatherFromService() {

        errorCount = 0;
//        callback = new PayloadCallback<ACMsg>() {
//            @Override
//            public void success(ACMsg acMsg) {
//                if (!MyApplication.getInstance().isLogin()) {
//                    new LoginModel().autoLogin();
//                }
//
//            }
//
//            @Override
//            public void error(ACException e) {
//                if (!MyApplication.getInstance().isLogin()) {
//                    new LoginModel().autoLogin();
//                }
//                errorCount++;
//                //如果失败则去尝试请求三次,
//                if (errorCount < 3 && NetWorkUtil.isNetWorkConnected()) {
//                    weatherModel.getWeather(callback, true);
//                }
//            }
//
//        };
//        weatherModel.getWeather(callback, true);
        httpCallBack = new HttpUtil.HttpCallBack() {
            @Override
            public void onFailure(String errorString) {
                LogUtil.writeWeatherLogInfotoFile("onFailure......errorCount:" + errorCount);
                errorCount++;
                //如果失败则去尝试请求三次,
                if (errorCount < 3 && NetWorkUtil.isNetWorkConnected()) {
                    HttpUtil.getWeatherData(httpCallBack, true);
                }
            }

            @Override
            public void onSuccess(Response response) {
                LogUtil.writeWeatherLogInfotoFile("onSuccess......");
            }
        };
        HttpUtil.getWeatherData(httpCallBack, true);

    }

    private static boolean isNotificationListenerServiceEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        if (packageNames.contains(context.getPackageName())) {
            return true;
        }
        return false;
    }


    /**
     * 触摸事件
     */
    public interface MyTouchListener {
        public void onTouchEvent(MotionEvent event);
    }

    // 保存MyTouchListener接口的列表
    private ArrayList<MyTouchListener> myTouchListeners = new ArrayList<HomeActivity.MyTouchListener>();

    /**
     * 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法
     *
     * @param listener
     */
    public void registerMyTouchListener(MyTouchListener listener) {
        myTouchListeners.add(listener);
    }

    /**
     * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
     *
     * @param listener
     */
    public void unRegisterMyTouchListener(MyTouchListener listener) {
        myTouchListeners.remove(listener);
    }

    /**
     * 分发触摸事件给所有注册了MyTouchListener的接口
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyTouchListener listener : myTouchListeners) {
            listener.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void registerFragmentMessageReceiver() {
        if (fragmentMessageReceiver == null) {
            fragmentMessageReceiver = new FragmentMessageReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyConstant.HOMEACTIVITY_ACTION_ACTIVIY);
        intentFilter.addAction(MyConstant.HOMEACTIVITY_ACTION_SLEEP);
        intentFilter.addAction(MyConstant.HOMEACTIVITY_ACTION_HR);

        intentFilter.addAction(MyConstant.MAINFRAGMENT_CHECKBOX_SETBOOLEAN);
        intentFilter.addAction(MyConstant.MAINFRAGMENT_CHECKBOX_SETTEXT);

        intentFilter.addAction(MyConstant.MAINPAGERELATIVELAYOUT_ISLINKED);
        intentFilter.addAction(MyConstant.MAINFRAGMENT_ISSHOW);
        intentFilter.addAction(MyConstant.TODAYPOINTVIEW_ISANIMATION);
        intentFilter.addAction(MyConstant.MAINPAGERELATIVELAOUT_PULLDOWN);
        intentFilter.addAction(MyConstant.MAINPAGERELATIVELAOUT_PULLFREE);

        registerReceiver(fragmentMessageReceiver, intentFilter);
    }


    class FragmentMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 切换活动页面
            if (intent.getAction().equals(MyConstant.HOMEACTIVITY_ACTION_ACTIVIY)) {
                mroot.setBackground(getResources().getDrawable(R.drawable.bg_day));

            }
            // 切换睡眠页面
            else if (intent.getAction().equals(MyConstant.HOMEACTIVITY_ACTION_SLEEP)) {
                mroot.setBackground(getResources().getDrawable(R.drawable.bg_sleep));
            }
            // 切换心率页面
            else if (intent.getAction().equals(MyConstant.HOMEACTIVITY_ACTION_HR)) {
                mroot.setBackground(getResources().getDrawable(R.drawable.bg_heart));
            }
            // mainfragment checkbox 设置是否为true
            else if (intent.getAction().equals(MyConstant.MAINFRAGMENT_CHECKBOX_SETBOOLEAN)) {
                cb_date.setChecked(intent.getBooleanExtra("CHECKBOX_ISCHECK", false));
            }
            // mainfragment checkbox 设置text
            else if (intent.getAction().equals(MyConstant.MAINFRAGMENT_CHECKBOX_SETTEXT)) {
                String text = intent.getStringExtra("CHECKBOX_TEXT");
                if (!text.equals("")) {
                    cb_date.setText(text);
                }
            }

            // mainralativelayout  设置 是否连接
            else if (intent.getAction().equals(MyConstant.MAINPAGERELATIVELAYOUT_ISLINKED)) {
                if(intent.getBooleanExtra("ISLINKED",false)){
                    ib_link_state.setImageResource(R.drawable.link_state);
                }else {
                    ib_link_state.setImageResource(R.drawable.unlink_state);
                }
            }
            // mainfragment  是否显示
            else if (intent.getAction().equals(MyConstant.MAINFRAGMENT_ISSHOW)) {
                if(intent.getBooleanExtra("ISSHOW",false)){
                    main_title_rl.setVisibility(View.VISIBLE);
                }else {
                    main_title_rl.setVisibility(View.GONE);
                }
            }
            // 点击今天，弹出框是否正在动画中，设置checkbox是否可点
            else if (intent.getAction().equals(MyConstant.TODAYPOINTVIEW_ISANIMATION)) {
                if(intent.getBooleanExtra("ISANIMATION",false)){
                    cb_date.setEnabled(false);
                }else {
                    cb_date.setEnabled(true);
                }
            }
            // 主页下拉刷新，下拉动作
            else if (intent.getAction().equals(MyConstant.MAINPAGERELATIVELAOUT_PULLDOWN)) {
                setTopTitlevisibility(2);
            }
            // 主页下拉刷新，释放刷新动作
            else if (intent.getAction().equals(MyConstant.MAINPAGERELATIVELAOUT_PULLFREE)) {
                setTopTitlevisibility(1);
            }
        }
    }

    /**
     * 设置顶部checkbox 和 textview是否可见
     * 0为正在同步百分比状态，textview显示
     * 1为默认状态，checkbox显示
     * 2为下拉的状态，textview显示
     */
    private void setTopTitlevisibility(int mode) {
        switch (mode){
            case 0:
                cb_date.setVisibility(View.GONE);
                tv_title.setVisibility(View.VISIBLE);
                freshProgressPb.setProgress(0);
                freshProgressPb.setVisibility(View.VISIBLE);
                break;
            case 1:
                cb_date.setVisibility(View.VISIBLE);
                tv_title.setVisibility(View.GONE);
                freshProgressPb.setVisibility(View.GONE);
                freshProgressPb.setProgress(0);
                break;
            case 2:
                cb_date.setVisibility(View.GONE);
                tv_title.setVisibility(View.VISIBLE);
                tv_title.setText("下拉同步数据");
                freshProgressPb.setProgress(0);
                freshProgressPb.setVisibility(View.GONE);
                break;
        }

    }

    private BaseAppBleListener baseAppBleListener = new BaseAppBleListener() {
        @Override
        public void onDataSendTimeOut(byte[] bytes) {
            super.onDataSendTimeOut(bytes);
            DebugLog.d("..............连接超时了");

        }

        @Override
        public void onBlueToothError(int error) {
            super.onBlueToothError(error);
            DebugLog.d(".........蓝牙出错了");
            if (error == BleStatus.STATE_OFF) {
                setTopTitlevisibility(0);
                tv_title.setText(R.string.fresh_failed);
            }
        }

        @Override
        public void onBLEConnecting(String s) {
            super.onBLEConnecting(s);

//            long disConningTime=System.currentTimeMillis();

//            if (disConningTime-startConningTime>10000){
//                DebugLog.d("连接超过10秒。。。。。");
//                stopAnim();
//                isFirstDisConnect=false;
//            }
            if (mainFragment != null) {
                if (mainFragment.mainPageRelativeLayout_main != null) {
                    if (mainFragment.mainPageRelativeLayout_main.freshView != null) {
                        if (-mainFragment.mainPageRelativeLayout_main.getFreshViewHeight() == mainFragment.mainPageRelativeLayout_main.getFreshViewPaddingtop()) {
                            setTopTitlevisibility(0);
                            tv_title.setText("正在连接设备");
                        }
                    }

                }

            }

        }

        @Override
        public void onBLEConnected(BluetoothGatt bluetoothGatt) {
            super.onBLEConnected(bluetoothGatt);
            setTopTitlevisibility(1);
        }

        @Override
        public void onBLEDisConnected(String s) {
            super.onBLEDisConnected(s);


        }

    };

}

