package ru.shark.home.common.dao.common;

import java.util.List;

public class RequestCriteria {
    private int page;
    private int size;
    private List<RequestFilter> filters;
    private RequestSearch search;
    private List<RequestSort> sorts;

    public RequestCriteria(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<RequestFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<RequestFilter> filters) {
        this.filters = filters;
    }

    public RequestSearch getSearch() {
        return search;
    }

    public void setSearch(RequestSearch search) {
        this.search = search;
    }

    public List<RequestSort> getSorts() {
        return sorts;
    }

    public void setSorts(List<RequestSort> sorts) {
        this.sorts = sorts;
    }
}
