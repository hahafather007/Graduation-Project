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
import com.hello.model.db.table.Note;
import com.hello.model.pref.HelloPref;
import com.hello.utils.DialogUtil;
import com.hello.utils.rx.Observables;
import com.hello.utils.rx.RxLifeCycle;
import com.hello.view.activity.NoteCreateActivity;
import com.hello.viewmodel.NoteViewModel;

import javax.inject.Inject;

import static com.hello.utils.IntentUtil.setupActivity;
import static com.hello.utils.ToastUtil.showToast;

public class NoteFragment extends AppFragment {
    private FragmentNoteBinding binding;
    private OnNewsCreateListener listener;

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

        if (getActivity() instanceof OnNewsCreateListener) {
            listener = ((OnNewsCreateListener) getActivity());
        }

        addChangeListener();
    }

    @Override
    public void onDestroy() {
        viewModel.onCleared();

        super.onDestroy();
    }

    private void addChangeListener() {
        viewModel.deleteOver
                .compose(RxLifeCycle.with(this))
                .doOnNext(__ -> showToast(getContext(), R.string.text_delete_over))
                .subscribe();
    }

    public void onBindItem(ViewDataBinding binding, Object data, int position) {
        ItemVoiceNoteBinding noteBinding = ((ItemVoiceNoteBinding) binding);
        noteBinding.setFragment(this);
    }

    public void openNote(long id, String title) {
        startActivity(NoteCreateActivity.intentOfNote(getContext(), id, title));
    }

    @SuppressWarnings("ConstantConditions")
    public void readyDelete(Note note) {
        DialogUtil.showDialog(getContext(), R.string.text_ask_delete,
                R.string.text_cancel, R.string.text_enter, null,
                (__, ___) -> viewModel.deleteNote(note));
    }

    public void newsNote() {
        //如果登录状态，直接打开界面，否则弹出登录界面
        if (HelloPref.INSTANCE.isLogin()) {
            if (listener != null) {
                listener.onOpenCreate();
            }

            setupActivity(getContext(), NoteCreateActivity.class);
        } else {
            DialogUtil.showDialog(getContext(), R.string.text_should_login,
                    R.string.text_cancel, R.string.text_enter, null,
                    (__, ___) -> {
                        if (listener != null) {
                            listener.onOpenLogin();
                        }
                    });
        }
    }

    public interface OnNewsCreateListener {
        void onOpenCreate();

        void onOpenLogin();
    }
}
