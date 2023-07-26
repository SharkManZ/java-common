package ru.shark.home.common.dao.repository.query.generator;

import ru.shark.home.common.dao.repository.query.parts.HqlFromQueryPart;

import static org.apache.commons.lang3.StringUtils.isBlank;

public abstract class BaseHqlClauseGenerator {

    protected String transformField(HqlFromQueryPart fromPart, String field) {
        String prefix = isBlank(fromPart.getMainTableAlias()) ? "" : fromPart.getMainTableAlias() + ".";
        String transformedField = fromPart.transformFieldChain(field);
        if (!transformedField.equalsIgnoreCase(field)) {
            return transformedField;
        }
        return prefix + field;
    }
}
