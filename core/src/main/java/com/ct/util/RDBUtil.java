package com.ct.util;

import com.ct.exception.BaseRuntimeException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;


import javax.persistence.*;
import java.lang.reflect.*;

/**
 * Created by Administrator on 2017/9/12.
 */
@SuppressWarnings("unckecked")
public class RDBUtil {

    /**
     * 根据JPA的Id注解获取主键值
     *
     * @param obj
     * @return
     */
    public static Object getPKVal(Object obj){
        Class clazz=obj.getClass();

        Field[] fields= FieldUtils.getFieldsWithAnnotation(obj.getClass(),Id.class);
        if(fields.length==0){
            throw BaseRuntimeException.getException("[RDBUtil.getPKVal],Class["+clazz.getName()+"] Must Have Primary Key!");
        }else if(fields.length>1){
            throw BaseRuntimeException.getException("[RDBUtil.getPKVal],Class["+clazz.getName()+"] Can't Have More Than One Primary Key!");
        }
        Field idField= fields[0];
        try {
            return PropertyUtils.getProperty(obj,idField.getName());
        } catch (IllegalAccessException |InvocationTargetException | NoSuchMethodException e) {
            throw BaseRuntimeException.getException(e);
        }
    }

}
