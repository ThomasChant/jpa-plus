package com.ct.wrapper.core;


/**
 * 静态工厂方法类
 *
 * @author chentao
 * @date 2021/7/14
 */
public final class Conditions {

    public static <T> SimpleCondition<T> use(Class<T> clzz){
        return new SimpleCondition<>();
    }

    public static <T> SimpleLambdaCondition<T> lambdaUse(Class<T> clzz){
        return new SimpleLambdaCondition<>();
    }
}
