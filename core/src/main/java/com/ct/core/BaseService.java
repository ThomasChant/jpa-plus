package com.ct.core;

import com.ct.anno.Unique;
import com.ct.bean.SuperBean;
import com.ct.exception.BaseRuntimeException;
import com.ct.util.RDBUtil;
import com.ct.util.StringUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.*;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@SuppressWarnings("unchecked")
public abstract class BaseService<T extends SuperBean<K>, K extends Serializable> implements IBaseService<T, K> {
    @PersistenceContext
    public EntityManager em;

    @Autowired
    public BaseRepository<T, K> repository;

    @Autowired
    public JdbcTemplate jdbcTemplate;

    private volatile BeanInfo beanInfo;


    /**
     * 获取当前service对应实体类的信息
     *
     * @return
     */
    public BeanInfo getBeanInfo() {
        if (beanInfo == null) {
            synchronized (this) {
                if (beanInfo == null) {
                    Class beanClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                    beanInfo = BeanInfo.getBeanInfo(beanClass);
                }
            }
        }
        return beanInfo;
    }

    @Override
    public boolean existsById(K id) {
        return repository.existsById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public List<T> findAll(Specification<T> specification, Sort sort) {
        return repository.findAll(specification, sort);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<T> findAll(Specification<T> specification, Pageable pageable) {
        return repository.findAll(specification, pageable);
    }

    @Override
    public List<T> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    @Override
    public List<T> findAllById(Iterable<K> iterable) {
        return repository.findAllById(iterable);
    }

    @Override
    public List<T> findAllById(K[] kArr) {
        return repository.findAllById(Arrays.asList(kArr));
    }

    @Override
    public T findById(K k) {
        return repository.findById(k).orElse(null);
    }

    @Override
    public T findOne(Specification<T> specification) {
        return repository.findOne(specification).orElse(null);
    }

    @Override
    @Transactional
    public T save(T t) {
        validateUniqueBeforeSave(t);
        return repository.save(t);
    }

    @Override
    @Transactional
    public List<T> saveAll(Iterable<T> iterable, Boolean... valid) {
        if(valid.length == 0 || (valid.length != 0 && valid[0])) {
            validateUniqueBeforeSave(iterable);
        }
        return repository.saveAll(iterable);
    }

    @Override
    @Transactional
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    @Transactional
    public void deleteById(K... ids) {
        for (K id : ids) {
            repository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void delete(T t) {
        repository.delete(t);
    }

    @Override
    @Transactional
    public void deleteAll(Iterable<T> iterable) {
        repository.deleteAll(iterable);
    }

    @Override
    @Transactional
    public void deleteAllInBatch() {
        repository.deleteAllInBatch();
    }

    @Override
    @Transactional
    public void deleteInBatch(Iterable<T> iterable) {
        repository.deleteInBatch(iterable);
    }

    /**
     * 优于普通删除方法
     *
     * @param specification
     * @return 删除的记录条数
     */
    @Override
    @Transactional
    public int delete(Specification<T> specification) {
        return deleteBySpecification(specification);
    }

    private int deleteBySpecification(Specification<T> specification) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(getBeanInfo().clazz);
        CriteriaDelete<T> criteriaDelete = criteriaBuilder.createCriteriaDelete(getBeanInfo().clazz);
        Predicate predicate = specification.toPredicate(criteriaDelete.from(getBeanInfo().clazz), criteriaQuery, criteriaBuilder);
        criteriaDelete.where(predicate);
        return em.createQuery(criteriaDelete).executeUpdate();
    }

    public int updateBySpecification(Specification<T> specification, Map<String, Object> attrMap) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(getBeanInfo().clazz);
        CriteriaUpdate<T> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(getBeanInfo().clazz);
        Predicate predicate = specification.toPredicate(criteriaUpdate.from(getBeanInfo().clazz), criteriaQuery, criteriaBuilder);
        criteriaUpdate.where(predicate);
        Iterator<Map.Entry<String, Object>> it = attrMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            criteriaUpdate.set(entry.getKey(), entry.getValue());
        }
        return em.createQuery(criteriaUpdate).executeUpdate();
    }

    @Override
    @Transactional
    public int update(Specification<T> specification, Map<String, Object> attrMap) {
        if (attrMap == null || attrMap.size() == 0) {
            return 0;
        }
        return updateBySpecification(specification, attrMap);
    }

    /**
     * 执行native sql
     * query.getResultList() 结果类型为 List<Map>
     *
     * @param sql
     * @return
     */
    @Override
    @Transactional
    public Query executeNativeSql(String sql, Object... params) {
        Query query = em.createNativeQuery(sql);
        //设置返回的结果集为List<Map>形式;如果不设置,则默认为List<Object[]>
        query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        if (params != null) {
            for (int i = 0; i <= params.length - 1; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }
        return query;
    }

//    @Override
//    public <R> Page<R> queryPageByNativeSql(String sql, Class<R> clzz, Pageable pageable, Object... params) {
//        Objects.requireNonNull(pageable);
//        String limitSql = sql + " limit ?,?";
//        Object[] limitParams;
//        if (params == null || params.length == 0) {
//            limitParams = new Object[2];
//            params = new Object[0];
//        } else {
//            limitParams = Arrays.copyOf(params, params.length + 2);
//        }
//        limitParams[limitParams.length - 2] = pageable.getPageNumber() * pageable.getPageSize();
//        limitParams[limitParams.length - 1] = pageable.getPageSize();
//        List<Map<String, Object>> ts = jdbcTemplate.query(limitSql, MyColumnMapRowMapper.ROW_MAPPER, limitParams);
//        List<R> list = ts.stream().map(e -> new JSONObject(e).toBean(clzz)).collect(Collectors.toList());
//        long count = jdbcTemplate.queryForObject("select count(1) as count from (" + sql + ") a", params, Long.class);
//        return new PageImpl<>(list, pageable, count);
//    }


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
    @Override
    public boolean isUnique(String fieldName, Object val, K... excludeIds) {
        //值为空，不需要判断是否唯一
        if(Objects.isNull(val)||StringUtils.isEmpty(val.toString())){
            return true;
        }
        boolean flag = true;
        List<T> resultList = repository.findAll((Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) -> {
            {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Expression<Boolean>> expressions = predicate.getExpressions();
                expressions.add(criteriaBuilder.equal(root.get(fieldName), val));
                return predicate;
            }
        });
        if (resultList == null || resultList.isEmpty()) {
            return true;
        } else {
            if (excludeIds == null || excludeIds.length == 0 || Arrays.stream(excludeIds).filter(Objects::nonNull).count() == 0) {
                return false;
            } else {
                Set<K> idSet = Arrays.stream(excludeIds).filter(Objects::nonNull).collect(Collectors.toSet());
                List filterList = resultList.stream().filter(e -> !idSet.contains(RDBUtil.getPKVal(e))).collect(Collectors.toList());
                if (filterList != null && !filterList.isEmpty()) {
                    flag = false;
                }
            }
        }
        return flag;
    }


    /**
     * 获取唯一注解字段的message值
     *
     * @param field
     * @return
     */
    private String getUniqueMessage(Field field) {
        Unique anno = field.getAnnotation(Unique.class);
        String msg = anno.messageValue();
        if (StringUtils.isEmpty(msg)) {
            msg = I18NData.getI18NData(anno.messageKey()).getValue(field.getName());
        }
        return msg;
    }

    /**
     * 保存前进行唯一性验证
     *
     * @param t
     */
    @Override
    public void validateUniqueBeforeSave(T t) {
        if (!getBeanInfo().isCheckUnique) {
            return;
        }
        //1、循环集合,验证每个唯一字段是否在数据库中有重复值
        for (Field f : getBeanInfo().uniqueFieldList) {
            Object val;
            try {
                val = PropertyUtils.getProperty(t, f.getName());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw BaseRuntimeException.getException(e);
            }
            if (!isUnique(f.getName(), val, (K) RDBUtil.getPKVal(t))) {
                throw BaseRuntimeException.getException(getUniqueMessage(f));
            }
        }
    }

    /**
     * 保存前进行批量唯一性验证
     *
     * @param iterable
     */
    @Override
    public void validateUniqueBeforeSave(Iterable<T> iterable) {
        if (!getBeanInfo().isCheckUnique) {
            return;
        }
        try {
            //1、循环集合,看传入的参数集合中唯一字段是否有重复的值
            Map<String, Set<Object>> fieldValueSetMap = new HashMap<>();
            for (T t : iterable) {
                for (Field f : getBeanInfo().uniqueFieldList) {
                    String fieldName = f.getName();
                    Object val = PropertyUtils.getProperty(t, fieldName);

                    Set<Object> valueSet = fieldValueSetMap.get(fieldName);
                    if (valueSet == null) {
                        valueSet = new HashSet<>();
                        fieldValueSetMap.put(fieldName, valueSet);
                    } else {
                        if (valueSet.contains(val)) {
                            throw BaseRuntimeException.getException(getUniqueMessage(f));
                        }
                    }
                    //只有非空的值才进行唯一性判断
                    if(val != null && org.apache.commons.lang3.StringUtils.isNotBlank(val.toString())) {
                        valueSet.add(val);
                    }
                }
            }
            //2、循环集合,验证每个唯一字段是否在数据库中有重复值
            for (T t : iterable) {
                for (Field f : getBeanInfo().uniqueFieldList) {
                    String fieldName = f.getName();
                    Object val = PropertyUtils.getProperty(t, fieldName);
                    if (!isUnique(f.getName(), val, (K) RDBUtil.getPKVal(t))) {
                        throw BaseRuntimeException.getException(getUniqueMessage(f));
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw BaseRuntimeException.getException(e);
        }
    }

    /**
     * 批量新增（仅做新增，不能用于更新）
     *
     * @param collect
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsert(List<T> collect) {
        if (collect.size()>0){
            String batchInsertSql = getBatchInsertSql();
            batchExecute(collect, batchInsertSql, true);
        }
    }

    private void setPreparedStatement(PreparedStatement ps, T t, boolean isAdd) throws SQLException {
        for (int j = 0; j < beanInfo.tableFields.size(); j++) {
            //新增，忽略掉id字段
            if(isAdd && j == 0){
                continue;
            }
            Field field = beanInfo.tableFields.get(j);
            try {
                field.setAccessible(true);
                if(isAdd) {
                    ps.setObject(j, field.get(t));
                }else {
                    ps.setObject(j+1, field.get(t));
                }
            } catch (Exception e) {
                throw BaseRuntimeException.getException(e);
            }
        }
    }


    private String getBatchInsertSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ");
        builder.append(this.getBeanInfo().tableName);
        builder.append(" (");
        for (Field field : this.getBeanInfo().tableFields) {
            if("id".equals(field.getName())){
                continue;
            }
            builder.append("`");
            builder.append(StringUtil.toFirstSplitWithUpperCase(field.getName(), '_'));
            builder.append("`");
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(") VALUES (");
        for (int i = 1; i < this.beanInfo.tableFields.size(); i++) {
            builder.append("?");
            if (i != this.beanInfo.tableFields.size() - 1) {
                builder.append(",");
            }
        }
        builder.append(")");
        return builder.toString();
    }

    private String getBatchUpdateSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ");
        builder.append(this.getBeanInfo().tableName);
        builder.append(" (");
        for (Field field : this.getBeanInfo().tableFields) {
            if (field.isAnnotationPresent(OneToMany.class) ||
                    field.isAnnotationPresent(ManyToMany.class) ||
                    field.isAnnotationPresent(OneToOne.class) ||
                    field.isAnnotationPresent(ManyToOne.class)
            ) {
                throw BaseRuntimeException.getException("不支持字段上存在注解@OneToMany、@ManyToMany、@OneToOne、@ManyToOne的批量插入");
            }
            builder.append("`");
            builder.append(StringUtil.toFirstSplitWithUpperCase(field.getName(), '_'));
            builder.append("`");
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(") VALUES (");
        for (int i = 0; i < this.beanInfo.tableFields.size(); i++) {
            builder.append("?");
            if (i != this.beanInfo.tableFields.size() - 1) {
                builder.append(",");
            }
        }
        builder.append(")");
        builder.append(" ON DUPLICATE KEY UPDATE ");
        for (int i = 0; i < this.beanInfo.tableFields.size(); i++) {
            String fieldName = StringUtil.toFirstSplitWithUpperCase(this.beanInfo.tableFields.get(i).getName(), '_');
            builder.append(fieldName);
            builder.append("=");
            builder.append("values(");
            builder.append(fieldName);
            builder.append(")");
            if (i != this.beanInfo.tableFields.size() - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }




    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsertOnDuplicateKeyUpdate(List<T> entities){
        if(entities.size()>0){
            String batchUpdateSql = getBatchUpdateSql();
            batchExecute(entities, batchUpdateSql, false);
        }
    }

    private void batchExecute(List<T> entities, String batchSql, boolean isAdd) {
        jdbcTemplate.batchUpdate(batchSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                T t = entities.get(i);
                if (Objects.nonNull(t)) {
                    setPreparedStatement(ps, t, isAdd);
                }
            }

            @Override
            public int getBatchSize() {
                return entities.size();
            }
        });
    }
}
