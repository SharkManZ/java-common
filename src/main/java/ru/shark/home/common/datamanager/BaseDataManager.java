package ru.shark.home.common.datamanager;

import org.springframework.beans.factory.annotation.Autowired;
import ru.shark.home.common.dao.dto.Dto;
import ru.shark.home.common.dao.entity.BaseEntity;
import ru.shark.home.common.dao.service.BaseDao;
import ru.shark.home.common.dao.util.ConverterUtil;

import java.util.List;

/**
 * Базовый класс для промежуточного слоя между dao и хранением данных.
 */
public abstract class BaseDataManager<E extends BaseEntity, D extends Dto> {
    private ConverterUtil converterUtil;
    private final BaseDao<E> dao;
    private final Class<D> dtoClass;

    protected <S extends BaseDao<E>> BaseDataManager(S dao, Class<D> dtoClass) {
        this.dao = dao;
        this.dtoClass = dtoClass;
    }

    /**
     * Сохраняет сущность.
     *
     * @param dto данные для сохранения.
     * @return сохраненный данные
     */
    public D save(D dto) {
        return entityToDto(dao.save(dtoToEntity(dto)));
    }

    /**
     * Удаляем сущность по идентификатору.
     *
     * @param id идентификатор сущности
     */
    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    /**
     * Возвращает сущность по идентификатору.
     *
     * @param id идентификатор
     * @return сущность
     */
    public D findById(Long id) {
        return entityToDto(dao.findById(id));
    }

    /**
     * Возвращает список сущностей
     *
     * @return список сущностей
     */
    public List<D> findALl() {
        return converterUtil.entityListToDtoList(dao.findAll(), dtoClass);
    }

    protected D entityToDto(E entity) {
        return converterUtil.entityToDto(entity, dtoClass);
    }

    protected E dtoToEntity(D dto) {
        return converterUtil.dtoToEntity(dto, dao.getEntityClass());
    }

    protected BaseDao<E> getDao() {
        return dao;
    }

    protected ConverterUtil getConverterUtil() {
        return converterUtil;
    }

    @Autowired
    public void setConverterUtil(ConverterUtil converterUtil) {
        this.converterUtil = converterUtil;
    }
}
