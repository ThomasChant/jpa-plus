package com.ct.wrapper;


import java.io.Serializable;
import java.util.function.Function;

/**
 * 可序列化lambda函数，用于获取字段名
 *
 * @author chentao
 * @date 2021/7/14
 */
@FunctionalInterface
public interface SFunction<T,R> extends Function<T,R> , Serializable {
}
