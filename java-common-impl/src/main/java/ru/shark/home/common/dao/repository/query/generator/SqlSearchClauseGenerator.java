package ru.shark.home.common.dao.repository.query.generator;

import ru.shark.home.common.dao.common.RequestSearch;
import ru.shark.home.common.dao.repository.query.ParsedQuery;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.ObjectUtils.isEmpty;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.SEARCH_EQ_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.SEARCH_LIKE_TPL;
import static ru.shark.home.common.dao.repository.query.generator.GeneratorConstants.SIMPLE_SEARCH_LEFT;

@Component
public class SqlSearchClauseGenerator implements ClauseGenerator {
    @Override
    public String generate(QueryClauseRequest request) {
        ParsedQuery query = request.getParsedQuery();
        if ((isEmpty(query.getAdvancedSearchFields()) && isEmpty(query.getSearchFields())) || request.getRequestCriteria().getSearch() == null) {
            return "";
        }
        String searchClause = "";
        RequestSearch search = request.getRequestCriteria().getSearch();
        if (!isEmpty(query.getSearchFields())) {
            String searchTemplate = search.isEquals() ? SIMPLE_SEARCH_LEFT + SEARCH_EQ_TPL : SIMPLE_SEARCH_LEFT + SEARCH_LIKE_TPL;
            searchClause = query.getSearchFields().stream()
                    .map(field -> MessageFormat.format(searchTemplate, field, search.getValue()))
                    .collect(Collectors.joining(" or "));
        }
        if (!isEmpty(query.getAdvancedSearchFields())) {
            String searchTemplate = (search.isEquals() ? SEARCH_EQ_TPL : SEARCH_LIKE_TPL).replace("1", "0");
            String advancedSearchClause = query.getAdvancedSearchFields().stream()
                    .map(item -> MessageFormat.format(item, MessageFormat.format(searchTemplate, search.getValue())))
                    .collect(Collectors.joining(" or "));
            if (isBlank(searchClause)) {
                searchClause = advancedSearchClause;
            } else {
                searchClause += " or " + advancedSearchClause;
            }
        }

        return "(" + searchClause + ")";
    }

    @Override
    public boolean canHandle(QueryClauseType type, boolean isNative) {
        return QueryClauseType.SEARCH.equals(type) && isNative;
    }
}
