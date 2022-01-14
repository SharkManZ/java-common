package ru.shark.home.common.dao.util;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import ru.shark.home.common.dao.common.RequestSearch;
import ru.shark.home.common.dao.entity.BaseEntity;

import javax.persistence.criteria.*;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class SpecificationUtils {
    public static <T extends BaseEntity> Specification<T> andSpecifications(final Specification<T>... specifications) {
        if (specifications == null || specifications.length == 0) {
            return null;
        }
        Specification<T> specCombine = Specification.where(specifications[0]);
        if (specifications.length == 1) {
            return specCombine;
        }
        for (int i = 1; i < specifications.length; i++) {
            specCombine = specCombine.and(specifications[i]);
        }

        return specCombine;
    }

    public static <T extends BaseEntity> Specification<T> searchSpecification(RequestSearch search, String... attributes) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return new Specification<T>() {
                    @Override
                    public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder criteriaBuilder) {
                        if (search == null || isBlank(search.getValue()) || ObjectUtils.isEmpty(attributes)) {
                            return null;
                        }
                        Specification<BaseEntity> combineSpec = null;
                        for (int i = 0; i < attributes.length; i++) {
                            Specification<BaseEntity> spec = search.isEquals() ?
                                    equalAttribute(attributes[i], search.getValue()) :
                                    likeAttribute(attributes[i], search.getValue());
                            if (i == 0) {
                                combineSpec = Specification.where(spec);
                            } else {
                                combineSpec = combineSpec.or(spec);
                            }
                        }

                        return combineSpec.toPredicate(root, query, criteriaBuilder);
                    }
                }.toPredicate(root, query, criteriaBuilder);
            }
        };
    }

    public static <T extends BaseEntity> Specification<T> likeAttribute(String attribute, String value) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return new Specification<T>() {
                    @Override
                    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        if (isBlank(value)) {
                            return null;
                        }
                        Path path = null;
                        if (attribute.contains(".")) {
                            String[] attributeChain = attribute.split("\\.");
                            path = root.get(attributeChain[0]).get(attributeChain[1]);
                        } else {
                            path = root.get(attribute);
                        }

                        return criteriaBuilder.like(criteriaBuilder.lower(path), "%" + value.toLowerCase() + "%");
                    }
                }.toPredicate(root, query, criteriaBuilder);
            }
        };
    }

    public static <T extends BaseEntity> Specification<T> equalAttribute(String attribute, Object value) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return new Specification<T>() {
                    @Override
                    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        if (ObjectUtils.isEmpty(value)) {
                            return null;
                        }
                        Path path = null;
                        if (attribute.contains(".")) {
                            String[] attributeChain = attribute.split("\\.");
                            path = root.get(attributeChain[0]).get(attributeChain[1]);
                        } else {
                            path = root.get(attribute);
                        }

                        return criteriaBuilder.equal(path, value);
                    }
                }.toPredicate(root, query, criteriaBuilder);
            }
        };
    }
}
