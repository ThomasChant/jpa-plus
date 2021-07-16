package com.ct.wrapper;


import com.ct.bean.SuperBean;

/**
 * 静态工厂方法类
 *
 * @author chentao
 * @date 2021/7/14
 */
public final class Wrappers {

    public static <T extends SuperBean<Long>> SpecificationWrapper<T> wrapper(Class<T> clzz){
        return new SpecificationWrapper<>();
    }

    public static <T extends SuperBean<Long>> LambdaSpecificationWrapper<T> lambdaWrapper(Class<T> clzz){
        return new LambdaSpecificationWrapper<>();
    }
}
