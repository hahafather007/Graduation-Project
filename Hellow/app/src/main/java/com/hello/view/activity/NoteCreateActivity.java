package com.hello.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hello.R;
import com.hello.databinding.ActivityNoteCreateBinding;
import com.hello.utils.DialogUtil;
import com.hello.utils.DimensionUtil;
import com.hello.utils.ToastUtil;
import com.hello.utils.ValidUtilKt;
import com.hello.utils.rx.RxField;
import com.hello.utils.rx.RxLifeCycle;
import com.hello.viewmodel.NoteCreateViewModel;
import com.hello.widget.listener.SimpleTextWatcher;

import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

import static com.hello.common.Constants.EXTRA_ID;
import static com.hello.common.Constants.EXTRA_TITLE;
import static com.hello.utils.ValidUtilKt.isStrValid;

public class NoteCreateActivity extends AppActivity {
    private ActivityNoteCreateBinding binding;
    //录音是否正在播放的状态
    private boolean recordPlaying;
    private boolean hasSave = true;

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
        binding.setActivity(this);
        viewModel.initNote(getIntent().getLongExtra(EXTRA_ID, -1));

        addChangeListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_create_menu, menu);
        String title = getIntent().getStringExtra(EXTRA_TITLE);

        if (isStrValid(title)) {
            setTitle(title);
            menu.getItem(3).setVisible(false);
        } else {//如果是查看之前的note，则显示分享按钮，否者不显示
            for (int i = 0; i < 3; i++) {
                menu.getItem(i).setVisible(false);
            }
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
            case R.id.nav_voice:
                item.setVisible(false);

                startOrStopRecord();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        viewModel.onCleared();
    }

    @Override
    public void onBackPressed() {
        if (viewModel.getRecording().get()) {
            DialogUtil.showDialog(this, R.string.text_diaolg_stop_recording,
                    R.string.text_cancel, R.string.text_enter,
                    null, (__, ___) -> {
                        viewModel.stopRecord();
                        hasSave = true;

                        super.onBackPressed();
                    });
        } else {
            //如果修改的内容保存了就直接退出，否则提醒
            if (hasSave) {
                super.onBackPressed();
            } else {
                DialogUtil.showDialog(this, R.string.text_give_up_save,
                        R.string.text_cancel, R.string.text_enter,
                        null, (__, ___) -> super.onBackPressed());
            }
        }
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
                            String content = viewModel.getNoteText().get();
                            viewModel.addNote(editText.getText().toString(), content != null ? content : "");
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
        viewModel.getSaveOver()
                .compose(RxLifeCycle.resumed(this))
                .doOnNext(__ -> {
                    setTitle(viewModel.getNoteTitle());
                    ToastUtil.showToast(this, R.string.text_save_over);
                    hasSave = true;
                })
                .subscribe();

        //实时更新音频图的振幅
        RxField.of(viewModel.getVolume())
                .skip(0)
                .compose(RxLifeCycle.resumed(this))
                .doOnNext(v -> binding.waveView.updateAmplitude(v / 30f))
                .subscribe();

        binding.editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(@Nullable Editable s) {
                hasSave = false;
            }
        });
    }

    public void startOrStopRecord() {
        if (!viewModel.getRecording().get()) {
            viewModel.startRecord();
        } else {
            DialogUtil.showDialog(this, R.string.text_stop_recording,
                    R.string.text_cancel, R.string.text_enter,
                    null, (__, ___) -> {
                        viewModel.stopRecord();
                        for (int i = 0; i < 3; i++) {
                            binding.toolbar.getMenu().getItem(i).setVisible(true);
                        }

                        if (!isStrValid(viewModel.getNoteText().get())) {
                            ToastUtil.showToast(this, R.string.text_say_nothing);
                        }
                    });
        }
    }
}
