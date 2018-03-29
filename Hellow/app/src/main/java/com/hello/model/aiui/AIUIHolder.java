package com.hello.model.aiui;

import android.annotation.SuppressLint;
import android.content.Context;

import com.annimon.stream.Optional;
import com.hello.common.Constants;
import com.hello.model.data.DescriptionData;
import com.hello.model.data.HelloTalkData;
import com.hello.model.data.TuLingSendData;
import com.hello.model.data.UserTalkData;
import com.hello.model.data.WeatherData;
import com.hello.model.pref.HelloPref;
import com.hello.model.service.TuLingService;
import com.hello.utils.AlarmUtil;
import com.hello.utils.CalendarUtil;
import com.hello.utils.DeviceIdUtil;
import com.hello.utils.Log;
import com.hello.utils.rx.Singles;
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
import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.hello.utils.MusicUtil.playMusic;
import static com.hello.utils.MusicUtil.stopMusic;
import static com.hello.utils.ValidUtilKt.isStrValid;

@Singleton
public class AIUIHolder {
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
    //用户说的话
    private String userMsg;

    //返回的结果
    public Subject<Object> aiuiResult = PublishSubject.create();
    //错误信息
    public Subject<Optional> error = PublishSubject.create();
    //音量大小变化回调
    public Subject<Integer> volume = PublishSubject.create();

    @Inject
    Context context;
    @Inject
    TuLingService tuLingService;

    @Inject
    AIUIHolder() {
    }

    @Inject
    void init() {
        initAIUIListener();

        agent = AIUIAgent.createAgent(context, getAIUIParams(), aiuiListener);

        wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);

        speech = SpeechSynthesizer.createSynthesizer(context, null);
        //获取语音发音人
        speech.setParameter(SpeechConstant.VOICE_NAME, HelloPref.INSTANCE.getTalkPeople());
        speech.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        speechListener = new SimpleSynthesizerListener() {
            @Override
            public void onCompleted(@Nullable SpeechError errorMsg) {
                if (errorMsg != null && isStrValid(errorMsg.getMessage())) {
                    error.onNext(Optional.empty());
                }
            }
        };
    }

    //外部调用该方法控制录音开始和结束
    public void startRecording() {
        stopMusic();
        speech.stopSpeaking();

        if (AIUIConstant.STATE_WORKING != status) {
            agent.sendMessage(wakeupMsg);
        }
        message = new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0,
                "sample_rate=16000,data_type=audio", null);
        agent.sendMessage(message);
    }

    public void stopRecording() {
        message = new AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, "", null);
        agent.sendMessage(message);
    }

    //外部调用该方法发送本文消息
    public void sendMessage(String msg) {
        stopMusic();
        speech.stopSpeaking();

        if (AIUIConstant.STATE_WORKING != status) {
            agent.sendMessage(wakeupMsg);
        }
        message = new AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0,
                "data_type=text", msg.getBytes());
        agent.sendMessage(message);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
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

    @SuppressWarnings({"MismatchedQueryAndUpdateOfStringBuilder", "ConstantConditions"})
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
                                userMsg = resultJson.getString("text");
                                aiuiResult.onNext(new UserTalkData(userMsg));

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
                //音频的状态
                case AIUIConstant.EVENT_VAD: {
                    if (event.arg1 == 1) {
                        Log.i("音量：" + event.arg2);
                        volume.onNext(event.arg2);
                    }
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

    @SuppressLint("SimpleDateFormat")
    //对结果进行解析
    private void analyzeResult(JSONObject resultJson) throws JSONException {
        int rc = resultJson.getInt("rc");
        if (rc == 0 || rc == 3) {//0表示语义理解成功，3表示业务失败但有信息返回
            try {
                String mTalkText = resultJson.getJSONObject("answer").getString("text");
                //按照类别选择不同方式
                switch (resultJson.getString("service")) {
                    case "joke": {
                        JSONArray array = resultJson.getJSONObject("data").getJSONArray("result");
                        JSONObject object = new JSONObject(array.getString(new Random()
                                .nextInt(array.length())));
                        mTalkText = object.getString("title") + "：\n";
                        mTalkText += object.getString("content");
                        DescriptionData data = new DescriptionData(object.getString("title"),
                                object.getString("content"));

                        aiuiResult.onNext(data);
                        speech.startSpeaking(mTalkText, speechListener);

                        break;
                    }
                    case "news": {
                        JSONArray array = resultJson.getJSONObject("data").getJSONArray("result");
                        String url = new JSONObject(array.getString(new Random()
                                .nextInt(array.length()))).getString("url");

                        aiuiResult.onNext(new HelloTalkData(mTalkText));
                        playMusic(url, () -> error.onNext(Optional.empty()));

                        break;
                    }
                    case "weather": {
                        //获取用户询问天气预报的具体时间
                        WeatherData weather = null;
                        JSONArray array = resultJson.getJSONObject("data").getJSONArray("result");
                        try {
                            String suggestData = new JSONObject(resultJson.getJSONArray("semantic")
                                    .getJSONObject(0).getJSONArray("slots")
                                    .getJSONObject(0).getString("normValue"))
                                    .getString("suggestDatetime");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                if (object.getString("date").equals(suggestData)) {
                                    weather = new WeatherData(object.getString("city"), suggestData,
                                            object.getString("tempRange"),
                                            object.getString("weather"),
                                            object.getString("wind"));

                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            JSONObject object = array.getJSONObject(0);
                            weather = new WeatherData(object.getString("city"),
                                    object.getString("date"), object.getString("tempRange"),
                                    object.getString("weather"), object.getString("wind"));
                        }
                        mTalkText = mTalkText.replaceFirst("℃", "度")
                                .replaceFirst(" ~ ", "至");

                        assert weather != null;
                        aiuiResult.onNext(weather);
                        speech.startSpeaking(mTalkText, speechListener);

                        break;
                    }
                    case "cookbook": {
                        String cookName = resultJson.getJSONArray("semantic").getJSONObject(0)
                                .getJSONArray("slots").getJSONObject(0)
                                .getString("value");
//                        CookResult cook = new Gson().fromJson(resultJson.toString(), CookResult.class);
//                        cook.getAnswer().setText(msg);

                        TuLingSendData data = new TuLingSendData(Constants.TULING_KEY, cookName,
                                null, DeviceIdUtil.getId(context));

                        tuLingService.getCook(data)
                                .compose(Singles.async())
                                .doOnSuccess(v -> {
                                    aiuiResult.onNext(v);
                                    speech.startSpeaking(v.getText(), speechListener);
                                })
                                .subscribe();

                        break;
                    }
                    case "radio": {
                        JSONArray array = resultJson.getJSONObject("data").getJSONArray("result");
                        JSONObject object = array.getJSONObject(0);
                        String url = object.getString("url");

                        aiuiResult.onNext(new HelloTalkData(mTalkText));
                        playMusic(url, () -> error.onNext(Optional.empty()));

                        break;
                    }
                    case "scheduleX": {
                        //state表示了提醒任务是否完成
                        String state = resultJson.getJSONObject("used_state")
                                .getString("state");
                        if ("reminderFinished".equals(state) || "clockFinished".equals(state)) {
                            JSONArray array = resultJson.getJSONArray("semantic")
                                    .getJSONObject(0).getJSONArray("slots");
                            String content;
                            String suggestTime;
                            if ("reminderFinished".equals(state)) {
                                content = array.getJSONObject(0).getString("value");
                                suggestTime = new JSONObject(array.getJSONObject(1)
                                        .getString("normValue")).getString("suggestDatetime");
                            } else {
                                content = array.getJSONObject(1).getString("value");
                                suggestTime = new JSONObject(array.getJSONObject(1)
                                        .getString("normValue")).getString("suggestDatetime");
                            }
                            Calendar time = Calendar.getInstance();
                            try {
                                LocalDateTime dateTime = LocalDateTime.parse(suggestTime.replace(" ", ""));
                                time.set(dateTime.getYear(), dateTime.getMonthOfYear() - 1,
                                        dateTime.getDayOfMonth(), dateTime.getHourOfDay(),
                                        dateTime.getMinuteOfHour(), dateTime.getSecondOfMinute());
                                if (mTalkText.contains("明天") &&
                                        dateTime.getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
                                    time.add(Calendar.DATE, 1);
                                }
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            }
                            if ("reminderFinished".equals(state)) {
                                CalendarUtil.addCalendarEvent(context, time, content);
                            } else {
                                AlarmUtil.addAlarmEvent(context, time, content);
                            }
                        }

                        aiuiResult.onNext(new HelloTalkData(mTalkText));
                        speech.startSpeaking(mTalkText, speechListener);

                        break;
                    }
                    case "drama": {
                        JSONArray array = resultJson.getJSONObject("data").getJSONArray("result");
                        JSONObject object = array.getJSONObject(new Random().nextInt(array.length()));
                        DescriptionData data = new DescriptionData(object.getString("album"),
                                object.getString("description"));

                        aiuiResult.onNext(data);
                        playMusic(object.getString("url"), () -> error.onNext(Optional.empty()));

                        break;
                    }
                    case "translation": {
                        String msg = resultJson.getJSONObject("data").getJSONArray("result")
                                .getJSONObject(0).getString("translated");

                        aiuiResult.onNext(new HelloTalkData(msg));
                        speech.startSpeaking(msg, speechListener);

                        break;
                    }
                    case "story": {
                        JSONArray array = resultJson.getJSONObject("data").getJSONArray("result");
                        JSONObject object = new JSONObject(array.getString(new Random()
                                .nextInt(array.length())));

                        mTalkText = "请听故事：" + object.getString("name");

                        aiuiResult.onNext(new HelloTalkData(mTalkText));
                        playMusic(object.getString("playUrl"), () -> error.onNext(Optional.empty()));

                        break;
                    }
                    default: {
                        aiuiResult.onNext(new HelloTalkData(mTalkText));
                        speech.startSpeaking(mTalkText, speechListener);

                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();

                useTuLing();
            }
        } else {//如果AIUI没有结果返回或者返回错误结果，则调用图灵机器人
            useTuLing();
        }
    }

    private void useTuLing() {
        if (userMsg.contains("你的") && userMsg.contains("照片")) {
            userMsg = "美女图片";
        }
        TuLingSendData data = new TuLingSendData(Constants.TULING_KEY, userMsg,
                null, DeviceIdUtil.getId(context));

        tuLingService.getResult(data)
                .compose(Singles.async())
                .doOnSuccess(v -> {
                    aiuiResult.onNext(v);
                    speech.startSpeaking(v.getText(), speechListener);
                })
                .subscribe();
    }
}
