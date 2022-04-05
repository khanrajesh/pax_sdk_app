package com.matm.matmsdk.Utils;

public class ResultEvent extends Event {
    public enum Status {
        SUCCESS,
        FAILED,
        DISCOVER_ONE,
        SEARCH_COMPLETE,
        CONNECT_SUCCESS,
        CONNECT_FAILED,
        DOWNLOAD_SUCCESS,
        DOWNLOAD_FAILED,
        RECV_REPORT,
    }

    public ResultEvent(Status status) {
        super(status);
    }

    public ResultEvent(Status status, Object data) {
        super(status, data);
    }
}
