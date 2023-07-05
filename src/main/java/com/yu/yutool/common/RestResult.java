package com.yu.yutool.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yu.yutool.exception.BaseException;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RestResult<T> implements Serializable {

    public static String SUCCESS_CODE = "0";
    public static String FAIL_CODE = "1";
    public static String SYS_ERROR_CODE = "500";

    private String code;
    private String msg;
    private T data;

    private RestResult(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public RestResult() {
        this.code = SYS_ERROR_CODE;
    }

    public static RestResult ok() {
        return new RestResult<>(SUCCESS_CODE, "操作成功", null);
    }

    public static RestResult ok(String msg) {
        return new RestResult<>(SUCCESS_CODE, msg, null);
    }

    public static <T> RestResult<T> ok(T data) {
        return new RestResult<>(SUCCESS_CODE, "操作成功", data);
    }

    public static <T> RestResult<T> ok(String msg, T data) {
        return new RestResult<>(SUCCESS_CODE, msg, data);
    }

    public static RestResult error() {
        return new RestResult<>(FAIL_CODE, "操作失败", null);
    }

    public static RestResult error(String msg) {
        return new RestResult<>(FAIL_CODE, msg, null);
    }

    public static RestResult error(Throwable throwable) {
        if (throwable instanceof BaseException) {
            return new RestResult<>(((BaseException) throwable).getCode()
                    , throwable.getMessage(), null);
        } else {
            return new RestResult<>(FAIL_CODE
                    , throwable.getMessage(), null);
        }
    }

    public static RestResult error(String code, String msg) {
        return new RestResult<>(code, msg, null);
    }

    public static RestResult error(Integer code, String msg) {
        return new RestResult<>(String.valueOf(code), msg, null);
    }

    public static <T> RestResult<T> error(String code, String msg, T data) {
        return new RestResult<>(code, msg, data);
    }

    public static RestResult okWithKeyValueSerial(Object... objects) {
        return keyValueSerial(SUCCESS_CODE, "操作成功", objects);
    }

    public static RestResult keyValueSerial(String code, String msg, Object... objects) {
        Map<Object, Object> map = new HashMap<>(16);
        int i = 1;
        Object key = null;
        Object value = null;
        for (Object oneObject : objects) {
            if (i % 2 == 1) {
                key = oneObject;
            } else {
                value = oneObject;
            }
            if (i % 2 == 0) {
                if (key == null) {
                    throw new BaseException("key不能为空");
                }
                map.put(key, value);
                key = null;
                value = null;
            }
            i++;
        }
        return new RestResult<Object>(code, msg, map);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @JsonIgnore
    public boolean isSuccess() {
        if (StringUtils.isBlank(code)) {
            return false;
        }
        return code.equals(SUCCESS_CODE);
    }

    @JsonIgnore
    public boolean isFailed() {
        return !code.equals(SUCCESS_CODE);
    }

    @Override
    public String toString() {
        return "RestResult{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
