package com.hello.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
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
import android.view.inputmethod.InputMethodManager;

import com.hello.R;
import com.hello.databinding.ActivityMainBinding;
import com.hello.view.fragment.NoteFragment;
import com.hello.view.fragment.TodayTodoFragment;
import com.hello.viewmodel.MainActivityViewModel;
import com.hello.widget.view.HeartFlyView;
import com.zhouwei.blurlibrary.EasyBlur;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import static com.hello.utils.IntentUtil.setupActivity;
import static com.hello.utils.ToastUtil.showToast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private ActivityMainBinding binding;
    private InputMethodManager inputMethodManager;
    private boolean isExit = false;

    @Inject
    MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        initDrawer();
        initViewPager();
        initFlyView();
        initUserGround();
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
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        viewModel.onCleared();
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
        binding.navView.getHeaderView(0).findViewById(R.id.headerLayout)
                .setBackground(new BitmapDrawable(EasyBlur.with(this)
                        .bitmap(BitmapFactory.decodeResource(getResources(), R.drawable.img_user_ground))
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
}
