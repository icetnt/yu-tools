package com.yu.yutool.exception;

import com.yu.yutool.common.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局异常拦截
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 基础异常
     */
    @ExceptionHandler(value = BaseException.class)
    public RestResult customExceptionHandler(BaseException exception) {
        log.error("基础异常：", exception);
        return RestResult.error(exception.getCode(), exception.getMessage());
    }

    /**
     * 请求方法错误
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public RestResult HttpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException exception) {
        log.error("http请求方法异常", exception);
        return RestResult.error(RestResult.SYS_ERROR_CODE, exception.getMessage());
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler({BindException.class})
    public RestResult methodArgumentNotValidExceptionHandler(BindException exception) {
        log.error("参数校验失败：", exception);
        BindingResult bindingResult = exception.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        Map<String, String> validMsg = new HashMap<>(fieldErrors.size());
        for (FieldError fieldError : fieldErrors) {
            validMsg.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return RestResult.error(RestResult.FAIL_CODE, "参数校验失败:" + validMsg);
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public RestResult methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception) {
        log.error("参数校验失败：", exception);
        BindingResult bindingResult = exception.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        Map<String, String> validMsg = new HashMap<>(fieldErrors.size());
        for (FieldError fieldError : fieldErrors) {
            validMsg.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return RestResult.error(RestResult.FAIL_CODE, "参数校验失败:" + validMsg);
    }

    /**
     * http请求参数异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public RestResult HttpMessageNotReadableExceptionHandler(HttpMessageNotReadableException exception) {
        log.error("http请求参数异常", exception);
        return RestResult.error(RestResult.FAIL_CODE, "http请求参数异常:" + exception.getMessage());
    }

    /**
     * 参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public RestResult illegalArgumentExceptionHandler(IllegalArgumentException exception) {
        log.error("参数异常", exception);
        return RestResult.error(RestResult.FAIL_CODE, "参数异常:" + exception.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public RestResult systemExceptionHandler(Exception exception) {
        log.error("未知异常", exception);
        return RestResult.error(RestResult.SYS_ERROR_CODE, "未知异常");
    }

}
