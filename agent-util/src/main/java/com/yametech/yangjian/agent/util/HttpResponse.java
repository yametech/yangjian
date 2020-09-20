package com.yametech.yangjian.agent.util;

/**
 *
 * @author dengliming
 * @date 2020/4/12
 */
public class HttpResponse<T> {

    private int code;
    private T data;

    public HttpResponse() {
    }

    public HttpResponse(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean is2xxSuccessful() {
        return this.code / 100 == 2;
    }
}
