package com.hello.model.aiui;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.annimon.stream.Optional;
import com.hello.R;
import com.hello.utils.Log;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.speech.SynthesizerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

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
    SynthesizerListener speechListener;
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
    public Subject<Optional<String>> aiuiResult = PublishSubject.create();

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

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    //外部调用该方法控制录音开始和结束
    public void startOrStopRecording() {
        mediaPlayer.stop();

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
                                aiuiResult.onNext(Optional.of(resultStr));
                                analyzeResult(new JSONObject(resultStr));
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

    private String talkText;

    //对结果进行解析
    private void analyzeResult(JSONObject resultJson) throws JSONException {
        if (resultJson.getInt("rc") == 0) {//0表示语义理解成功
            String mtalkText = resultJson.getJSONObject("answer").getString("text");

            //按照类别选择不同方式
            try {
                switch (resultJson.getString("service")) {
                    case "joke": {
                        mtalkText += new JSONObject(resultJson.getJSONObject("data")
                                .getJSONArray("result").getString(0))
                                .getString("content");
                        talkText = mtalkText;
                        break;
                    }
                    case "news": {
                        musicUrl = new JSONObject(resultJson.getJSONObject("data")
                                .getJSONArray("result").getString(0))
                                .getString("url");
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(musicUrl);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        break;
                    }
                    default: {
                        talkText = mtalkText;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            speech.startSpeaking(talkText, null);
        } else {
            speech.startSpeaking(context.getString(R.string.aiui_no_result), null);
        }
    }
}
