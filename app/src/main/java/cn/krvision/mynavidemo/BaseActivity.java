package cn.krvision.mynavidemo;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.unisound.client.SpeechConstants;
import com.unisound.client.SpeechSynthesizer;
import com.unisound.client.SpeechSynthesizerListener;

/**
 * Created by gaoqiong on 2018/3/9
 */

public class BaseActivity extends AppCompatActivity {


    private Context mContext;
    private static SpeechSynthesizer mTTSPlayer;
    public static boolean isSpeaking = false;
    public static boolean isReleaseTts = false;
    private static String mFrontendModel;
    private static String mBackendModellLzl;
    private static String mBackendModelFemale;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
//        initTts();
    }

    /**
     * 初始化本地离线TTS
     */
    protected void initTts() {
        String temp = Environment.getExternalStorageDirectory().getAbsolutePath();
        String path = temp + "/krvision/OfflineTTSModels";
        mFrontendModel = path + "/frontend_model";
        mBackendModellLzl = path + "/backend_lzl";
        mBackendModelFemale = path + "/backend_female";

        // 初始化语音合成对象
        mTTSPlayer = new SpeechSynthesizer(this, "jit6wcqgni5j2ww4a3eq64u3la3sdykqpgiykqqt", "542f54d41ec04aca63e00e2d0d415afa");
        // 设置本地合成
        mTTSPlayer.setOption(SpeechConstants.TTS_SERVICE_MODE, SpeechConstants.TTS_SERVICE_MODE_LOCAL);
        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_SPEED, 10 * Integer.parseInt("5"));
        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_PITCH, 10 * Integer.parseInt("5"));
        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_VOICE_VOLUME, 10 * Integer.parseInt("9"));

        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_FRONT_SILENCE, 50);
        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_BACK_SILENCE, 200);

        // 设置前端模型
        mTTSPlayer.setOption(SpeechConstants.TTS_KEY_FRONTEND_MODEL_PATH, mFrontendModel);
        // 设置后端模型
        final String speaker = "志玲姐姐";

        if (speaker.equals("高老师")) {
            mTTSPlayer.setOption(SpeechConstants.TTS_KEY_BACKEND_MODEL_PATH, mBackendModelFemale);
        } else {
            mTTSPlayer.setOption(SpeechConstants.TTS_KEY_BACKEND_MODEL_PATH, mBackendModellLzl);
        }

        // 设置回调监听
        mTTSPlayer.setTTSListener(new SpeechSynthesizerListener() {
            @Override
            public void onEvent(int type) {
                switch (type) {
                    case SpeechConstants.TTS_EVENT_INIT:
                        // 初始化成功回调
                        if (isReleaseTts) {
                            TTSSpeak(2, "视氪导航，让出行更美好");
                            isReleaseTts = false;
                        }
                        break;
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_START:
                        isSpeaking = true;
                        // 开始合成回调
                        break;
                    case SpeechConstants.TTS_EVENT_SYNTHESIZER_END:
                        // 合成结束回调
                        break;
                    case SpeechConstants.TTS_EVENT_BUFFER_BEGIN:
                        // 开始缓存回调
                        break;
                    case SpeechConstants.TTS_EVENT_BUFFER_READY:
                        // 缓存完毕回调
                        break;
                    case SpeechConstants.TTS_EVENT_PLAYING_START:
                        // 开始播放回调
//                        LogUtils.e("speaker=", "开始播放回调");
                        break;
                    case SpeechConstants.TTS_EVENT_PLAYING_END:
                        isSpeaking = false;
                        // 播放完成回调
                        break;
                    case SpeechConstants.TTS_EVENT_PAUSE:
                        // 暂停回调
                        break;
                    case SpeechConstants.TTS_EVENT_RESUME:
                        // 恢复回调
                        break;
                    case SpeechConstants.TTS_EVENT_STOP:
                        // 停止回调
                        break;
                    case SpeechConstants.TTS_EVENT_RELEASE:
                        // 释放资源回调

                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onError(int type, String errorMSG) {
                // 语音合成错误回调
                LogUtils.e("speaker=", errorMSG);
            }
        });

        // 初始化合成引擎
        mTTSPlayer.init("");
    }

    private static int priorityTemp = 5;

    public static void TTSSpeak(int priority, String string) {

        if (string.length() < 2) {
            return;
        }
        if (isSpeaking && priority > priorityTemp) {

        } else {
            if (mTTSPlayer != null) {
                mTTSPlayer.stop();
                mTTSPlayer.playText(string);
                priorityTemp = priority;
            }
        }


    }

    public static void releaseTts() {
        // 主动释放离线引擎
        if (mTTSPlayer != null) {
            mTTSPlayer.release(SpeechConstants.TTS_RELEASE_ENGINE, null);
        }
    }

}

