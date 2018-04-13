package com.hello.model.aiui;

import android.annotation.SuppressLint;
import android.content.Context;

import com.annimon.stream.Optional;
import com.hello.R;
import com.hello.common.Constants;
import com.hello.common.RxController;
import com.hello.model.data.AppOpenData;
import com.hello.model.data.DescriptionData;
import com.hello.model.data.HelloTalkData;
import com.hello.model.data.IdiomData;
import com.hello.model.data.KugoMusicData;
import com.hello.model.data.KugoSearchData;
import com.hello.model.data.LightSwitchData;
import com.hello.model.data.MusicData;
import com.hello.model.data.MusicState;
import com.hello.model.data.PhoneData;
import com.hello.model.data.PhoneMsgData;
import com.hello.model.data.PoetryData;
import com.hello.model.data.SystemMsgData;
import com.hello.model.data.TuLingData;
import com.hello.model.data.TuLingSendData;
import com.hello.model.data.UserTalkData;
import com.hello.model.data.WeatherData;
import com.hello.model.db.SpeakDataHolder;
import com.hello.model.location.LocationHolder;
import com.hello.model.pref.HelloPref;
import com.hello.model.service.KugoMusicService;
import com.hello.model.service.KugoSearchService;
import com.hello.model.service.TuLingService;
import com.hello.utils.AlarmUtil;
import com.hello.utils.CalendarUtil;
import com.hello.utils.Log;
import com.hello.utils.rx.Completables;
import com.hello.utils.rx.Observables;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static com.hello.common.SpeechPeople.XIAO_YAN;
import static com.hello.model.aiui.AIUIHolder.LocUse.TULING;
import static com.hello.utils.MusicUtil.stopMusic;
import static com.hello.utils.ValidUtilKt.isListValid;
import static com.hello.utils.ValidUtilKt.isStrValid;

@Singleton
public class AIUIHolder extends RxController {
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
    //当前的状态
    private int status;
    //用户说的话
    private String userMsg;
    //接收短信的联系人
    private String msgPeople;
    //发送短信内容
    private String msgDetail;
    //获取地址之后的用途
    private LocUse locUse;

    //返回的结果
    public Subject<Object> aiuiResult = PublishSubject.create();
    //错误信息
    public Subject<Optional> error = PublishSubject.create();
    //音量大小变化回调
    public Subject<Integer> volume = PublishSubject.create();
    //音乐数据
    public Subject<MusicData> music = PublishSubject.create();
    //合成语音说完
    public Subject<Optional> speakOver = PublishSubject.create();
    //导航的终点坐标
    public Subject<String> location = PublishSubject.create();

    @Inject
    Context context;
    @Inject
    TuLingService tuLingService;
    @Inject
    KugoSearchService searchService;
    @Inject
    KugoMusicService musicService;
    @Inject
    LocationHolder locationHolder;
    @Inject
    SpeakDataHolder speakDataHolder;

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

                speakOver.onNext(Optional.empty());
            }
        };

        sendLocationInfo();
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
        message = new AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0,
                "sample_rate=16000,data_type=audio", null);
        agent.sendMessage(message);
    }

    //外部调用该方法说出指定话语
    public void speakText(String text) {
        speech.startSpeaking(text, speechListener);
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

                                if (msgDetail == null && msgPeople == null) {
                                    aiuiResult.onNext(new UserTalkData(userMsg));
                                }

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
                //状态事件
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
                    break;
                }
                //结束录音
                case AIUIConstant.EVENT_STOP_RECORD: {
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
                String mTalkText;

                try {
                    mTalkText = resultJson.getJSONObject("answer").getString("text");
                } catch (JSONException e) {
                    mTalkText = resultJson.getString("text");
                }

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
                        JSONObject object = new JSONObject(array.getString(new Random()
                                .nextInt(array.length())));

                        aiuiResult.onNext(new HelloTalkData(mTalkText));
                        music.onNext(new MusicData(object.getString("url"),
                                object.getString("imgUrl"), object.getString("title"), MusicState.ON));

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

                        tuLingService.getCook(getTuLingSendData(cookName))
                                .compose(Singles.async())
                                .compose(Singles.disposable(compositeDisposable))
                                .doOnSuccess(v -> {
                                    aiuiResult.onNext(v);
                                    speech.startSpeaking(v.getText(), speechListener);
                                })
                                .doOnError(__ -> error.onNext(Optional.empty()))
                                .subscribe();

                        break;
                    }
                    case "radio": {
                        JSONArray array = resultJson.getJSONObject("data").getJSONArray("result");
                        JSONObject object = array.getJSONObject(0);

                        aiuiResult.onNext(new HelloTalkData(mTalkText));
                        music.onNext(new MusicData(object.getString("url"),
                                object.getString("img"), object.getString("name"), MusicState.ON));

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
                    case "crossTalk":
                    case "health":
                    case "drama": {
                        JSONArray array = resultJson.getJSONObject("data").getJSONArray("result");
                        JSONObject object = array.getJSONObject(new Random().nextInt(array.length()));
                        DescriptionData data = new DescriptionData(object.getString("album"),
                                object.getString("description"));

                        aiuiResult.onNext(data);
                        music.onNext(new MusicData(object.getString("url"),
                                null, object.getString("name"), MusicState.ON));

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
                        music.onNext(new MusicData(object.getString("playUrl"),
                                null, object.getString("name"), MusicState.ON));

                        break;
                    }
                    //成语含义和成语接龙
                    case "idiom": {
                        String intent = resultJson.getJSONArray("semantic")
                                .getJSONObject(0).getString("intent");

                        //SOLITAIRE表示的是成语接龙的情况
                        if ("SOLITAIRE".equals(intent)) {
                            aiuiResult.onNext(new HelloTalkData(mTalkText));
                            speech.startSpeaking(mTalkText, speechListener);
                        } else {//成语含义查询
                            JSONObject object = resultJson.getJSONObject("data")
                                    .getJSONArray("result").getJSONObject(0);

                            aiuiResult.onNext(new IdiomData(object.getString("name"),
                                    object.getString("interpretation"), object.getString("source")));
                            speech.startSpeaking(object.getString("interpretation"), speechListener);
                        }

                        break;
                    }
                    case "musicX": {
                        String state = resultJson.getJSONObject("used_state")
                                .getString("state");
                        //判断是播放音乐还是停止音乐
                        if (!"paused".equals(state)) {
                            String name;

                            try {
                                JSONArray array = resultJson.getJSONArray("semantic")
                                        .getJSONObject(0).getJSONArray("slots");

                                name = array.getJSONObject(0).getString("value") + "-";
                                name += array.getJSONObject(1).getString("value");
                            } catch (Exception e) {
                                e.printStackTrace();

                                name = mTalkText.replaceFirst("听下", "")
                                        .replaceFirst("请欣赏", "")
                                        .replaceFirst("演唱", "")
                                        .replaceFirst("的", "-");
                                name = name.substring(0, name.length() - 1);
                            }

                            Log.i(name);

                            final String taklText = mTalkText;

                            searchService.getMusicList(1, name)
                                    .map(KugoSearchData::getData)
                                    .flatMap(v -> {
                                        if (isListValid(v.getLists())) {
                                            //noinspection ConstantConditions
                                            return musicService.getMusic(v.getLists().get(0).getHash());
                                        } else {
                                            return Single.error(new Exception("没有找到歌曲"));
                                        }
                                    })
                                    .compose(Singles.async())
                                    .compose(Singles.disposable(compositeDisposable))
                                    .map(KugoMusicData::getData)
                                    .doOnSuccess(v -> {
                                        aiuiResult.onNext(new HelloTalkData(taklText));

                                        speech.startSpeaking(taklText, speechListener);

                                        music.onNext(new MusicData(v.getUrl(), v.getImg(), v.getName(), MusicState.ON));
                                    })
                                    .doOnError(__ -> error.onNext(Optional.empty()))
                                    .subscribe();
                        } else {
                            aiuiResult.onNext(new HelloTalkData("好的"));

                            speech.startSpeaking("好的", speechListener);

                            music.onNext(new MusicData(null, null, null, MusicState.OFF));
                        }

                        break;
                    }
                    case "telephone": {
                        //正则表达式用来匹配有效电话号码
                        Pattern pattern = Pattern.compile("1[35789]\\d{9}");
                        Matcher matcher = pattern.matcher(mTalkText);

                        String phoneNum = null;

                        while (matcher.find()) {
                            phoneNum = matcher.group();
                        }

                        if (phoneNum == null) {
                            aiuiResult.onNext(new HelloTalkData("未找到该联系人，请确认联系人"));
                            speech.startSpeaking("未找到该联系人，请确认联系人", speechListener);
                        } else if (msgPeople != null && msgDetail != null) {//这两个参数不为空表示是发短信功能间接调用
                            aiuiResult.onNext(new HelloTalkData("请确认短信内容"));
                            aiuiResult.onNext(new PhoneMsgData(phoneNum, msgDetail));
                            speech.startSpeaking("请确认短信内容", speechListener);

                            msgPeople = null;
                            msgDetail = null;
                        } else {
                            aiuiResult.onNext(new HelloTalkData(context.getString(R.string.text_calling)));
                            aiuiResult.onNext(new PhoneData(phoneNum));
                            speech.startSpeaking(context.getString(R.string.text_calling), speechListener);
                        }

                        break;
                    }
                    case "poetry": {
                        JSONObject object = resultJson.getJSONObject("data")
                                .getJSONArray("result").getJSONObject(0);

                        String author = object.getString("author");
                        String dynasty = object.getString("dynasty");
                        String content = object.getString("showContent");
                        String title = object.getString("title");

                        aiuiResult.onNext(new PoetryData(author, dynasty, content, title));
                        speech.startSpeaking(content, speechListener);

                        break;
                    }
                    //自定义导航技能
                    case "HELLOASSIS.navigation": {
                        JSONObject object = resultJson.getJSONArray("semantic")
                                .getJSONObject(0).getJSONArray("slots").getJSONObject(0);

                        String loc = object.getString("value");

                        location.onNext(loc);

                        aiuiResult.onNext(new HelloTalkData("正在为您导航"));
                        speech.startSpeaking("正在为您导航", speechListener);

                        break;
                    }
                    //自定义的短信发送技能
                    case "HELLOASSIS.message_send": {
                        JSONArray array = resultJson.getJSONArray("semantic")
                                .getJSONObject(0).getJSONArray("slots");

                        msgPeople = array.getJSONObject(0).getString("value");
                        msgDetail = array.getJSONObject(1).getString("value");

                        sendMessage("给" + msgPeople + "打电话");

                        break;
                    }
                    //自定义手电筒技能
                    case "HELLOASSIS.CameraLight": {
                        JSONObject object = resultJson.getJSONArray("semantic")
                                .getJSONObject(0).getJSONArray("slots").getJSONObject(0);

                        if ("on".equals(object.getString("normValue"))) {
                            aiuiResult.onNext(new HelloTalkData("已开启"));
                            aiuiResult.onNext(new LightSwitchData(LightSwitchData.State.ON));
                            speech.startSpeaking("已开启", speechListener);
                        } else {
                            aiuiResult.onNext(new HelloTalkData("已关闭"));
                            aiuiResult.onNext(new LightSwitchData(LightSwitchData.State.OFF));
                            speech.startSpeaking("已关闭", speechListener);
                        }

                        break;
                    }
                    //自定义打开应用技能
                    case "HELLOASSIS.open_app": {
                        String appName = resultJson.getJSONArray("semantic")
                                .getJSONObject(0).getJSONArray("slots")
                                .getJSONObject(0).getString("normValue");

                        aiuiResult.onNext(new HelloTalkData("已为你打开" + appName));
                        aiuiResult.onNext(new AppOpenData(appName));
                        speech.startSpeaking("已为你打开" + appName, speechListener);

                        break;
                    }
                    //自定义周围的东西技能
                    case "HELLOASSIS.nearby_thing": {
                        JSONArray array = resultJson.getJSONArray("semantic")
                                .getJSONObject(0).getJSONArray("slots");

                        String thing = array.getJSONObject(0).getString("value");

                        aiuiResult.onNext(new TuLingData(-1, "已为你找到附近的" + thing,
                                Constants.MEI_TUAN.replace("city", "")
                                        .replace("thing", thing)));
                        speech.startSpeaking("已为你找到附近的" + userMsg, speechListener);

                        break;
                    }
                    //自定义朗读短信内容
                    case "HELLOASSIS.speak_message": {
                        aiuiResult.onNext(new SystemMsgData("", "", ""));

                        break;
                    }
                    default: {
                        aiuiResult.onNext(new HelloTalkData(mTalkText));
                        speech.startSpeaking(mTalkText, speechListener);

                        break;
                    }
                }

                saveTalkData(userMsg, mTalkText);
            } catch (JSONException e) {
                e.printStackTrace();

                useTuLing();
            }
        } else

        {//如果AIUI没有结果返回或者返回错误结果，则调用图灵机器人
            useTuLing();
        }

    }

    private void useTuLing() {
        if (userMsg.contains("你的") && (userMsg.contains("照片") || userMsg.contains("自拍"))) {
            if (HelloPref.INSTANCE.getTalkPeople().equals(XIAO_YAN)) {
                userMsg = "美女图片";
            } else {
                userMsg = "帅哥图片";
            }
        }

        tuLingService.getResult(getTuLingSendData(userMsg))
                .compose(Singles.async())
                .compose(Singles.disposable(compositeDisposable))
                .doOnSuccess(v -> {
                    //与地理位置相关的问题，先进行定位{
                    if (v.getText().contains("告诉") && v.getText().contains("哪个地方")) {
                        locationHolder.startLocation();
                        locUse = TULING;
                    } else {
                        aiuiResult.onNext(v);
                        speech.startSpeaking(v.getText(), speechListener);
                    }

                    saveTalkData(userMsg, v.getText());
                })
                .doOnError(__ -> error.onNext(Optional.empty()))
                .subscribe();
    }

    private void sendLocationInfo() {
        locationHolder.getLoaction()
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext(v -> {
                    Log.i(v);

                    switch (locUse) {
                        case TULING:
                            userMsg = v.getAddress();
                            useTuLing();
                            break;
                    }
                })
                .subscribe();
    }

    //若openid为null，则表示用户使用的是公用数据
    private TuLingSendData getTuLingSendData(String userMsg) {
        return new TuLingSendData(getTulingKey(), userMsg,
                null, HelloPref.INSTANCE.getOpenId());
    }

    private String getTulingKey() {
        return HelloPref.INSTANCE.getTalkPeople().equals(XIAO_YAN) ?
                Constants.TULING_KEY : Constants.TULING_KEY_BOY;
    }

    private void saveTalkData(String userTalk, String helloTalk) {
        speakDataHolder.addSpeakData(userTalk, helloTalk)
                .compose(Completables.async())
                .compose(Completables.disposable(compositeDisposable))
                .doOnError(Throwable::printStackTrace)
                .subscribe();
    }

    public enum LocUse {
        TULING,
        NEARBY
    }
}
