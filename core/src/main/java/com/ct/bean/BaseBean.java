package com.ct.bean;


import javax.persistence.MappedSuperclass;

import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public class BaseBean<K extends Serializable> extends SuperBean<K> {

    protected Date createTime;

    protected K createUserId;

    protected String createUserName;

    protected Date updateTime;

    protected K updateUserId;

    protected String updateUserName;

    protected String createIp;

    protected String updateIp;

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public K getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(K createUserId) {
        this.createUserId = createUserId;
    }

    public K getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(K updateUserId) {
        this.updateUserId = updateUserId;
    }

    public String getCreateIp() {
        return createIp;
    }

    public void setCreateIp(String createIp) {
        this.createIp = createIp;
    }

    public String getUpdateIp() {
        return updateIp;
    }

    public void setUpdateIp(String updateIp) {
        this.updateIp = updateIp;
    }
}
