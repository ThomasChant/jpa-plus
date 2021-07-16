package com.ct.wrapper;


import com.ct.bean.SuperBean;

/**
 * Wrapper
 *
 * @author chentao
 * @date 2021/7/6
 */
public class SpecificationWrapper<T extends SuperBean<Long>> extends AbstractWrapper<SpecificationWrapper<T>, String, T>{

    @Override
    protected SpecificationWrapper<T> instance() {
        return new SpecificationWrapper<>();
    }
}
