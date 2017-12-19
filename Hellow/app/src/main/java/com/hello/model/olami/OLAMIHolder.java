package com.hello.model.olami;

import android.content.Context;

import com.annimon.stream.Optional;
import com.hello.utils.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

import ai.olami.android.IRecorderSpeechRecognizerListener;
import ai.olami.android.RecorderSpeechRecognizer;
import ai.olami.android.tts.ITtsPlayerListener;
import ai.olami.android.tts.TtsPlayer;
import ai.olami.cloudService.APIConfiguration;
import ai.olami.cloudService.APIResponse;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.hello.common.Constants.OLAMI_KEY;
import static com.hello.common.Constants.OLAMI_SECRET;

@Singleton
public class OLAMIHolder {
    //语音合成播放器
    private TtsPlayer ttsPlayer;
    //麦克风语音识别器对象
    private RecorderSpeechRecognizer recognizer;
    //是否正在录音的标识符
    private boolean recording;
    //语音识别结果
    private APIResponse apiResponse;

    //说话音量大小回调
    public Subject<Optional<Integer>> volumeChange = PublishSubject.create();
    //识别成功回调
    public Subject<Optional> voiceCompleted = PublishSubject.create();

    @Inject
    Context context;

    @Inject
    OLAMIHolder() {
    }

    @Inject
    void init() {
        //语音识别的配置
        APIConfiguration config = new APIConfiguration(OLAMI_KEY, OLAMI_SECRET,
                APIConfiguration.LOCALIZE_OPTION_SIMPLIFIED_CHINESE);

        ttsPlayer = new TtsPlayer(context, new mPlayerListener());

        recognizer =
                RecorderSpeechRecognizer.create(new mRecognizerListener(), config);
    }

    //外部调用该方法用来开始或结束录音
    public void startOrStopRecorder() {
        if (recording) {
            recognizer.stop();
        } else {
            try {

                recognizer.start();
                recording = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public APIResponse getApiResponse() {
        return apiResponse;
    }

    //语音识别Listener
    private class mRecognizerListener implements IRecorderSpeechRecognizerListener {
        @Override
        public void onRecordStateChange(RecorderSpeechRecognizer.RecordState state) {
            Log.i("录音状态：" + state.toString());
            if (state == RecorderSpeechRecognizer.RecordState.STOPPED ||
                    state == RecorderSpeechRecognizer.RecordState.ERROR) {
                recording = false;
            }
        }

        @Override
        public void onRecognizeStateChange(RecorderSpeechRecognizer.RecognizeState state) {
            Log.i("识别器状态：" + state.toString());
            if (state == RecorderSpeechRecognizer.RecognizeState.COMPLETED) {
                voiceCompleted.onNext(Optional.empty());
                if (apiResponse.getData().getNLIResults() != null) {
                    ttsPlayer.playText(apiResponse.getData()
                            .getNLIResults()[0].getDescObject().getReplyAnswer(), true);
                }
            }
        }

        @Override
        public void onRecognizeResultChange(APIResponse response) {
            Log.i("识别结果：" + response.toString());
            apiResponse = response;
        }

        @Override
        public void onRecordVolumeChange(int volumeValue) {
            Log.i("说话音量：" + volumeValue);
            volumeChange.onNext(Optional.of(volumeValue));
        }

        @Override
        public void onServerError(APIResponse response) {
            Log.e("服务器错误：" + response.getErrorMessage());
        }

        @Override
        public void onError(RecorderSpeechRecognizer.Error error) {
            Log.e("发生错误：" + error.toString());
        }

        @Override
        public void onException(Exception e) {
            e.printStackTrace();
        }
    }

    //语音合成Listener
    private class mPlayerListener implements ITtsPlayerListener {
        @Override
        public void onPlayEnd() {

        }

        @Override
        public void onStop() {

        }

        @Override
        public void onPlayingTTS(String s) {

        }
    }
}
