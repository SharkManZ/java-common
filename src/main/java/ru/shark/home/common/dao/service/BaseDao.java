package ru.shark.home.common.dao.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.shark.home.common.dao.entity.BaseEntity;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * Базовый класс для сервисов доступа к данным.
 */
@Transactional(Transactional.TxType.REQUIRED)
public abstract class BaseDao<E extends BaseEntity> {

    private EntityManager em;
    private final Class<E> entityClass;

    protected BaseDao(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Сохранение сущности.
     *
     * @param entity сущность для сохранения
     * @return сохраненная сущность
     */
    public E save(E entity) {
        return (E) em.merge(entity);
    }

    /**
     * Удаление сущности по идентификатору.
     */
    public void deleteById(Long id) {
        em.remove(findById(id));
    }

    /**
     * Удаление сущности.
     *
     * @param entity сущность для удаления
     */
    public void delete(E entity) {
        deleteById(entity.getId());
    }

    /**
     * Возвращает сущность по идентификатору
     *
     * @param id идентификатор
     * @return сущность
     */
    public E findById(Long id) {
        return em.find(entityClass, id);
    }

    /**
     * Возвращает класс сущности.
     */
    public Class<E> getEntityClass() {
        return entityClass;
    }

    @Autowired
    public void setEm(EntityManager em) {
        this.em = em;
    }
}
