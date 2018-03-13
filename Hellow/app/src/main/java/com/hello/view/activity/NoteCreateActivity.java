package com.hello.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.hello.R;
import com.hello.databinding.ActivityNoteCreateBinding;
import com.hello.utils.DialogUtil;
import com.hello.viewmodel.NoteCreateViewModel;

import javax.inject.Inject;

import static com.hello.common.Constants.EXTRA_ID;
import static com.hello.common.Constants.EXTRA_TITLE;
import static com.hello.utils.ValidUtilKt.isStrValid;

public class NoteCreateActivity extends AppActivity {
    private ActivityNoteCreateBinding binding;

    @Inject
    NoteCreateViewModel viewModel;

    public static Intent intentOfNote(Context context, long id, String title) {
        Intent intent = new Intent(context, NoteCreateActivity.class);
        intent.putExtra(EXTRA_ID, id);
        intent.putExtra(EXTRA_TITLE, title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_note_create);
        binding.setViewModel(viewModel);
        viewModel.initNote(getIntent().getLongExtra(EXTRA_ID, -1));

        addChangeListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (isStrValid(title)) {
            setTitle(title);
            //如果是查看之前的note，则显示分享按钮
            getMenuInflater().inflate(R.menu.note_create_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_share:
                showShareView();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showShareView() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.text_share));
        intent.setType("text/plain");
        startActivity(intent);
    }

    private void addChangeListener() {
        viewModel.getError()
                .subscribe();

        viewModel.getDeleteOver()
                .subscribe();
    }

    public void startRecord() {
        viewModel.startRecord();
    }

    public void stopRecord() {
        DialogUtil.showDialog(this, R.string.text_stop_recording,
                R.string.text_cancel, R.string.text_enter,
                null, (__, ___) -> viewModel.stopRecord());
    }
}
