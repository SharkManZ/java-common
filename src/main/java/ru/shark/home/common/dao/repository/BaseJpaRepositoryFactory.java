package ru.shark.home.common.dao.repository;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import ru.shark.home.common.dao.service.HqlQueryService;
import ru.shark.home.common.dao.service.SqlQueryService;

import javax.persistence.EntityManager;

/**
 * Базовая фабрика для JPA репозиториев. Нужна для того чтобы в создаваемые репозитории можно было подать
 * сервис построения запросов.
 */
public class BaseJpaRepositoryFactory extends JpaRepositoryFactory {
    private HqlQueryService hqlQueryService;
    private SqlQueryService sqlQueryService;
    private EntityManager entityManager;

    /**
     * Creates a new {@link JpaRepositoryFactory}.
     *
     * @param entityManager must not be {@literal null}
     */
    public BaseJpaRepositoryFactory(EntityManager entityManager, HqlQueryService hqlQueryService,
                                    SqlQueryService sqlQueryService) {
        super(entityManager);
        this.entityManager = entityManager;
        this.hqlQueryService = hqlQueryService;
        this.sqlQueryService = sqlQueryService;
    }

    @Override
    protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        return new JpaBaseRepository(getEntityInformation(information.getDomainType()), entityManager, hqlQueryService, sqlQueryService);
    }
}
