package com.ct.wrapper.core;

import java.lang.reflect.Array;

/**
 * ArrayUtil
 *
 * @author chentao
 * @date 2021/7/19
 */
public final class ArrayUtil {
    /**
     * 强转数组类型<br>
     * 强制转换的前提是数组元素类型可被强制转换<br>
     * 强制转换后会生成一个新数组
     *
     * @param arrayObj 原数组
     * @return 转换后的数组类型
     * @throws NullPointerException 提供参数为空
     * @throws IllegalArgumentException 参数arrayObj不是数组
     * @since 3.0.6
     */
    public static Object[] toArray(Object arrayObj) throws NullPointerException, IllegalArgumentException {
        if (null == arrayObj) {
            throw new NullPointerException("Argument [arrayObj] is null !");
        }
        if (!arrayObj.getClass().isArray()) {
            throw new IllegalArgumentException("Argument [arrayObj] is not array !");
        }
        final Class<?> componentType = arrayObj.getClass().getComponentType();
        final Object[] array = (Object[]) arrayObj;
        final Object[] result = (Object[]) Array.newInstance(componentType, array.length);
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }
}
