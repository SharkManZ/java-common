package ru.shark.home.common.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import ru.shark.home.common.dao.common.PageableList;
import ru.shark.home.common.dao.common.RequestCriteria;
import ru.shark.home.common.dao.common.RequestSort;
import ru.shark.home.common.dao.entity.BaseEntity;
import ru.shark.home.common.dao.specification.SpecificationRequest;
import ru.shark.home.common.dao.util.SpecificationUtils;
import ru.shark.home.common.enums.SortDirection;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

public class JpaBaseRepository<E extends BaseEntity> extends SimpleJpaRepository<E, Long> implements BaseRepository<E> {

    public JpaBaseRepository(JpaEntityInformation<E, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    @Override
    public PageableList<E> getWithPagination(RequestCriteria request) {
        return getWithPagination(request, null);
    }

    @Override
    public PageableList<E> getWithPagination(RequestCriteria request, Specification searchSpecification) {
        return getWithPagination(request, searchSpecification, null);
    }

    @Override
    public PageableList<E> getWithPagination(RequestCriteria request, Specification searchSpecification, String... defaultSort) {
        Specification filterSpec = new SpecificationRequest(request);
        PageRequest pageRequest;
        Sort sort = getSortFromRequest(request.getSorts(), defaultSort);
        if (sort == null) {
            pageRequest = PageRequest.of(request.getPage(), request.getSize());
        } else {
            pageRequest = PageRequest.of(request.getPage(), request.getSize(), getSortFromRequest(request.getSorts(), defaultSort));
        }
        Page<E> all = findAll(SpecificationUtils.andSpecifications(filterSpec, searchSpecification), pageRequest);
        return new PageableList<>(all.getContent(), all.getTotalElements());
    }

    private Sort getSortFromRequest(List<RequestSort> sortList, String... defaultSort) {
        if (isEmpty(sortList) && isEmpty(defaultSort)) {
            return null;
        }
        List<Sort.Order> orders = !isEmpty(sortList) ? getOrdersByRequestSortList(sortList) :
                getOrdersByDefaultSort(defaultSort);

        return Sort.by(orders);
    }

    private List<Sort.Order> getOrdersByRequestSortList(List<RequestSort> sortList) {
        return sortList.stream()
                .map(item -> (item.getDirection() == null || SortDirection.ASC.equals(item.getDirection())) ?
                        Sort.Order.asc(item.getField()) : Sort.Order.desc(item.getField()))
                .collect(Collectors.toList());
    }

    private List<Sort.Order> getOrdersByDefaultSort(String... defaultSort) {
        List<Sort.Order> list = new ArrayList<>();
        for (String sort : defaultSort) {
            String[] sortChain = sort.split(" ");
            if (sortChain.length == 1) {
                list.add(Sort.Order.asc(sort));
            } else {
                SortDirection direction = SortDirection.valueOf(sortChain[1]);
                list.add(SortDirection.ASC.equals(direction) ? Sort.Order.asc(sortChain[0]) :
                        Sort.Order.desc(sortChain[0]));
            }
        }

        return list;
    }
}
