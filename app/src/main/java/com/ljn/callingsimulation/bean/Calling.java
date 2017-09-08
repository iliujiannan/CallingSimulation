package com.ljn.callingsimulation.bean;

import android.app.Service;
import android.content.ServiceConnection;

import java.io.Serializable;

/**
 * Created by 12390 on 2017/8/30.
 */
public class Calling{
    private Integer callingId;
    private String caller;
    private String startTime;
    private String pattern;
    private String content;
    private String callerSex;
    private String dialect;
    private String voice;
    private String isOpen;
    private String del;

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }

    public Integer getCallingId() {
        return callingId;
    }

    public void setCallingId(Integer callingId) {
        this.callingId = callingId;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCallerSex() {
        return callerSex;
    }

    public void setCallerSex(String callerSex) {
        this.callerSex = callerSex;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }
}
