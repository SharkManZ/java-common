package ru.shark.home.common.dao.util;

import ru.shark.home.common.dao.common.PageableList;
import ru.shark.home.common.dao.entity.BaseEntity;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shark.home.common.dao.dto.Dto;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * Класс для преобразоваия сущностей и dto друг в друга.
 */
@Component
public class ConverterUtil {
    private Mapper mapper;

    /**
     * Преобразование списка сущностей в список DTO.
     *
     * @param entityList список сущностей.
     * @param dtoClass   класс DTO
     * @return список dto
     */
    public <D extends Dto, E extends BaseEntity> List<D> entityListToDtoList(List<E> entityList, Class<D> dtoClass) {
        if (isEmpty(entityList)) {
            return new ArrayList<>();
        }

        List<D> dtoList = new ArrayList<>();
        for (E entity : entityList) {
            dtoList.add(mapper.map(entity, dtoClass));
        }

        return dtoList;
    }

    /**
     * Преобразует пагинированный список сущностей в пагинированный список dto
     *
     * @param entityList список сущностей
     * @param dtoClass   класс DTO
     * @return список dto
     */
    public <D extends Dto, E extends BaseEntity> PageableList<D> entityListToDtoPageableList(PageableList<E> entityList,
                                                                                             Class<D> dtoClass) {
        if (isEmpty(entityList) || isEmpty(entityList.getData())) {
            return new PageableList<>(new ArrayList<>(), 0L);
        }

        List<D> dtoList = new ArrayList<>();
        for (E entity : entityList.getData()) {
            dtoList.add(mapper.map(entity, dtoClass));
        }

        return new PageableList<>(dtoList, entityList.getTotalCount());
    }

    /**
     * Преобразует список dto в список сущностей.
     *
     * @param dtoList     список dto
     * @param entityClass класс сущности
     * @return спислк сущностей
     */
    public <D extends Dto, E extends BaseEntity> List<E> dtoListToEntityList(List<D> dtoList, Class<E> entityClass) {
        if (isEmpty(dtoList)) {
            return new ArrayList<>();
        }

        List<E> entityList = new ArrayList<>();
        for (D dto : dtoList) {
            entityList.add(mapper.map(dto, entityClass));
        }

        return entityList;
    }

    /**
     * Преобразует сущность в dto
     *
     * @param entity   сущность
     * @param dtoClass класс dto
     * @return dto
     */
    public <D extends Dto, E extends BaseEntity> D entityToDto(E entity, Class<D> dtoClass) {
        if (entity == null) {
            return null;
        }

        return mapper.map(entity, dtoClass);
    }

    /**
     * Преобразует dto в сущность.
     *
     * @param dto         dto
     * @param entityClass класс сущности
     */
    public <D extends Dto, E extends BaseEntity> E dtoToEntity(D dto, Class<E> entityClass) {
        if (dto == null) {
            return null;
        }

        return mapper.map(dto, entityClass);
    }

    @Autowired
    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }
}