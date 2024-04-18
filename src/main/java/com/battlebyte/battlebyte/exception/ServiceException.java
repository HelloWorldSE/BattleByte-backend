package com.battlebyte.battlebyte.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private int code;

    public ServiceException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public ServiceException(String msg) {
        super(msg);
        this.code = 1;
    }
}