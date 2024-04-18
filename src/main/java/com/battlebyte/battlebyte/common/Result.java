package com.battlebyte.battlebyte.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private int status;
    private String msg;
    private Object data;

    public static Result success() {
        return new Result(0, "", null);
    }

    public static Result success(Object data) {
        return new Result(0, "", data);
    }

    public static Result error(String msg) {
        return new Result(1, msg, null);
    }
    public static Result error(int code, String msg) {
        return new Result(code, msg, null);
    }
}