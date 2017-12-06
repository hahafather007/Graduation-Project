package com.hello.model.aiui;

import android.content.Context;
import android.content.res.AssetManager;

import com.hello.utils.Log;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

@Singleton
public class AIUIRepository {
    private AIUIListener listener;
    private AIUIMessage message;

    @Inject
    Context context;

    @Inject
    AIUIRepository() {

    }

    @Inject
    void init() {
        initAIUIListener();

        AIUIAgent agent = AIUIAgent.createAgent(context, getAIUIParams(), listener);

        message = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, null, null);
        agent.sendMessage(message);

        Observable.timer(2, TimeUnit.SECONDS)
                .subscribe(__ -> {
                    message = new AIUIMessage(
                            AIUIConstant.CMD_WRITE, 0, 0, "data_type=text", "明天早上8点提醒我开会".getBytes());
                    agent.sendMessage(message);
                });
/*        message = new AIUIMessage(
                AIUIConstant.CMD_WRITE, 0, 0, "data_type=text", "今天天气怎么样".getBytes());
        agent.sendMessage(message);*/
    }

    private String getAIUIParams() {
        String params = "";
        AssetManager assetManager = context.getAssets();
        try {
            InputStream ins = assetManager.open("cfg/aiui_phone.cfg");
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
        listener = event -> {
            switch (event.eventType) {
                //唤醒事件
                case AIUIConstant.EVENT_WAKEUP: {
                    Log.i("wakeup!!!!");
                    break;
                }
                //结果事件（包含听写，语义，离线语法结果）
                case AIUIConstant.EVENT_RESULT: {
                    Log.i(event.info);
                    break;
                }
                //休眠事件
                case AIUIConstant.EVENT_SLEEP: {
                    break;
                }
                //错误事件
                case AIUIConstant.EVENT_ERROR: {
                    Log.e(event.info);
                    break;
                }
            }
        };
    }
}
