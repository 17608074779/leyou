package com.leyou.search.pojo;


import java.util.Map;

public class SearchRequest {

    private String key;// 搜索条件

    private Integer page;// 当前页

    private Map<String, Object> filter;

    private static final Integer DEFAULT_SIZE = 20;// 每页大小，不从页面接收，而是固定大小
    private static final Integer DEFAULT_PAGE = 1;// 默认页

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Map<String, Object> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, Object> filter) {
        this.filter = filter;
    }

    public static Integer getDefaultSize() {
        return DEFAULT_SIZE;
    }

    public static Integer getDefaultPage() {
        return DEFAULT_PAGE;
    }
}
