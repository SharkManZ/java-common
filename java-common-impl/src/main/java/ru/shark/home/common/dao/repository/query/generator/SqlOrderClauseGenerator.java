package ru.shark.home.common.dao.repository.query.generator;

import org.springframework.stereotype.Component;
import ru.shark.home.common.dao.common.RequestSort;

import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Component
public class SqlOrderClauseGenerator implements ClauseGenerator {
    @Override
    public String generate(QueryClauseRequest request) {
        if (isEmpty(request.getRequestCriteria().getSorts())) {
            return "";
        }

        return request.getRequestCriteria().getSorts().stream().map(this::sortToClause).collect(Collectors.joining(", "));
    }

    private String sortToClause(RequestSort sort) {
        return sort.getField() + (sort.getDirection() == null ? " asc" : " " + sort.getDirection().name().toLowerCase());
    }

    @Override
    public boolean canHandle(QueryClauseType type, boolean isNative) {
        return QueryClauseType.ORDER.equals(type) && isNative;
    }
}
