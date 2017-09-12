package com.ljn.callingsimulation.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.ljn.callingsimulation.bean.Calling;

/**
 * Created by 12390 on 2017/9/12.
 */
public class CustomCommunicate extends Thread {
    private Calling calling;
    public CustomCommunicate(Calling calling){
        this.calling = calling;
    }

    @Override
    public void run() {

    }
}
