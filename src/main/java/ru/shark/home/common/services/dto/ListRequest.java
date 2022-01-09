package ru.shark.home.common.services.dto;

import java.util.List;

public class ListRequest {
    private List<Filter> filters;
    private Search search;
    private List<ru.shark.home.common.services.dto.Sort> sorts;

    public List<ru.shark.home.common.services.dto.Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<ru.shark.home.common.services.dto.Filter> filters) {
        this.filters = filters;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public List<ru.shark.home.common.services.dto.Sort> getSorts() {
        return sorts;
    }

    public void setSorts(List<ru.shark.home.common.services.dto.Sort> sorts) {
        this.sorts = sorts;
    }
}
