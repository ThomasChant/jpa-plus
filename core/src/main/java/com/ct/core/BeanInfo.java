package com.ct.core;

import com.ct.anno.Unique;
import com.ct.bean.BaseBean;
import com.ct.bean.SuperBean;
import com.ct.util.SpringUtil;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.annotations.Where;
import org.springframework.cache.Cache;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class BeanInfo{
    /**
     * service的实体类
     */
    public Class clazz;
    /**
     * Unique 注解字段集合
     */
    public Boolean isCheckUnique;
    public List<Field> uniqueFieldList;
    /**
     * 当前类逻辑删除字段
     * isLogicDelete:当前类是否是逻辑删除
     */
    public Boolean isLogicDelete;

    /**
     * manyToManyFieldList 多对多字段集合
     * oneToManyFieldList 一对多字段集合
     * manyToOneFieldList 多对一字段集合
     * oneToOneFieldList 一对一字段集合
     */
    public List<Field> manyToManyFieldList;
    public List<Field> oneToManyFieldList;
    public List<Field> manyToOneFieldList;
    public List<Field> oneToOneFieldList;

    public String tableName;
    public List<Field> tableFields;
    /**
     * 获取bean信息的缓存,先从自定义的缓存设置中取,取不到则生成bean信息
     * @param clazz
     * @return
     */
    public static BeanInfo getBeanInfo(Class clazz){
        BeanInfo beanInfo=null;
        Cache cache=null;
        if(SpringUtil.applicationContext!=null){
            cache=SpringUtil.applicationContext.getBean("beanInfoCache", Cache.class);
            if(cache!=null){
                beanInfo= cache.get(clazz.getName(),BeanInfo.class);
            }
        }
        if(beanInfo==null){
            beanInfo=new BeanInfo(clazz);
            beanInfo.init();
            if(cache!=null){
                cache.put(clazz.getName(),beanInfo);
            }
        }
        return beanInfo;
    }


    private BeanInfo(Class clazz){
        this.clazz = clazz;
    }

    public void init(){
        initUnique();
        initLoginDelete();
        initJPAAnno();
        initTableSchema();
    }

    private void initTableSchema() {
        Table table = (Table) clazz.getAnnotation(Table.class);
        tableName = table.name();
        Field[] fields = clazz.getDeclaredFields();
        this.tableFields = new ArrayList<>();

        List<Field> superBeanFields = Arrays.stream(SuperBean.class.getDeclaredFields()).filter(e->!e.isAnnotationPresent(Transient.class)).collect(Collectors.toList());
        this.tableFields.addAll(superBeanFields);

        List<Field> baseBeanFields = Arrays.stream(BaseBean.class.getDeclaredFields()).filter(e->!e.isAnnotationPresent(Transient.class)).collect(Collectors.toList());

        if(this.getClazz().getSuperclass().equals(BaseBean.class)) {
            this.tableFields.addAll(baseBeanFields);
        }
        for(Field field : fields){
            //排除Transient
            if(!field.isAnnotationPresent(Transient.class)){
                tableFields.add(field);
            }
        }
    }

    public void initUnique(){
        uniqueFieldList= Arrays.asList(FieldUtils.getFieldsWithAnnotation(clazz, Unique.class));
        if(uniqueFieldList.isEmpty()){
            isCheckUnique =false;
        }else{
            isCheckUnique =true;
        }
    }

    /**
     * 区别类是否是逻辑删除,判断类是否存在Where注解
     */
    public void initLoginDelete(){
        Where anno= (Where) clazz.getAnnotation(Where.class);
        if(anno==null){
            isLogicDelete=false;
        }else{
            isLogicDelete=true;
        }
    }


    /**
     * 则初始化 JPA 注解
     */
    public void initJPAAnno(){
        manyToManyFieldList=Arrays.asList(FieldUtils.getFieldsWithAnnotation(clazz, ManyToMany.class));
        oneToManyFieldList=Arrays.asList(FieldUtils.getFieldsWithAnnotation(clazz, OneToMany.class));
        manyToOneFieldList=Arrays.asList(FieldUtils.getFieldsWithAnnotation(clazz, ManyToOne.class));
        oneToOneFieldList=Arrays.asList(FieldUtils.getFieldsWithAnnotation(clazz, OneToOne.class));
    }


    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Boolean getCheckUnique() {
        return isCheckUnique;
    }

    public void setCheckUnique(Boolean checkUnique) {
        isCheckUnique = checkUnique;
    }

    public List<Field> getUniqueFieldList() {
        return uniqueFieldList;
    }

    public void setUniqueFieldList(List<Field> uniqueFieldList) {
        this.uniqueFieldList = uniqueFieldList;
    }

    public Boolean getLogicDelete() {
        return isLogicDelete;
    }

    public void setLogicDelete(Boolean logicDelete) {
        isLogicDelete = logicDelete;
    }


    public List<Field> getManyToManyFieldList() {
        return manyToManyFieldList;
    }

    public void setManyToManyFieldList(List<Field> manyToManyFieldList) {
        this.manyToManyFieldList = manyToManyFieldList;
    }

    public List<Field> getOneToManyFieldList() {
        return oneToManyFieldList;
    }

    public void setOneToManyFieldList(List<Field> oneToManyFieldList) {
        this.oneToManyFieldList = oneToManyFieldList;
    }

    public List<Field> getManyToOneFieldList() {
        return manyToOneFieldList;
    }

    public void setManyToOneFieldList(List<Field> manyToOneFieldList) {
        this.manyToOneFieldList = manyToOneFieldList;
    }

    public List<Field> getOneToOneFieldList() {
        return oneToOneFieldList;
    }

    public void setOneToOneFieldList(List<Field> oneToOneFieldList) {
        this.oneToOneFieldList = oneToOneFieldList;
    }
}
