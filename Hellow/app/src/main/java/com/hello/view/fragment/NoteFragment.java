package com.hello.view.fragment;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hello.R;
import com.hello.databinding.FragmentNoteBinding;
import com.hello.databinding.ItemVoiceNoteBinding;
import com.hello.view.activity.NoteCreateActivity;
import com.hello.viewmodel.NoteViewModel;

import javax.inject.Inject;

import static com.hello.utils.IntentUtil.setupActivity;

public class NoteFragment extends AppFragment {
    private FragmentNoteBinding binding;

    @Inject
    NoteViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = DataBindingUtil.bind(view);
        binding.setFragment(this);
        binding.setViewModel(viewModel);
    }

    public void onBindItem(ViewDataBinding binding, Object data, int position) {
        ItemVoiceNoteBinding noteBinding = ((ItemVoiceNoteBinding) binding);
        noteBinding.setFragment(this);
    }

    public void openNote(long id, String title) {
        startActivity(NoteCreateActivity.intentOfNote(getContext(), id, title));
    }

    //长按item弹出删除选项
    public void readyDelete(long id) {

    }

    public void newsNote() {
        setupActivity(getContext(), NoteCreateActivity.class);
    }
}
