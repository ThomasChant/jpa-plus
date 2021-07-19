package com.ct.condition.core;

/**
 * JpaMinusException
 *
 * @author chentao
 * @date 2021/7/19
 */
public class JpaMinusException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public JpaMinusException() {
        super();
    }

    public JpaMinusException(String message) {
        super(message);
    }

    public JpaMinusException(String message, Throwable cause) {
        super(message, cause);
    }

    public static JpaMinusException getException(String message){
        return new JpaMinusException(message);
    }

    public static JpaMinusException getException(String message, Throwable throwable){
        return new JpaMinusException(message, throwable);
    }
}
