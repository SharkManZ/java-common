package ru.shark.home.common.dao.repository.query;

import ru.shark.home.common.dao.repository.query.parts.FromQueryPart;

import java.util.List;

public class ParsedQuery {
    private String selectPart;
    private FromQueryPart fromPart;
    private String wherePart;
    private String groupPart;
    private String orderPart;
    private List<String> searchFields;
    private List<String> advancedSearchFields;

    public ParsedQuery(List<String> searchFields, List<String> advancedSearchFields) {
        this.searchFields = searchFields;
        this.advancedSearchFields = advancedSearchFields;
    }

    public String getSelectPart() {
        return selectPart;
    }

    public void setSelectPart(String selectPart) {
        this.selectPart = selectPart;
    }

    public FromQueryPart getFromPart() {
        return fromPart;
    }

    public void setFromPart(FromQueryPart fromPart) {
        this.fromPart = fromPart;
    }

    public String getWherePart() {
        return wherePart;
    }

    public void setWherePart(String wherePart) {
        this.wherePart = wherePart;
    }

    public String getGroupPart() {
        return groupPart;
    }

    public void setGroupPart(String groupPart) {
        this.groupPart = groupPart;
    }

    public String getOrderPart() {
        return orderPart;
    }

    public void setOrderPart(String orderPart) {
        this.orderPart = orderPart;
    }

    public List<String> getSearchFields() {
        return searchFields;
    }

    public List<String> getAdvancedSearchFields() {
        return advancedSearchFields;
    }
}
