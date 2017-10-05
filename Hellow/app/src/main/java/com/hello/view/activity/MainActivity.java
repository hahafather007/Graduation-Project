package com.hello.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.hello.R;
import com.hello.databinding.ActivityMainBinding;
import com.hello.utils.IntentUtil;
import com.hello.utils.ToastUtil;
import com.hello.view.fragment.BookFragment;
import com.hello.view.fragment.TodayTodoFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public long durationMillis = 300;
    public int verticalanimUp = 1;
    public int verticalanimDown = 0;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private DrawerLayout drawer;
    public ObservableBoolean isShowMore = new ObservableBoolean(false);
    private ActivityMainBinding binding;
    private LinearLayout showMoreLayout;
    private LinearLayout voiceLayout;
    private Button btnVoice;
    private EditText editVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setView(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initViewPager();
        initView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_setting:
                IntentUtil.startActivity(this, SettingActivity.class);
                break;
            case R.id.nav_book:
                IntentUtil.startActivity(this, BookActivity.class);
                break;
            case R.id.nav_checkUpdate:
                ToastUtil.showToast(this, getResources().getString(R.string.toast_checkUpdate));
                break;
            case R.id.nav_share:
                showShareView();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_showMore:
                editVoice.clearFocus();
                //隐藏软键盘
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if (isShowMore.get()) {
                    hideMore();
                } else {
                    showMore();
                }
                break;
        }
    }

    private void hideMore() {
        isShowMore.set(false);
        startVerticalAnim(showMoreLayout, verticalanimDown, showMoreLayout.getHeight());
        startVerticalAnim(voiceLayout, verticalanimUp, -showMoreLayout.getHeight());
        startVerticalAnim(btnVoice, verticalanimUp, -showMoreLayout.getHeight());
        showMoreLayout.setVisibility(View.GONE);
    }

    private void showMore() {
        isShowMore.set(true);
        startVerticalAnim(showMoreLayout, verticalanimUp, showMoreLayout.getHeight());
        startVerticalAnim(voiceLayout, verticalanimUp, showMoreLayout.getHeight());
        startVerticalAnim(btnVoice, verticalanimUp, showMoreLayout.getHeight());
        showMoreLayout.setVisibility(View.VISIBLE);
    }

    private void showShareView() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.text_share));
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.text_share_title)));
    }

    private void initViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new TodayTodoFragment());
        fragments.add(new BookFragment());
        String title[] = new String[]{"今日一览", "小哈说书"};
        viewPager = (ViewPager) findViewById(R.id.mainViewPager);
        tabLayout = (TabLayout) findViewById(R.id.mainTab);
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
                return title[position];
            }
        });
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initView() {
        showMoreLayout = binding.appBarMain.showMoreLayout;
        voiceLayout = binding.appBarMain.voiceLayout;
        btnVoice = binding.appBarMain.btnVoice;
        editVoice = binding.appBarMain.editVoice;
        editVoice.setOnFocusChangeListener((v, b) -> {
            hideMore();
        });
    }

    private void startVerticalAnim(View view, int type, int height) {
        TranslateAnimation animation;
        if (type == verticalanimUp) {
            animation = new TranslateAnimation(0, 0, height, 0);
        } else {
            animation = new TranslateAnimation(0, 0, 0, height);
        }
        //设置动画的回弹效果
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(durationMillis);
        view.startAnimation(animation);
    }
}
