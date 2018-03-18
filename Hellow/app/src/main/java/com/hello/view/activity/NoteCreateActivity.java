package com.hello.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hello.R;
import com.hello.databinding.ActivityNoteCreateBinding;
import com.hello.utils.DialogUtil;
import com.hello.utils.DimensionUtil;
import com.hello.utils.ToastUtil;
import com.hello.viewmodel.NoteCreateViewModel;

import javax.inject.Inject;

import static android.view.View.*;
import static com.hello.common.Constants.EXTRA_ID;
import static com.hello.common.Constants.EXTRA_TITLE;
import static com.hello.utils.ValidUtilKt.isStrValid;

public class NoteCreateActivity extends AppActivity {
    private ActivityNoteCreateBinding binding;
    //录音是否正在播放的状态
    private boolean recordPlaying;
    //是否正在进行录音状态
    private boolean recording;

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
        getMenuInflater().inflate(R.menu.note_create_menu, menu);
        String title = getIntent().getStringExtra(EXTRA_TITLE);

        if (isStrValid(title)) {
            setTitle(title);
            binding.btnPlay.setVisibility(GONE);
        } else {//如果是查看之前的note，则显示分享按钮，否者不显示
            menu.setGroupVisible(0, false);
            binding.btnPlay.setOnClickListener(__ -> startOrStopRecord());
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_share:
                showShareView();
                break;
            case R.id.nav_save:
                saveNote();
                break;
            case R.id.nav_play:
                if (!recordPlaying) {
                    item.setIcon(R.mipmap.ic_menu_pause);
                } else {
                    item.setIcon(R.mipmap.ic_menu_play);
                }
                recordPlaying = !recordPlaying;
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveNote() {
        EditText editText = new EditText(this);
        int padding = DimensionUtil.dp2px(this, 24);
        if (isStrValid(getIntent().getStringExtra(EXTRA_TITLE))) {
            editText.setText(viewModel.getNoteTitle());
        }
        editText.setHint(R.string.text_title_save_hint);

        DialogUtil.showViewDialog(this, R.string.text_title_save, editText,
                R.string.text_cancel, R.string.text_enter, null,
                (__, ___) -> {
                    if (editText.getText().toString().isEmpty()) {
                        ToastUtil.showToast(this, R.string.text_no_title);
                    } else {
                        if (isStrValid(getIntent().getStringExtra(EXTRA_TITLE))) {
                            viewModel.setNoteTitle(editText.getText().toString());
                            viewModel.saveNote();
                        } else {
                            viewModel.addNote(editText.getText().toString(), viewModel.getNoteText().get());
                        }
                    }
                });

        //让View与title对齐
        ((ViewGroup.MarginLayoutParams) editText.getLayoutParams()).setMargins(padding, 0, padding, 0);
    }

    private void showShareView() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, viewModel.getNoteText().get());
        intent.setType("text/plain");
        startActivity(intent);
    }

    private void addChangeListener() {
        viewModel.getError()
                .subscribe();

        viewModel.getDeleteOver()
                .subscribe();

        viewModel.getSaveOver()
                .subscribe(__ -> {
                    setTitle(viewModel.getNoteTitle());
                    ToastUtil.showToast(this, R.string.text_save_over);
                });
    }

    public void startOrStopRecord() {
        if (!recording) {
            viewModel.startRecord();
            binding.btnPlay.setText(R.string.text_stop_record);
        } else {
            DialogUtil.showDialog(this, R.string.text_stop_recording,
                    R.string.text_cancel, R.string.text_enter,
                    null, (__, ___) -> {
                        viewModel.stopRecord();
                        binding.btnPlay.setVisibility(GONE);
                        binding.toolbar.getMenu().setGroupVisible(0, true);
                    });
        }

        recording = !recording;
    }
}
