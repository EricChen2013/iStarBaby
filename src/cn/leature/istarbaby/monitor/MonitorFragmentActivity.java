package cn.leature.istarbaby.monitor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.slidingmenu.SlidingMenu;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.LcLog;

public class MonitorFragmentActivity extends FragmentActivity implements
        OnClickListener, IRegisterMonitorListener {
    private final String TAG = "MonitorFragmentActivity";

    public static final int MESSAGE_CHANGE_FRAGMENT_LIST = 1000;
    public static final int MESSAGE_CHANGE_FRAGMENT_ADD = 1001;
    public static final int MESSAGE_CHANGE_MONITOR_DELETE = 1002;
    public static final int MESSAGE_UID_SCAN_RESULT = 1003;
    public static final int MESSAGE_SHOW_CAMERA_LIST = 1004;
    public static final int MESSAGE_RECONNECT_CAMERA_LIST = 1005;

    // 用户登录信息
    private String mLoginUserId;

    private ImageView mBtn_List, mBtn_Event, mBtn_Add, mBtn_photo;
    private TextView mTxt_List, mTxt_Event, mTxt_Add, mTxt_photo;

    private FragmentManager fm;
    private FragmentTransaction ft;
    private Fragment fgMonitorList, fgEvents, fgPhotoes, fgAddMonitor;
    private ImageButton showLeftMenu, mTitleBarPost;

    private List<MonitorInfo> mListItems;
    private List<MyMonitor> mMonitorList;
    private MonitorShareModel mShareMonitor = null;

    private int mCurrentPage = 0;
    private boolean isReloadListData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sliding_main);

        // 检查用户登录状况
        checkUserLogin();

        loadLocalMonitors();

        initView();

        // 通知启动判断
        (new ThreadNotificationEvent(this.getIntent())).start();
    }

    class ThreadNotificationEvent extends Thread {
        private Intent bIntent;

        public void run() {
            Log.i("MonitorListenService", "---->ThreadCamerasConnect start");

            if (bIntent.getBooleanExtra("notification", false)) {
                // 从消息启动， 跳转到事件列表
                String uid = bIntent.getStringExtra("monitor_uid");
                if (!"".equals(uid)) {
                    mShareMonitor.saveCurrentMonitor(uid);

                    Intent intent = new Intent();
                    intent.setClass(MonitorFragmentActivity.this,
                            MonitorEventDetailActivity.class);

                    Bundle mBundle = new Bundle();

                    LcLog.i(TAG, "[onStart] userId:" + mLoginUserId + ",uid:"
                            + uid);

                    MonitorInfo monitorInfo = MonitorDBManager.loadMonitorInfo(
                            MonitorFragmentActivity.this, uid, mLoginUserId);
                    mBundle.putSerializable("monitor_selected", monitorInfo);
                    intent.putExtras(mBundle);

                    startActivityForResult(intent,
                            ConstantDef.REQUEST_CODE_MONITOR_EVENT);

                    // 设定启动动画
                    overridePendingTransition(R.anim.activity_in_from_right,
                            R.anim.activity_nothing_in_out);
                }
            }
        }

        public ThreadNotificationEvent(Intent intent) {
            this.bIntent = intent;
        }
    }

    private void initView() {

        // 设置主菜单
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMain);
        View leftView = getLayoutInflater()
                .inflate(R.layout.sliding_menu, null);
        View centerView = getLayoutInflater().inflate(
                R.layout.activity_monitor_fragment, null);

        mSlidingMenu.setLeftView(leftView);
        mSlidingMenu.setCenterView(centerView);

        // titlebar
        showLeftMenu = (ImageButton) centerView
                .findViewById(R.id.titlebar_leftbtn);
        showLeftMenu.setBackgroundResource(R.drawable.selector_hd_menu);
        showLeftMenu.setOnClickListener(this);
        mTitleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
        mTitleBarPost = (ImageButton) this
                .findViewById(R.id.titlebar_rightbtn1);

        // bottombar
        mBtn_List = (ImageView) centerView.findViewById(R.id.bottom_btn_list);
        mBtn_Event = (ImageView) centerView.findViewById(R.id.bottom_btn_event);
        mBtn_photo = (ImageView) centerView.findViewById(R.id.bottom_btn_photo);
        mBtn_Add = (ImageView) centerView.findViewById(R.id.bottom_btn_add);

        mBtn_List.setOnClickListener(this);
        mBtn_Event.setOnClickListener(this);
        mBtn_Add.setOnClickListener(this);
        mBtn_photo.setOnClickListener(this);

        mTxt_List = (TextView) centerView.findViewById(R.id.bottom_text_list);
        mTxt_Event = (TextView) centerView.findViewById(R.id.bottom_text_event);
        mTxt_photo = (TextView) centerView.findViewById(R.id.bottom_text_photo);
        mTxt_Add = (TextView) centerView.findViewById(R.id.bottom_text_add);

        mTxt_List.setOnClickListener(this);
        mTxt_Event.setOnClickListener(this);
        mTxt_photo.setOnClickListener(this);
        mTxt_Add.setOnClickListener(this);

        fgMonitorList = MonitorListActivity.newInstance(R.id.framentlayout,
                mBtn_List, mBtn_Event, mBtn_Add, mBtn_photo, mTitleBarTitle,
                mTitleBarPost, mHandler);
        fgEvents = MonitorEventActivity.newInstance(R.id.framentlayout,
                mBtn_List, mBtn_Event, mBtn_Add, mBtn_photo, mTitleBarTitle,
                mTitleBarPost);
        fgPhotoes = MonitorPhotoActivity.newInstance(R.id.framentlayout,
                mBtn_List, mBtn_Event, mBtn_Add, mBtn_photo, mTitleBarTitle,
                mTitleBarPost);
        fgAddMonitor = MonitorAddActivity.newInstance(R.id.framentlayout,
                mBtn_List, mBtn_Event, mBtn_Add, mBtn_photo, mTitleBarTitle,
                mTitleBarPost, mHandler);

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.add(R.id.framentlayout, fgMonitorList).commit();
    }

    private void checkUserLogin() {
        // 取得用户登录信息
        mLoginUserId = LoginInfo.getLoginUserId(this);
    }

    private void loadLocalMonitors() {
        mShareMonitor = MonitorShareModel.getInstance();
        mShareMonitor.release();

        mListItems = new ArrayList<MonitorInfo>();
        mMonitorList = new ArrayList<MyMonitor>();

        List<MonitorInfo> list = MonitorDBManager.loadMonitorInfoList(this,
                mLoginUserId);
        for (int i = 0; i < list.size(); i++) {
            MonitorInfo monitorInfo = list.get(i);
            monitorInfo.Status = "连接中...";
            mListItems.add(monitorInfo);

            if (monitorInfo.UID.length() == 0) {
                continue;
            }

            MyMonitor myMonitor = new MyMonitor(monitorInfo.UID,
                    monitorInfo.ViewAccount, monitorInfo.ViewPassword);

            myMonitor.registerIOTCListener(this);

            mMonitorList.add(myMonitor);
        }

        // 增加一行 新增摄像机
        mListItems.add(new MonitorInfo("", "", "请按此新增监护器", "", "", "", ""));

        mShareMonitor.setMonitorListsAndItems(mListItems, mMonitorList);
        if (isReloadListData) {
            isReloadListData = false;
            doReloadFragmentData();
        }

        // 启动服务
        Intent intent = new Intent("cn.leature.istarbaby.monitor.listenservice");
        Bundle bundle = new Bundle();
        bundle.putString("start_channel", "list");
        intent.putExtras(bundle);
        startService(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LcLog.e(TAG, "--->onActivityResult.");

        if (requestCode == ConstantDef.RESULT_CODE_MONITOR_EVENT) {
            // 消息启动后返回，要重新连接
            mHandler.sendEmptyMessage(MESSAGE_RECONNECT_CAMERA_LIST);
        }
    }

    @Override
    public void onPause() {
        LcLog.i(TAG, "---->onPause.");
        mShareMonitor.unregisterIOTCListener(this);

        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_leftbtn:
                // 打开主菜单（从左边划出）
                mSlidingMenu.showLeftView();
                break;
            case R.id.bottom_btn_list:
            case R.id.bottom_text_list:
                mCurrentPage = 0;
                doChangeFragment();
                break;
            case R.id.bottom_btn_event:
            case R.id.bottom_text_event:
                mCurrentPage = 1;
                doChangeFragment();
                break;
            case R.id.bottom_btn_photo:
            case R.id.bottom_text_photo:
                mCurrentPage = 2;
                doChangeFragment();
                break;
            case R.id.bottom_btn_add:
            case R.id.bottom_text_add:
                mCurrentPage = 3;
                doChangeFragment();
                break;
        }
    }

    private void doChangeFragment() {
        Fragment fragment = fgMonitorList;
        switch (mCurrentPage) {
            case 0:
                fragment = fgMonitorList;
                break;
            case 1:
                fragment = fgEvents;
                break;
            case 2:
                fragment = fgPhotoes;
                break;
            case 3:
                fragment = fgAddMonitor;
                break;
        }

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out);
        ft.replace(R.id.framentlayout, fragment).commit();
    }

    private boolean ispress;

    private SlidingMenu mSlidingMenu;

    private TextView mTitleBarTitle;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (ispress) {
                mShareMonitor.logout(this);
                finish();
            } else {
                Toast.makeText(MonitorFragmentActivity.this, "请再按一次退出",
                        Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        ispress = false;
                    }
                }, 5000);

                ispress = true;
            }
        }
        return true;

    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_CHANGE_FRAGMENT_LIST:
                    // 页面跳转
                    isReloadListData = true;
                    loadLocalMonitors();

                    mCurrentPage = 0;
                    doChangeFragment();
                    break;
                case MESSAGE_CHANGE_FRAGMENT_ADD:
                    // 页面跳转
                    mCurrentPage = 3;
                    doChangeFragment();
                    break;
                case MESSAGE_UID_SCAN_RESULT:
                    // 显示扫描结果
                    mCurrentPage = 3;
                    doShowUidScanCode(msg.obj.toString());
                    break;
                case MESSAGE_SHOW_CAMERA_LIST:
                    // 页面跳转
                    doReloadFragmentData();
                    break;
                case MESSAGE_RECONNECT_CAMERA_LIST:
                case MESSAGE_CHANGE_MONITOR_DELETE:
                    // 重新连接
                    isReloadListData = true;
                    loadLocalMonitors();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void receiveChannelInfo(MonitorClient paramCamera, int paramInt1,
                                   int paramInt2) {
        LcLog.i(TAG, "[receiveChannelInfo].paramInt:" + paramInt2 + ",uid:"
                + ((MyMonitor) paramCamera).getUID());

        for (int i = 0; i < mListItems.size(); i++) {
            MonitorInfo monitorInfo = mListItems.get(i);
            if (monitorInfo.UID == ((MyMonitor) paramCamera).getUID()) {
                if (paramInt2 == 1) {
                    // 表示开始接收
                    continue;
                } else if (paramInt2 == 5) {
                    monitorInfo.Status = "账号或密码错误";
                } else if (paramInt2 == 6) {
                    monitorInfo.Status = "连接超时";
                } else if (paramInt2 == 2) {
                    monitorInfo.Status = "已联机";
                } else {
                    monitorInfo.Status = "未联机";
                }

                mHandler.sendEmptyMessage(MESSAGE_SHOW_CAMERA_LIST);
                break;
            }
        }
    }

    protected void doShowUidScanCode(String result) {
        if (mCurrentPage == 3) {
            ((MonitorAddActivity) fgAddMonitor).reloadMonitorAdd(result);
        }
    }

    protected void doReloadFragmentData() {
        if (mCurrentPage == 0) {
            ((MonitorListActivity) fgMonitorList).reloadMonitorList();
        } else if (mCurrentPage == 1) {
            ((MonitorEventActivity) fgEvents).refreshMonitorList();
        }
    }

    @Override
    public void receiveFrameData(MonitorClient paramCamera, int paramInt,
                                 Bitmap paramBitmap) {
        // TODO Auto-generated method stub

    }

    @Override
    public void receiveFrameInfo(MonitorClient paramCamera, int paramInt1,
                                 long paramLong, int paramInt2, int paramInt3, int paramInt4,
                                 int paramInt5) {
        // TODO Auto-generated method stub

    }

    @Override
    public void receiveIOCtrlData(MonitorClient paramCamera, int channel,
                                  int ioCtrlType, byte[] paramArrayOfByte) {
        // TODO Auto-generated method stub

    }

    @Override
    public void receiveSessionInfo(MonitorClient paramCamera, int paramInt) {
        LcLog.i(TAG, "[receiveSessionInfo].paramInt:" + paramInt + ",uid:"
                + ((MyMonitor) paramCamera).getUID());

        for (int i = 0; i < mListItems.size(); i++) {
            MonitorInfo monitorInfo = mListItems.get(i);
            if (monitorInfo.UID.equals(((MyMonitor) paramCamera).getUID())) {
                if (paramInt == 1) {
                    // 表示开始接收
                    continue;
                } else if (paramInt == 4) {
                    monitorInfo.Status = "未知设备";
                } else if (paramInt == 2) {
                    monitorInfo.Status = "在线";
                } else {
                    monitorInfo.Status = "连接失败";
                }

                mHandler.sendEmptyMessage(MESSAGE_SHOW_CAMERA_LIST);
                break;
            }
        }
    }

}
