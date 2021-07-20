package com.ct.condition.core;

/**
 * JpaPlusException
 *
 * @author chentao
 * @date 2021/7/19
 */
public class JpaPlusException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public JpaPlusException() {
        super();
    }

    public JpaPlusException(String message) {
        super(message);
    }

    public JpaPlusException(String message, Throwable cause) {
        super(message, cause);
    }

    public static JpaPlusException getException(String message){
        return new JpaPlusException(message);
    }

    public static JpaPlusException getException(String message, Throwable throwable){
        return new JpaPlusException(message, throwable);
    }
}
