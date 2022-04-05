package com.matm.matmsdk.Utils;

public class Event {
    private Object status;
    private Object data = null;

    public Event(Object status) {
        this.setStatus(status);
    }

    public Event(Object status, Object data) {
        this.setStatus(status);
        this.setData(data);
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
