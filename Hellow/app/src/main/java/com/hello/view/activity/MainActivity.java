package com.hello.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import com.hello.R;
import com.hello.common.Constants;
import com.hello.databinding.ActivityMainBinding;
import com.hello.model.pref.HelloPref;
import com.hello.utils.DialogUtil;
import com.hello.utils.Log;
import com.hello.utils.ServiceUtil;
import com.hello.utils.ToastUtil;
import com.hello.view.fragment.NoteFragment;
import com.hello.view.fragment.TodayTodoFragment;
import com.hello.view.service.WakeUpService;
import com.hello.viewmodel.MainActivityViewModel;
import com.hello.widget.view.HeartFlyView;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhouwei.blurlibrary.EasyBlur;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.hello.common.Constants.ACTION_APP_CREATE;
import static com.hello.utils.IntentUtil.setupActivity;
import static com.hello.utils.ToastUtil.showToast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NoteFragment.OnNewsCreateListener {
    private DrawerLayout drawer;
    private ActivityMainBinding binding;
    private InputMethodManager inputMethodManager;
    private List<OnPageScrollListener> listeners;
    private List<OnListenedNewsCreateListener> createListeners;
    private Tencent tencent;
    //QQ登录的监听器
    private IUiListener iUiListener;
    private boolean isExit = false;

    @Inject
    MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidInjection.inject(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        listeners = new ArrayList<>();
        createListeners = new ArrayList<>();

        tencent = Tencent.createInstance(Constants.QQ_APPID, this);
        iUiListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject object = (JSONObject) o;

                try {
                    String openId = object.getString("openid");
                    String token = object.getString("access_token");
                    String expires = object.getString("expires_in");

                    HelloPref.INSTANCE.setOpenId(openId);
                    HelloPref.INSTANCE.setToken(token);
                    HelloPref.INSTANCE.setExpires(expires);

                    tencent.setOpenId(openId);
                    tencent.setAccessToken(token, expires);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getUserInfo();

                ToastUtil.showToast(MainActivity.this, R.string.text_login_success);
            }

            @Override
            public void onError(UiError uiError) {
                ToastUtil.showToast(MainActivity.this, R.string.text_login_error);
            }

            @Override
            public void onCancel() {
                Log.i("登录已取消");
            }
        };

        if (HelloPref.INSTANCE.isLogin()) {
            tencent.setOpenId(HelloPref.INSTANCE.getOpenId());
            tencent.setAccessToken(HelloPref.INSTANCE.getToken(), HelloPref.INSTANCE.getExpires());

            getUserInfo();
        } else {
            binding.navView.getMenu().findItem(R.id.nav_exit).setVisible(false);
        }

        initDrawer();
        initViewPager();
        initFlyView();
        initUserGround();

        //如果设置了后台唤醒且service没有运行就启动
        if (HelloPref.INSTANCE.isCanWakeup()
                && !ServiceUtil.isSerivceRunning(this, WakeUpService.class.getName())) {
            Intent intent = new Intent(this, WakeUpService.class);
            intent.setAction(ACTION_APP_CREATE);
            startService(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            exitByDoubleClick();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_setting:
                setupActivity(this, SettingActivity.class);
                break;
            case R.id.nav_book:
                setupActivity(this, BookActivity.class);
                break;
            case R.id.nav_checkUpdate:
                showToast(this, R.string.toast_checkUpdate);
                break;
            case R.id.nav_about:
                setupActivity(this, AboutActivity.class);
                break;
            case R.id.nav_share:
                showShareView();
                break;
            case R.id.nav_exit:
                logout();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDestroy() {
        viewModel.onCleared();
        listeners.clear();
        createListeners.clear();

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, iUiListener);
    }

    public void addScrollListener(OnPageScrollListener listener) {
        listeners.add(listener);
    }

    public void removeScrollListener(OnPageScrollListener listener) {
        listeners.remove(listener);
    }

    public void addCreateListener(OnListenedNewsCreateListener listener) {
        createListeners.add(listener);
    }

    public void removeCreateListener(OnListenedNewsCreateListener listener) {
        createListeners.remove(listener);
    }

    private void logout() {
        DialogUtil.showDialog(this, R.string.text_dialog_logout_msg,
                R.string.text_cancel, R.string.text_enter, null,
                (__, ___) -> {
                    tencent.logout(this);

                    HelloPref.INSTANCE.setLogin(false);
                    HelloPref.INSTANCE.setExpires(null);
                    HelloPref.INSTANCE.setToken(null);
                    HelloPref.INSTANCE.setOpenId(null);
                    HelloPref.INSTANCE.setImage(null);
                    HelloPref.INSTANCE.setName(null);

                    binding.navView.getMenu().findItem(R.id.nav_exit).setVisible(false);
                    ((ImageView) binding.navView.getHeaderView(0)
                            .findViewById(R.id.headerView)).setImageResource(R.drawable.image_logo_head);
                    ((TextView) binding.navView.getHeaderView(0)
                            .findViewById(R.id.headerText)).setText(R.string.drawer_click_to_login);
                    ToastUtil.showToast(this, R.string.text_logout_success);
                });
    }

    private void getUserInfo() {
        QQToken token = tencent.getQQToken();
        UserInfo info = new UserInfo(this, token);

        info.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject object = (JSONObject) o;

                try {
                    String name = object.getString("nickname");
                    String image = object.getString("figureurl_qq_2");

                    HelloPref.INSTANCE.setImage(image);
                    HelloPref.INSTANCE.setName(name);

                    Glide.with(MainActivity.this)
                            .load(image)
                            .into((ImageView) binding.navView.getHeaderView(0).findViewById(R.id.headerView));
                    ((TextView) binding.navView.getHeaderView(0).findViewById(R.id.headerText)).setText(name);

                    HelloPref.INSTANCE.setLogin(true);

                    binding.navView.getMenu().findItem(R.id.nav_exit).setVisible(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                tencent.logout(MainActivity.this);

                HelloPref.INSTANCE.setLogin(false);
                HelloPref.INSTANCE.setExpires(null);
                HelloPref.INSTANCE.setToken(null);
                HelloPref.INSTANCE.setOpenId(null);
                HelloPref.INSTANCE.setImage(null);
                HelloPref.INSTANCE.setName(null);

                ToastUtil.showToast(MainActivity.this, R.string.text_reLogin);
            }

            @Override
            public void onCancel() {
            }
        });
    }

    private void initDrawer() {
        Toolbar toolbar = binding.appBarMain.toolbar;
        toolbar.setTitle(R.string.app_name);
        drawer = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        binding.navView.setNavigationItemSelectedListener(this);

        binding.navView.getHeaderView(0).findViewById(R.id.headerView).setOnClickListener(__ -> {
            if (!HelloPref.INSTANCE.isLogin()) {
                tencent.login(this, "all", iUiListener);
            }
        });
    }

    private void initFlyView() {
        HeartFlyView flyView = binding.navView.getHeaderView(0).findViewById(R.id.flyView);
        flyView.post(() -> {
            flyView.setDefaultDrawableList();
            flyView.setScaleAnimation(0.5f, 1.5f);
            flyView.setMinHeartNum(Integer.MAX_VALUE);
            flyView.setMaxHeartNum(Integer.MAX_VALUE);
            flyView.setOriginsOffset(flyView.getWidth() / 2);
            flyView.setAnimationDelay(100);
            flyView.setBottomPadding(-flyView.getHeight());
            flyView.startAnimation(flyView.getWidth(), flyView.getHeight());
        });
    }

    //初始化用户头像背景
    private void initUserGround() {
        View view = binding.navView.getHeaderView(0).findViewById(R.id.headerLayout);
        view.setBackground(new BitmapDrawable(EasyBlur.with(this)
                .policy(EasyBlur.BlurPolicy.FAST_BLUR)
                .bitmap(((BitmapDrawable) view.getBackground()).getBitmap())
                .radius(10)
                .blur()));
    }

    private void showShareView() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.text_share));
        intent.setType("text/plain");
        startActivity(intent);
    }

    private void initViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new TodayTodoFragment());
        fragments.add(new NoteFragment());

        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.today_toda_title));
        titles.add(getString(R.string.book_title));

        ViewPager viewPager = binding.appBarMain.mainViewPager;
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles.get(position);
            }
        });
        binding.appBarMain.mainTab.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                inputMethodManager.hideSoftInputFromWindow(
                        getWindow().getDecorView().getWindowToken(), 0);

                Stream.of(listeners).forEach(OnPageScrollListener::onScroll);
            }
        });
    }

    //双击返回键退出
    private void exitByDoubleClick() {
        Timer tExit;
        if (!isExit) {
            isExit = true;
            showToast(this, R.string.exit_warn_msg);
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;//取消退出
                }
            }, 2000);// 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onOpenCreate() {
        Stream.of(createListeners).forEach(OnListenedNewsCreateListener::onOpenCreate);
    }

    @Override
    public void onOpenLogin() {
        tencent.login(this, "all", iUiListener);
    }

    public interface OnPageScrollListener {
        void onScroll();
    }

    public interface OnListenedNewsCreateListener {
        void onOpenCreate();
    }
}
