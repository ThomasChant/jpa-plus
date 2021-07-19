package com.ct.condition.core;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author chentao
 */
@SuppressWarnings("unchecked")
final class SpecificationFactory {

    /**
     * 创建Specification 静态工厂方法
     * @param handler
     * @param fieldName
     * @param val
     * @param <T>
     * @return
     */
    public static <T> Specification<T> createSpec(Handler handler, String fieldName, Object val) {
        if (fieldName == null) {
            throw JpaMinusException.getException("fieldName cannot null");
        }
        if (val == null && handler != Handler.IS_NOT_NULL && handler != Handler.IS_NULL) {
            return null;
        }
        switch (handler) {
            case EQUAL:
                return (Specification<T>) (root, query, cb) -> {
                    Path path = root.get(fieldName);
                    return cb.equal(path, val);
                };
            case NOT_EQUAL:
                return (Specification<T>) (root, query, cb) -> {
                    Path path = root.get(fieldName);
                    return cb.equal(path, val).not();
                };
            case GE:
                return (Specification<T>) (root, query, cb) -> {
                    Path path = root.get(fieldName);
                    return cb.greaterThanOrEqualTo(path, (Comparable) val);
                };
            case GT:
                return (Specification<T>) (root, query, cb) -> {
                    Path path = root.get(fieldName);
                    return cb.greaterThan(path, (Comparable) val);
                };
            case LE:
                return (Specification<T>) (root, query, cb) -> {
                    Path path = root.get(fieldName);
                    return cb.lessThanOrEqualTo(path, (Comparable) val);
                };
            case LT:
                return (Specification<T>) (root, query, cb) -> {
                    Path path = root.get(fieldName);
                    return cb.lessThan(path, (Comparable) val);
                };
            case ALL_LIKE:
                return (Specification<T>) (root, query, cb) -> {
                    Path path = root.get(fieldName);
                    return cb.like(path, "%" + val + "%");
                };
            case LEFT_LIKE:
                return (Specification<T>) (root, query, cb) -> {
                    Path path = root.get(fieldName);
                    return cb.like(path, "%" + val);
                };
            case RIGHT_LIKE:
                return (Specification<T>) (root, query, cb) -> {
                    Path path = root.get(fieldName);
                    return cb.like(path, val + "%");
                };
            case IN: {
                return (Specification<T>) (root, query, cb) -> {
                    Path path = parseRootPath(root, fieldName);
                    if (val instanceof Collection) {
                        Collection notEmptyList = (List) ((Collection) val).stream()
                                .filter(Objects::nonNull).collect(Collectors.toList());
                        return path.in(notEmptyList);
                    } else {
                        throw JpaMinusException.getException("Value Must be Collection Instance!");
                    }
                };
            }
            case NOT_IN: {
                return (Specification<T>) (root, query, cb) -> {
                    Path path = parseRootPath(root, fieldName);
                    if (val instanceof Collection) {
                        Collection notEmptyList = (List) ((Collection) val).stream()
                                .filter(Objects::nonNull).collect(Collectors.toList());
                        return path.in(notEmptyList).not();
                    } else {
                        throw JpaMinusException.getException("Value Must be Collection Instance!");
                    }
                };
            }
            case IS_NOT_NULL:
                return (Specification<T>) (root, query, cb) -> {
                    Path path = root.get(fieldName);
                    return cb.isNotNull(path);
                };
            case IS_NULL:
                return (Specification<T>) (root, query, cb) -> {
                    Path path = root.get(fieldName);
                    return cb.isNull(path);
                };
            default:
                break;
        }
        return null;
    }

    public static <T> Path parseRootPath(Root<T> root, String attrName) {
        Path path = null;
        if (attrName.indexOf('.') != -1) {
            String[] attrArr = attrName.split("\\.");
            for (int i = 0; i <= attrArr.length - 1; i++) {
                if (path == null) {
                    path = root.get(attrArr[i]);
                } else {
                    path = path.get(attrArr[i]);
                }
            }
        } else {
            path = root.get(attrName);
        }
        return path;
    }


}


