package com.ct.condition.core;


/**
 * SimpleCondition
 *
 * @author chentao
 * @date 2021/7/6
 */
public class SimpleCondition<T> extends AbstractCondition<SimpleCondition<T>, String, T> {

    @Override
    protected SimpleCondition<T> instance() {
        return new SimpleCondition<>();
    }
}
