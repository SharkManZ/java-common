package ru.shark.home.common.dao.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.shark.home.common.dao.entity.BaseEntity;
import ru.shark.home.common.services.dto.Filter;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.ObjectUtils.isEmpty;

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

    /**
     * Возвращает фильтр из списка по ключу поля
     *
     * @param filters коллекция фильтров
     * @param field   фильтр
     * @return фильтр или null
     */
    protected Filter getFilterValueByField(List<Filter> filters, String field) {
        if (isEmpty(filters)) {
            return null;
        }

        return filters.stream().filter(item -> !isBlank(item.getField()) &&
                item.getField().equalsIgnoreCase(field)).findFirst().orElse(null);
    }

    @Autowired
    public void setEm(EntityManager em) {
        this.em = em;
    }
}
