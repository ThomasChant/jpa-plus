package com.ct.condition.core;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * Wrapper
 *
 * @author chentao
 * @date 2021/7/6
 */
@SuppressWarnings("unchecked")
public abstract class AbstractCondition<Child extends AbstractCondition<Child,R,T>, R, T>
        implements Condition<Child, R, T> {

    private final Child typedThis = (Child)this;
    private Specification<T> specification;
    private volatile Predicate.BooleanOperator operator;

    public AbstractCondition() {
        this(Predicate.BooleanOperator.AND);
    }

    private AbstractCondition(Predicate.BooleanOperator operator) {
        this.operator = operator;
        this.specification = (Specification<T>) (root, query, criteriaBuilder) -> null;
    }

    /**
     * 调用链最后调用的方法，返回该condition
     */
    @Override
    public Specification<T> toSpec() {
        return specification;
    }

    /**
     * 嵌套and，和调用链前面的条件构成且的关系
     * 如：
     *  eq("a", 1)
     * .eq("b", 1)
     * .and(i->i.eq("c",1).eq("d",1))
     * .get();
     * 等价于：
     * (a = 1 && b = 2) and (c = 1 and d = 1)
     * @param consumer
     * @return Wrapper
     */
    @Override
    public Child and(Consumer<Child> consumer) {
        Child wrapper = instance();
        consumer.accept(wrapper);
        this.mergeSpecification(wrapper.toSpec(), Predicate.BooleanOperator.AND);
        return typedThis;
    }


    /**
     * 子类返回自己的一个实例
     * @return
     */
    protected abstract Child instance();

    /**
     *
     * 嵌套or查询，和调用链中上一个条件构成或的关系
     * 如：
     * .eq("a", 1)
     * .eq("b", 1)
     * .or(i->i.eq("c",1).eq("d",1))
     * .or(i->i.eq("f",1))
     * .get();
     * 等价于：
     * or(((a = 1 && b = 2) or (c = 1 and d = 1)) and f = 1)
     * @param consumer
     * @return Child
     */
    @Override
    public Child or(Consumer<Child> consumer) {
        Child wrapper = instance();
        consumer.accept(wrapper);
        this.mergeSpecification(wrapper.toSpec(), Predicate.BooleanOperator.OR);
        return typedThis;
    }

    /**
     *
     * or()的作用是将与操作变成或操作
     * 默认情况下两个条件之间的是与操作，
     * 如：
     * eq("a", 1).eq("b", 1).get()
     * 等价于
     * a = 1 and b = 1
     *
     * 如果要变成或操作只需要在调用链中加上or()
     * 如：
     * eq("a", 1).or().eq("b", 1).get()
     * 等价于
     * a = 1 or b = 1
     *
     *  or()如果放在在调用链的末尾，将被忽略
     * @return Wrapper
     */
    @Override
    public Child or() {
        this.mergeSpecification(null, Predicate.BooleanOperator.OR);
        this.operator = Predicate.BooleanOperator.OR;
        return typedThis;
    }

    /**
     * and()的作用是将或操作变成与操作
     * @return
     */
    @Override
    public Child and() {
        this.mergeSpecification(null, Predicate.BooleanOperator.AND);
        this.operator = Predicate.BooleanOperator.AND;
        return typedThis;
    }

    /**
     * 大于或等于 >=
     * 如: ge("a", 1) 等价于 a >= 1
     * @param field
     * @param val
     * @param <X>
     * @return Wrapper
     */
    @Override
    public <X extends Comparable<? super X>> Child ge(R field, X val) {
        this.specification = getSpecification(SpecificationFactory.createSpec(Handler.GE, columnToString(field), val));
        return typedThis;
    }


    protected String columnToString(R s){
        return (String)s;
    }

    /**
     * 等于
     * 如: eq("a", 1) 等价于 a == 1
     */
    @Override
    public <X extends Comparable<? super X>> Child eq(R field, X val) {
        this.specification = getSpecification(SpecificationFactory.createSpec(Handler.EQUAL, columnToString(field), val));
        return typedThis;
    }


    /**
     * 不等于
     * 如: eq("a", 1) 等价于 a <> 1
     */
    @Override
    public <X extends Comparable<? super X>> Child notEq(R field, X val) {
        this.specification = getSpecification(SpecificationFactory.createSpec(Handler.NOT_EQUAL, columnToString(field), val));
        return typedThis;
    }

    /**
     * 大于
     * 如: gt("a", 1) 等价于 a > 1
     */
    @Override
    public <X extends Comparable<? super X>> Child gt(R field, X val) {
        this.specification = getSpecification(SpecificationFactory.createSpec(Handler.GT, columnToString(field), val));
        return typedThis;
    }

    /**
     * 小于
     * 如: lt("a", 1) 等价于 a < 1
     */
    @Override
    public <X extends Comparable<? super X>> Child lt(R field, X val) {
        this.specification = getSpecification(SpecificationFactory.createSpec(Handler.LT, columnToString(field), val));
        return typedThis;
    }

    /**
     * 小于或等于
     * 如: le("a", 1) 等价于 a <= 1
     */
    @Override
    public <X extends Comparable<? super X>> Child le(R field, X val) {
        this.specification = getSpecification(SpecificationFactory.createSpec(Handler.LE, columnToString(field), val));
        return typedThis;
    }

    /**
     * 全模糊查询
     * 如: allLike("a", "1") 等价于 a like "%1%"
     */
    @Override
    public Child allLike(R field, String val) {
        this.specification = getSpecification(SpecificationFactory.createSpec(Handler.ALL_LIKE, columnToString(field), val));
        return typedThis;
    }

    /**
     * 右模糊查询
     * allLike("a", "1") 等价于 a like "1%"
     */
    @Override
    public Child rightLike(R field, String val) {
        this.specification = getSpecification(SpecificationFactory.createSpec(Handler.RIGHT_LIKE, columnToString(field), val));
        return typedThis;
    }

    /**
     * 左模糊查询
     * allLike("a", "1") 等价于 a like "%1"
     */
    @Override
    public Child leftLike(R field, String val) {
        this.specification = getSpecification(SpecificationFactory.createSpec(Handler.LEFT_LIKE, columnToString(field), val));
        return typedThis;
    }

    /**
     * 字段为空
     * isNull("a") 等价于 a is null
     */
    @Override
    public Child isNull(R field) {
        this.specification = getSpecification(SpecificationFactory.createSpec(Handler.IS_NULL, columnToString(field),null));
        return typedThis;
    }

    /**
     * 字段不为空
     * notNull("a") 等价于 a is not null
     */
    @Override
    public Child isNotNull(R field) {
        this.specification = getSpecification(SpecificationFactory.createSpec(Handler.IS_NOT_NULL, columnToString(field), null));
        return typedThis;
    }

    /**
     * 在集合中存在
     * list = Arrays.asList(1,2)
     * in("a", list) 等价于 a in (1,2)
     */
    @Override
    public <X extends Comparable<? super X>> Child in(R field, Collection<X> collection) {
        this.specification = getSpecification(SpecificationFactory.createSpec(Handler.IN, columnToString(field), collection));
        return typedThis;
    }

    /**
     * 在集合中不存在
     * list = Arrays.asList(1,2)
     * notIn("a", list) 等价于 a not in (1,2)
     */
    @Override
    public <X extends Comparable<? super X>> Child notIn(R field, Collection<X> collection) {
        this.specification = getSpecification(SpecificationFactory.createSpec(Handler.NOT_IN, columnToString(field), collection));
        return typedThis;
    }

    /**
     * 在数组中存在
     * in("a", 1, 2) 等价于 a in (1,2)
     */
    @Override
    public <X extends Comparable<? super X>> Child in(R field, X... array) {
        if(array.length == 0){
            throw new IllegalArgumentException("Array should not be empty");
        }
        this.specification = getSpecification(SpecificationFactory.createSpec(Handler.IN, columnToString(field), array));
        return typedThis;
    }

    /**
     * 在数组中不存在
     * in("a", 1, 2) 等价于 a in (1,2)
     */
    @Override
    public <X extends Comparable<? super X>> Child notIn(R field, X... array) {
        if(array.length == 0){
            throw new IllegalArgumentException("Array should not be empty");
        }
        this.specification = getSpecification(SpecificationFactory.createSpec(Handler.NOT_IN, columnToString(field), array));
        return typedThis;
    }

    @Override
    public <X extends Comparable<? super X>> Child between(R field, X low, X up) {
        if(low != null || up != null){
            Object[] val = new Object[]{low,up};
            this.specification = getSpecification(SpecificationFactory.createSpec(Handler.BETWEEN, columnToString(field), val));
        }
        return typedThis;
    }

    @Override
    public <X extends Comparable<? super X>> Child notBetween(R field, X low, X up) {
        if(low != null || up != null){
            Object[] val = new Object[]{low,up};
            this.specification = getSpecification(SpecificationFactory.createSpec(Handler.NOT_BETWEEN, columnToString(field), val));
        }
        return typedThis;
    }

    private void mergeSpecification(Specification<T> s, Predicate.BooleanOperator operator) {
        if(s != null){
            this.specification = operator == Predicate.BooleanOperator.AND ? this.specification.and(s) :  this.specification.or(s) ;
        }
    }


    private Specification<T> getSpecification(Specification<T> spec) {
        mergeSpecification(spec, this.operator);
        return this.specification;
    }




}
