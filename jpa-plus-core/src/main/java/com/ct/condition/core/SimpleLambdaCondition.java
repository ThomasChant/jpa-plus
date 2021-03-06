package com.ct.condition.core;


/**
 * 通过lambda调用
 *
 * @author chentao
 * @date 2021/7/14
 */
public class SimpleLambdaCondition<T> extends
        AbstractCondition<SimpleLambdaCondition<T>, SFunction<T,?>, T> implements LambdaColumnConverter<T> {

    @Override
    protected SimpleLambdaCondition<T> instance() {
        return new SimpleLambdaCondition<>();
    }

    @Override
    protected String columnToString(SFunction<T, ?> s){
        return functionToColumnName(s);
    }


}
