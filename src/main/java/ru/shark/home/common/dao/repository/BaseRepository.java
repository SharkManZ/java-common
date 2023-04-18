package ru.shark.home.common.dao.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.shark.home.common.dao.common.PageableList;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.entity.BaseEntity;

@NoRepositoryBean
public interface BaseRepository<E extends BaseEntity> extends PagingAndSortingRepository<E, Long>, JpaSpecificationExecutor<E> {

    /**
     * Возвращает пагинированный список сущностей по переданными критериям.
     *
     * @param request параметры выборки
     * @return пагинированный список
     */
    PageableList<E> getWithPagination(RequestCriteria request);

    /**
     * Возвращает пагинированный список сущностей по переданными критериям и условию поиска.
     *
     * @param request             параметры выборки
     * @param searchSpecification спецификация для поиска
     * @return пагинированный список
     */
    PageableList<E> getWithPagination(RequestCriteria request, Specification searchSpecification);

    /**
     * Возвращает пагинированный список сущностей по переданными критериям, условию поиска и сортировке по-умолчанию
     * (если не задана в критериях).
     *
     * @param request             параметры выборки
     * @param searchSpecification спецификация для поиска
     * @param defaultSort         сортировка по-умолчанию
     * @return пагинированный список
     */
    PageableList<E> getWithPagination(RequestCriteria request, Specification searchSpecification, String... defaultSort);
}
