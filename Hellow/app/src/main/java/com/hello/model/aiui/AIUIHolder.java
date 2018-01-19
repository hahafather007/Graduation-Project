package com.hello.model.aiui;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.annimon.stream.Optional;
import com.google.gson.Gson;
import com.hello.R;
import com.hello.model.data.CookResult;
import com.hello.model.data.HelloTalkData;
import com.hello.model.data.UserTalkData;
import com.hello.model.data.Weather;
import com.hello.utils.Log;
import com.hello.widget.listener.SimpleSynthesizerListener;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.hello.common.SpeechPeople.XIAO_QI;

@Singleton
public class AIUIHolder {
    //播放在线音乐文件
    private MediaPlayer mediaPlayer;
    //在线音乐url
    private String musicUrl;
    //语音合成器
    private SpeechSynthesizer speech;
    //语音合成监听器
    private SynthesizerListener speechListener;
    //AIUI返回结果监听器
    private AIUIListener aiuiListener;
    //发送给AIUI的消息
    private AIUIMessage message;
    //唤醒AIUI的消息
    private AIUIMessage wakeupMsg;
    //AIUI对象
    private AIUIAgent agent;
    //用来判断录音的状态
    private boolean recording;
    //当前的状态
    private int status;

    //返回的结果
    public Subject<Optional<Object>> aiuiResult = PublishSubject.create();
    //错误信息
    public Subject<Optional> error = PublishSubject.create();

    @Inject
    Context context;

    @Inject
    AIUIHolder() {

    }

    @Inject
    void init() {
        initAIUIListener();

        agent = AIUIAgent.createAgent(context, getAIUIParams(), aiuiListener);

        wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);

        speech = SpeechSynthesizer.createSynthesizer(context, null);
        speech.setParameter(SpeechConstant.VOICE_NAME, XIAO_QI);
        speech.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        speechListener = new SimpleSynthesizerListener() {
            @Override
            public void onCompleted(@Nullable SpeechError errorMsg) {
                if (errorMsg != null && !errorMsg.getMessage().isEmpty()) {
                    error.onNext(Optional.empty());
                }
            }
        };

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    //外部调用该方法控制录音开始和结束
    public void startOrStopRecording() {
        mediaPlayer.stop();
        speech.stopSpeaking();

        if (!recording) {
            if (AIUIConstant.STATE_WORKING != status) {
                agent.sendMessage(wakeupMsg);
            }
            message = new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0,
                    "sample_rate=16000,data_type=audio", null);
            agent.sendMessage(message);
        } else {
            message = new AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, "", null);
            agent.sendMessage(message);
        }
    }

    //外部调用该方法发送本文消息
    public void sendMessage(String msg) {
        mediaPlayer.stop();
        speech.stopSpeaking();

        if (AIUIConstant.STATE_WORKING != status) {
            agent.sendMessage(wakeupMsg);
        }
        message = new AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0,
                "data_type=text", msg.getBytes());
        agent.sendMessage(message);
    }

    private String getAIUIParams() {
        String params = "";
        try {
            InputStream ins = context.getAssets().open("cfg/aiui_phone.cfg");
            byte[] buffer = new byte[ins.available()];
            ins.read(buffer);
            ins.close();
            params = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return params;
    }

    private void initAIUIListener() {
        StringBuffer mNlpText = new StringBuffer();

        aiuiListener = event -> {
            switch (event.eventType) {
                //唤醒事件
                case AIUIConstant.EVENT_WAKEUP: {
                    Log.i("被唤醒！");
                    break;
                }
                //结果事件（包含听写，语义，离线语法结果）
                case AIUIConstant.EVENT_RESULT: {
                    try {
                        JSONObject bizParamJson = new JSONObject(event.info);
                        JSONObject data = bizParamJson.getJSONArray("data").getJSONObject(0);
                        JSONObject params = data.getJSONObject("params");
                        JSONObject content = data.getJSONArray("content").getJSONObject(0);

                        if (content.has("cnt_id")) {
                            String cnt_id = content.getString("cnt_id");
                            JSONObject cntJson = new JSONObject(new String(
                                    event.data.getByteArray(cnt_id), "utf-8"));

                            mNlpText.append("\n");
                            mNlpText.append(cntJson.toString());

                            String sub = params.optString("sub");
                            if ("nlp".equals(sub)) {
                                // 解析得到语义结果
                                String resultStr = cntJson.optString("intent");
                                Log.i("结果：" + resultStr);

                                JSONObject resultJson = new JSONObject(resultStr);
                                aiuiResult.onNext(Optional.of(new UserTalkData(
                                        resultJson.getString("text"))));
                                analyzeResult(resultJson);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        mNlpText.append("\n");
                        mNlpText.append(e.getLocalizedMessage());
                    }

                    mNlpText.append("\n");

                    break;
                }
                //休眠事件
                case AIUIConstant.EVENT_SLEEP: {
                    Log.i("休眠！");
                    break;
                }
                //错误事件
                case AIUIConstant.EVENT_ERROR: {
                    Log.e("错误：" + event.info);
                    error.onNext(Optional.empty());
                    break;
                }
                //状态时间
                case AIUIConstant.EVENT_STATE: {
                    status = event.arg1;
                    break;
                }
                //开始录音
                case AIUIConstant.EVENT_START_RECORD: {
                    recording = true;
                    break;
                }
                //结束录音
                case AIUIConstant.EVENT_STOP_RECORD: {
                    recording = false;
                    break;
                }
            }
        };
    }

    //对结果进行解析
    private void analyzeResult(JSONObject resultJson) throws JSONException {
        if (resultJson.getInt("rc") == 0) {//0表示语义理解成功
            String mTalkText = resultJson.getJSONObject("answer").getString("text");

            //按照类别选择不同方式
            try {
                switch (resultJson.getString("service")) {
                    case "joke": {
                        JSONArray array = resultJson.getJSONObject("data").getJSONArray("result");
                        JSONObject object = new JSONObject(array.getString(new Random()
                                .nextInt(array.length())));
                        mTalkText = object.getString("title") + "：\n";
                        mTalkText += object.getString("content");
                        aiuiResult.onNext(Optional.of(new HelloTalkData(mTalkText)));
                        speech.startSpeaking(mTalkText, speechListener);
                        break;
                    }
                    case "news": {
                        aiuiResult.onNext(Optional.of(new HelloTalkData(mTalkText)));
                        JSONArray array = resultJson.getJSONObject("data").getJSONArray("result");
                        musicUrl = new JSONObject(array.getString(new Random()
                                .nextInt(array.length()))).getString("url");
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(musicUrl);
                        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                            error.onNext(Optional.empty());
                            return false;
                        });
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        break;
                    }
                    case "weather": {
                        //获取用户询问天气预报的具体时间
                        Weather weather = null;
                        JSONArray array = resultJson.getJSONObject("data").getJSONArray("result");
                        try {
                            String suggestData = new JSONObject(resultJson.getJSONArray("semantic")
                                    .getJSONObject(0).getJSONArray("slots")
                                    .getJSONObject(0).getString("normValue"))
                                    .getString("suggestDatetime");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                if (object.getString("date").equals(suggestData)) {
                                    weather = new Weather(object.getString("city"), suggestData,
                                            object.getString("tempRange"),
                                            object.getString("weather"),
                                            object.getString("wind"));
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            JSONObject object = array.getJSONObject(0);
                            weather = new Weather(object.getString("city"),
                                    object.getString("date"), object.getString("tempRange"),
                                    object.getString("weather"), object.getString("wind"));
                        }
                        mTalkText = mTalkText.replaceFirst("℃", "度")
                                .replaceFirst(" ~ ", "至");
                        Log.i(weather);
                        aiuiResult.onNext(Optional.of(weather));
                        speech.startSpeaking(mTalkText, speechListener);
                        break;
                    }
                    case "cookbook": {
                        aiuiResult.onNext(Optional.of(new Gson().fromJson(resultJson.toString(),
                                CookResult.class)));
                        speech.startSpeaking(mTalkText, speechListener);
                        break;
                    }
                    default: {
                        aiuiResult.onNext(Optional.of(new HelloTalkData(mTalkText)));
                        speech.startSpeaking(mTalkText, speechListener);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            aiuiResult.onNext(Optional.of(new HelloTalkData(context.getString(R.string.aiui_no_result))));
            speech.startSpeaking(context.getString(R.string.aiui_no_result), speechListener);
        }
    }
}
