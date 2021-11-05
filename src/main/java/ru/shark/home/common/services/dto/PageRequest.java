package ru.shark.home.common.services.dto;

import java.util.List;

public class PageRequest {
    private int page;
    private int size;
    private List<ru.shark.home.common.services.dto.Filter> filters;
    private String search;
    private List<ru.shark.home.common.services.dto.Sort> sorts;

    public PageRequest() {
        // empty constructor
    }

    public PageRequest(int page, int size) {
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

    public List<ru.shark.home.common.services.dto.Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<ru.shark.home.common.services.dto.Filter> filters) {
        this.filters = filters;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public List<ru.shark.home.common.services.dto.Sort> getSorts() {
        return sorts;
    }

    public void setSorts(List<ru.shark.home.common.services.dto.Sort> sorts) {
        this.sorts = sorts;
    }
}
