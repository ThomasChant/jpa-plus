package com.ct.condition.core;


/**
 * LambdaColumn
 *
 * @author chentao
 * @date 2021/7/14
 */
public interface LambdaColumnConverter<T> {

    String GET = "get";
    String IS = "is";

    /**
     * 将函数转为字段名
     * @param fun
     * @return
     */
    default String functionToColumnName(SFunction<T, ?> fun){
        String methodName = LambdaUtils.extractLambdaFunctionName(fun);
        if(methodName.startsWith(GET)){
            return StringUtil.lowerFirst(methodName.substring(3));
        }else if(methodName.startsWith(IS)){
            return StringUtil.lowerFirst(methodName.substring(2));
        }else {
            return methodName;
        }
    }
}
