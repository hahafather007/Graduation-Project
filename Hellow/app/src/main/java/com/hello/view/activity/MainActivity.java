package com.hello.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
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

import com.hello.R;
import com.hello.databinding.ActivityMainBinding;
import com.hello.utils.IntentUtil;
import com.hello.utils.ToastUtil;
import com.hello.view.fragment.BookFragment;
import com.hello.view.fragment.TodayTodoFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private ActivityMainBinding binding;
    private boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        Toolbar toolbar = binding.appBarMain.toolbar;
        setSupportActionBar(toolbar);
        drawer = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        binding.navView.setNavigationItemSelectedListener(this);

        initViewPager();
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
                IntentUtil.startActivity(this, SettingActivity.class);
                break;
            case R.id.nav_book:
                IntentUtil.startActivity(this, BookActivity.class);
                break;
            case R.id.nav_checkUpdate:
                ToastUtil.showToast(this, R.string.toast_checkUpdate);
                break;
            case R.id.nav_share:
                showShareView();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showShareView() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.text_share));
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getString(R.string.text_share_title)));
    }

    private void initViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new TodayTodoFragment());
        fragments.add(new BookFragment());

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
    }

    //双击返回键退出
    private void exitByDoubleClick() {
        Timer tExit;
        if (!isExit) {
            isExit = true;
            ToastUtil.showToast(this, R.string.exit_warn_msg);
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
