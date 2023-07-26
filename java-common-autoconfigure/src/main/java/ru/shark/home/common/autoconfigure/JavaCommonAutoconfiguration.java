package ru.shark.home.common.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.shark.home.common.dao.repository.query.generator.HqlFilterClauseGenerator;
import ru.shark.home.common.dao.repository.query.generator.HqlOrderClauseGenerator;
import ru.shark.home.common.dao.repository.query.generator.HqlSearchClauseGenerator;
import ru.shark.home.common.dao.repository.query.generator.QueryClauseGenerator;
import ru.shark.home.common.dao.repository.query.generator.SqlFilterClauseGenerator;
import ru.shark.home.common.dao.repository.query.generator.SqlOrderClauseGenerator;
import ru.shark.home.common.dao.repository.query.generator.SqlSearchClauseGenerator;

@Configuration
public class JavaCommonAutoconfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SqlSearchClauseGenerator sqlSearchClauseGenerator() {
        return new SqlSearchClauseGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlOrderClauseGenerator sqlOrderClauseGenerator() {
        return new SqlOrderClauseGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlFilterClauseGenerator sqlFilterClauseGenerator() {
        return  new SqlFilterClauseGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public HqlSearchClauseGenerator hqlSearchClauseGenerator() {
        return new HqlSearchClauseGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public HqlOrderClauseGenerator hqlOrderClauseGenerator() {
        return new HqlOrderClauseGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public HqlFilterClauseGenerator hqlFilterClauseGenerator() {
        return new HqlFilterClauseGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public QueryClauseGenerator queryClauseGenerator() {
        return new QueryClauseGenerator();
    }
}
