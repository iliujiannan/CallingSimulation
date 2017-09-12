package com.ljn.callingsimulation.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by 12390 on 2017/9/12.
 */
public class SmartCommunicate extends Thread implements ICommunicate{


    public SmartCommunicate(){

    }

    @Override
    public void run() {
        super.run();
    }

    @Override
    public void begin() {
        this.start();
    }

    @Override
    public void end() {

    }
}
