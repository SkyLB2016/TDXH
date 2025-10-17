package com.sky.base.utils.uu;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by SKY on 16/5/10 下午3:50.
 * 自定义的handler
 */
public class UIHandler extends Handler {

    private IHandler iHandler;//回调接口，消息传递给注册者

    public void setiHandler(IHandler iHandler) {
        this.iHandler = iHandler;
    }

    public UIHandler(Looper looper) {
        super(looper);
    }

    public UIHandler(Looper looper, IHandler iHandler) {
        super(looper);
        this.iHandler = iHandler;
    }


    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (iHandler != null) {
            iHandler.handleMessage(msg);//有消息，就传递
        }
    }

    public interface IHandler {
        void handleMessage(Message msg);
    }
}