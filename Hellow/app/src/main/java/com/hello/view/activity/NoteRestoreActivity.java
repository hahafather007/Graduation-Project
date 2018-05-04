package com.hello.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.hello.R;
import com.hello.databinding.ActivityNoteRestoreBinding;
import com.hello.databinding.ItemRestoreNoteBinding;
import com.hello.model.data.NoteListData;
import com.hello.model.db.table.Note;
import com.hello.utils.ToastUtil;
import com.hello.viewmodel.NoteRestoreViewModel;

import java.util.List;

import javax.inject.Inject;

import static com.hello.common.Constants.EXTRA_ITEM;
import static com.hello.utils.ValidUtilKt.isListValid;

public class NoteRestoreActivity extends AppActivity {
    private ActivityNoteRestoreBinding binding;

    public static Intent intentOfNotes(Context context, List<Note> notes) {
        Intent intent = new Intent(context, NoteRestoreActivity.class);
        intent.putExtra(EXTRA_ITEM, new Gson().toJson(new NoteListData(notes)));
        return intent;
    }

    @Inject
    NoteRestoreViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_note_restore);
        binding.setActivity(this);
        binding.setViewModel(viewModel);

        binding.toolbar.setNavigationOnClickListener(__ -> onBackPressed());

        viewModel.initNotes(getIntent().getStringExtra(EXTRA_ITEM));
    }

    @Override
    protected void onDestroy() {
        viewModel.onCleared();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);

        finish();
    }

    public void onBindItem(ViewDataBinding binding, Object data, int position) {
        ((ItemRestoreNoteBinding) binding).setViewModel(viewModel);
    }

    public void startRestore() {
        if (!isListValid(viewModel.getClickedNotes())) {
            ToastUtil.showToast(this, R.string.text_choose_nothing);

            return;
        }

        setResult(RESULT_OK, new Intent().putExtra(EXTRA_ITEM,
                new Gson().toJson(new NoteListData(viewModel.getClickedNotes()))));

        finish();
    }
}
