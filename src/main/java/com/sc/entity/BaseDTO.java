package com.sc.entity;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class BaseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    public int count=10;//每页显示行数
    public int page=0;//当前页数
    private String total;//总数
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }



    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
