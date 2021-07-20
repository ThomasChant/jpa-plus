
package com.ct.condition.core;


import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Lambda 解析工具类
 *
 * @author chentao
 * @since 2021-07-14
 */
public final class LambdaUtils {

    /**
     *
     * @param func 需要解析的 lambda 对象
     * @param <T>  类型，被调用的 Function 对象的目标类型
     * @return 返回解析后的结果
     */
    private static <T> SerializedLambda extract(SFunction<T, ?> func) {
        try {
            Method method = func.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            return  ((SerializedLambda) method.invoke(func));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("can not extract method of func");
        }
    }

    /**
     * @param func 需要解析的 lambda 对象
     * @param <T>  类型，被调用的 Function 对象的目标类型
     * @return 返回lambda函数方法名
     */
    public static <T> String extractLambdaFunctionName(SFunction<T, ?> func) {
        SerializedLambda serializedLambda = extract(func);
        return serializedLambda.getImplMethodName();
    }

}
