package ru.shark.home.common.dao.repository.query.generator;

import ru.shark.home.common.common.ErrorConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import java.text.MessageFormat;
import java.util.List;

@Component
public class QueryClauseGenerator {

    private List<ClauseGenerator> generators;

    public String generate(QueryClauseType type, boolean isNative, QueryClauseRequest request) {
        return generators.stream()
                .filter(item -> item.canHandle(type, isNative))
                .findFirst()
                .orElseThrow(() -> new ValidationException(MessageFormat.format(ErrorConstants.QUERY_CLAUSE_GENERATOR_NOT_FOUND,
                        isNative ? "нативный" : "" + type.name())))
                .generate(request);
    }

    @Autowired
    public void setGenerators(List<ClauseGenerator> generators) {
        this.generators = generators;
    }
}
