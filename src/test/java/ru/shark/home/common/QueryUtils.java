package ru.shark.home.common;

import org.hibernate.engine.query.spi.EntityGraphQueryHint;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
import org.hibernate.hql.spi.QueryTranslator;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QueryUtils {
    public static ASTQueryTranslatorFactory prepareTranslatorFactory(String query) {
        ASTQueryTranslatorFactory factory = mock(ASTQueryTranslatorFactory.class);
        QueryTranslator queryTranslator = mock(QueryTranslator.class);
        when(queryTranslator.getSQLString()).thenReturn(query);
        when(factory.createQueryTranslator(anyString(), anyString(), anyMap(), any(SessionFactoryImplementor.class), nullable(EntityGraphQueryHint.class)))
                .thenReturn(queryTranslator);
        return factory;
    }
}
