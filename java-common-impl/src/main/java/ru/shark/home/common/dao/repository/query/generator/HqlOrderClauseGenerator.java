package ru.shark.home.common.dao.repository.query.generator;

import org.springframework.stereotype.Component;
import ru.shark.home.common.dao.common.RequestSort;
import ru.shark.home.common.dao.repository.query.parts.HqlFromQueryPart;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Component
public class HqlOrderClauseGenerator extends BaseHqlClauseGenerator implements ClauseGenerator{
    @Override
    public String generate(QueryClauseRequest request) {
        List<RequestSort> sorts = request.getRequestCriteria().getSorts();
        if (isEmpty(sorts)) {
            return "";
        }

        return sorts.stream().map(item -> sortToClause((HqlFromQueryPart) request.getParsedQuery().getFromPart(), item)).collect(Collectors.joining(", "));
    }

    private String sortToClause(HqlFromQueryPart fromPart, RequestSort sort) {
        return transformField(fromPart, sort.getField()) + (sort.getDirection() == null ? " asc" : " " + sort.getDirection().name().toLowerCase());
    }

    @Override
    public boolean canHandle(QueryClauseType type, boolean isNative) {
        return QueryClauseType.ORDER.equals(type) && !isNative;
    }
}
