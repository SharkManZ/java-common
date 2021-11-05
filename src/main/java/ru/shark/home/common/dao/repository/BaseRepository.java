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
    PageableList<E> getWithPagination(RequestCriteria request);

    PageableList<E> getWithPagination(RequestCriteria request, Specification searchSpecification);

    PageableList<E> getWithPagination(RequestCriteria request, Specification searchSpecification, String... defaultSort);
}
