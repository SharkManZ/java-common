package ru.shark.home.common.dao.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import ru.shark.home.common.dao.service.HqlQueryService;
import ru.shark.home.common.dao.service.SqlQueryService;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * Бин для создания фабрики репозиториев с инжектированным сервисом построения запросов.
 */
public class BaseRepositoryFactoryBean<T extends JpaRepository<S, ID>, S, ID extends Serializable> extends JpaRepositoryFactoryBean<T, S, ID> {
    @Autowired
    private HqlQueryService hqlQueryService;
    @Autowired
    private SqlQueryService sqlQueryService;

    /**
     * Creates a new {@link JpaRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public BaseRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new BaseJpaRepositoryFactory(entityManager, hqlQueryService, sqlQueryService);
    }
}
