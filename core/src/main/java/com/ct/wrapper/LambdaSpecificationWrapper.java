package com.ct.wrapper;


import com.ct.bean.SuperBean;

/**
 * LambdaConditionWrapper
 *
 * @author chentao
 * @date 2021/7/14
 */
public class LambdaSpecificationWrapper<T extends SuperBean<Long>> extends
        AbstractWrapper<LambdaSpecificationWrapper<T>, SFunction<T,?>, T> implements LambdaColumn<T> {

    @Override
    protected LambdaSpecificationWrapper<T> instance() {
        return new LambdaSpecificationWrapper<>();
    }

    @Override
    protected String columnToString(SFunction<T, ?> s){
        return functionToColumnName(s);
    }


}
