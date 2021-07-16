package com.ct.core;

import com.ct.bean.SuperBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * IBaseService
 *
 * @author chentao
 * @date 2021/2/7
 */

public interface IBaseService<T extends SuperBean<K>, K extends Serializable> {

    boolean existsById(K id);

    long count();

    long count(Specification<T> condition);

    List<T> findAll();

    List<T> findAll(Specification<T> condition);

    List<T> findAll(Specification<T> condition, Sort sort);

    Page<T> findAll(Pageable pageable);

    Page<T> findAll(Specification<T> condition, Pageable pageable);

    List<T> findAll(Sort sort);

    List<T> findAllById(Iterable<K> iterable);

    List<T> findAllById(K[] kArr);

    T findById(K k);

    T findOne(Specification<T> condition);

    @Transactional
    T save(T t);

    @Transactional
    List<T> saveAll(Iterable<T> iterable, Boolean... valid);

    @Transactional
    void deleteAll();

    @Transactional
    void deleteById(K... ids);

    @Transactional
    void delete(T t);

    @Transactional
    void deleteAll(Iterable<T> iterable);

    @Transactional
    void deleteAllInBatch();

    @Transactional
    void deleteInBatch(Iterable<T> iterable);


    /**
     * 优于普通删除方法
     *
     * @param condition
     * @return 删除的记录条数
     */
    @Transactional
    int delete(Specification<T> condition);


    /**
     * 优于普通更新方法
     *
     * 注意:调用此方法的方法必须加上 @Transactional
     *
     * @param condition
     * @param attrMap   更新的字段和值的map
     * @return 更新的记录条数
     */
    @Transactional
    int update(Specification<T> condition, Map<String, Object> attrMap);

    /**
     * 执行native sql
     * query.getResultList() 结果类型为 List<Map>
     *
     * @param sql
     * @return
     */
    @Transactional
    Query executeNativeSql(String sql, Object... params);


    <R> Page<R> queryPageByNativeSql(String sql, Class<R> clzz, Pageable pageable, Object... params);

    /**
     * 采用jdbc查询方式,将condition转换为where条件
     * condition中的字段名称将会由驼峰格式转换为下划线拼接格式
     *
     * @param sqlPre    sql语句前缀,where之前
     *                  example
     *                  select * from t
     *                  select a.name,b.* from a inner join b on a.id=b.relation_id
     * @param sqlSuffix sql语句结尾,where之后
     *                  example:
     *                  limit 1,10
     *                  order by id desc
     * @param condition 条件
     * @param clazz     返回结果集
     *                  三种情况
     *                  1、java 8大基础类型和包装类型,String
     *                  2、自定义对象类型
     * @param <R>       结果集类型
     * @return
     */
    <R> List<R> queryByCondition(String sqlPre, Specification<T> condition, String sqlSuffix, Class<R> clazz);

    <R> Page<R> queryPageByCondition(String sqlPre, Specification<T> condition, Pageable pageable, Class<R> clazz);

    <R> Page<R> queryPageByCondition(String sqlPre, Specification<T> condition, Pageable pageable, String suffixSql, Class<R> clazz);

    /**
     * 字段唯一性验证
     * <p>
     * 对象t主键必须为'id'且必须属于BeanUtil.BASE_DATA_TYPE
     *
     * @param fieldName  属性名称
     * @param val        属性值
     * @param excludeIds 排除id数组
     * @return
     */
    boolean isUnique(String fieldName, Object val, K... excludeIds);

    /**
     * 保存前进行唯一性验证
     *
     * @param t
     */
    void validateUniqueBeforeSave(T t) ;
    /**
     * 保存前进行批量唯一性验证
     *
     * @param iterable
     */
    void validateUniqueBeforeSave(Iterable<T> iterable);

    /**
     * 批量新增（仅做新增，不能用于更新）
     *
     * @param entities
     */
    void batchInsert(List<T> entities);

    /**
     * 批量新增，如果唯一键重复则更新 (唯一键既可以是主键，也可以是其他唯一键)
     * @param entities
     */
    void batchInsertOnDuplicateKeyUpdate(List<T> entities);
}
