/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sgu.admissor.dto;

/**
 *
 * @author Admin
 */
public class BUSResult<T> {
    private boolean success;
    private String message;
    private T data;

    private BUSResult(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> BUSResult<T> success(String message) {
        return new BUSResult<>(true, message, null);
    }

    public static <T> BUSResult<T> successWithData(String message, T data) {
        return new BUSResult<>(true, message, data);
    }

    public static <T> BUSResult<T> error(String message) {
        return new BUSResult<>(false, message, null);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
