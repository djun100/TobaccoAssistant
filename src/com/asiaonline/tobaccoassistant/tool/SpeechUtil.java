package com.asiaonline.tobaccoassistant.tool;

import android.content.Context;
import android.os.RemoteException;

import com.asiaonline.tobaccoassistant.PrimaryActivity;
import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechSynthesizer;
import com.iflytek.speech.SpeechUtility;
import com.iflytek.speech.SynthesizerListener;

public class SpeechUtil {
	private SpeechSynthesizer mTts;
	private static final String APPID = "527c6099";
	private boolean ttsMark = false;
	private Context context;
	public SpeechUtil(Context context){
		this.context = context;
		SpeechUtility.getUtility(context).setAppid(APPID);
	}
	public void initSpeechSynthesizer() {
		mTts = new SpeechSynthesizer(context, mTtsInitListener);
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, "local");
		mTts.setParameter(SpeechSynthesizer.SPEED, "50");
		mTts.setParameter(SpeechSynthesizer.PITCH, "50");
		mTts.setParameter(SpeechConstant.PARAMS, "tts_audio_path=/sdcard/tts.pcm");
	}
	/**
     * 初期化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(ISpeechModule arg0, int code) {
        	if (code == ErrorCode.SUCCESS) {
        		ttsMark = true;
        	}
		}
    };
    
    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener.Stub() {
        @Override
        public void onBufferProgress(int progress) throws RemoteException {
        }
        @Override
        public void onCompleted(int code) throws RemoteException {
        }
        @Override
        public void onSpeakBegin() throws RemoteException {
        }
        @Override
        public void onSpeakPaused() throws RemoteException {
        }
        @Override
        public void onSpeakProgress(int progress) throws RemoteException {
        }
        @Override
        public void onSpeakResumed() throws RemoteException {
        }
    };
    /**
	 * 是否有讯飞语音支持包
	 * @return
	 */
	public boolean isTTSSupport(){
		return !(SpeechUtility.getUtility(context).queryAvailableEngines() == null
				|| SpeechUtility.getUtility(context).queryAvailableEngines().length <= 0);
	}
	
    /**
     * 语音合成
     * @param context
     * @return
     */
    public int speak(String context){
    	if(ttsMark&&PrimaryActivity.speek==0){
    		mTts.stopSpeaking(mTtsListener);
    		return mTts.startSpeaking(context, mTtsListener);
    	}
    	return -1;
    }
    
    public void destroy(){
    	mTts.stopSpeaking(mTtsListener);
        // 退出时释放连接
        mTts.destory();
    }
}
