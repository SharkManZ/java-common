package ru.shark.home.common.dao.repository;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import ru.shark.home.common.dao.service.QueryService;

import javax.persistence.EntityManager;

/**
 * Базовая фабрика для JPA репозиториев. Нужна для того чтобы в создаваемые репозитории можно было подать
 * сервис построения запросов.
 */
public class BaseJpaRepositoryFactory extends JpaRepositoryFactory {
    private QueryService queryService;
    private EntityManager entityManager;

    /**
     * Creates a new {@link JpaRepositoryFactory}.
     *
     * @param entityManager must not be {@literal null}
     */
    public BaseJpaRepositoryFactory(EntityManager entityManager, QueryService queryService) {
        super(entityManager);
        this.entityManager = entityManager;
        this.queryService = queryService;
    }

    @Override
    protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        return new JpaBaseRepository(getEntityInformation(information.getDomainType()), entityManager, queryService);
    }
}
