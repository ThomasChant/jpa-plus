package com.ct.condition.core;

import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Wrapper
 *
 * @author chentao
 * @date 2021/7/8
 */
public interface Condition<Child extends Condition<Child,R,T>, R, T> {
    Specification<T> toSpec();

    Child and(Consumer<Child> consumer);

    Child or(Consumer<Child> consumer);

    Child or();

    Child and();

    <X extends Comparable<? super X>> Child ge(R field, X val);

    <X extends Comparable<? super X>> Child eq(R field, X val);

    <X extends Comparable<? super X>> Child notEq(R field, X val);

    <X extends Comparable<? super X>> Child gt(R field, X val);

    <X extends Comparable<? super X>> Child lt(R field, X val);

    <X extends Comparable<? super X>> Child le(R field, X val);

    Child allLike(R field, String val);

    Child rightLike(R field, String val);

    Child leftLike(R field, String val);

    Child isNull(R field);

    Child isNotNull(R field);

    <X extends Comparable<? super X>> Child in(R field, Collection<X> collection);

    <X extends Comparable<? super X>> Child notIn(R field, Collection<X> collection);

    <X extends Comparable<? super X>> Child in(R field, X... array);

    <X extends Comparable<? super X>> Child notIn(R field, X... array);

    <X extends Comparable<? super X>> Child between(R field, X a, X b);

    <X extends Comparable<? super X>> Child notBetween(R field, X low, X up);
}
