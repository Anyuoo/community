package com.anyu.community.entity;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

/**
 * 用于分页的组件
 */
public class Page {
    //当前页码
    @Min(value = 1, message = "current >= 1")
    private int current = 1;
    //每页数据量
    @Range(max = 100, min = 10, message = "100>=limit=>10")
    private int limit = 10;
    //总数据
    @Min(value = 0, message = "row >= 0")
    private int rows;
    //查询路径(用于复用分页链接)
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页起始行
     *
     * @return 行数
     */
    public int getOffSet() {
        return (current - 1) * limit;
    }

    /**
     * 获取总的页数
     *
     * @return
     */
    public int getTotal() {
        if (rows % limit == 0)
            return rows / limit;
        else
            return rows / limit + 1;
    }

    /**
     * 获取起始页码
     *
     * @return
     */
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取终止页
     *
     * @return
     */
    public int getTo() {
        int to = current + 2;
        return to > getTotal() ? getTotal() : to;
    }
}
