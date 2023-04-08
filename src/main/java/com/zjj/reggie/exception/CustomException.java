package com.zjj.reggie.exception;

/**
 * @create: 2023-04-05 13:21
 * @author: Junj_Zou
 * @Description:
 */
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}
