package org.dpoeqb.querybuilder.impl;


import org.dpoeqb.querybuilder.dtos.QueryConstantsEnum;
import org.dpoeqb.querybuilder.dtos.QueryDto;
import org.dpoeqb.querybuilder.dtos.QueryDtoPart;
import org.dpoeqb.querybuilder.interfaces.IPredicateBuilder;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author randy
 */
public class PredicateBuilderServiceImpl<T> implements IPredicateBuilder<T> {

    /**
     * Method to save dynamic predicates for query using specification jpa
     * API.
     *
     * @param queryDto
     */
    private static final String dateFormat = "dd/MM/yyyy";

    private static final String timeFormat = "HH:mm:ss";

    private static final String dateTimeFormat = "dd/MM/yyyy'T'HH:mm:ss";

    @Override
    public Specification<T> createPredicate(QueryDto queryDto) {
        boolean index = false;
        if (queryDto == null)
            return null;
        Specification<T> specifications = queryDto.isDistinct() ? Specification.where((root, query, builder) -> {
            query.distinct(true);
            return null;
        }) : null;
        for (QueryDtoPart part : queryDto.getQueryDtoPartList()) {
            if (specifications != null) {
                specifications = specifications.and(createSpecification(part));
            } else {
                specifications = Specification.where(createSpecification(part));
                index = true;
            }
        }

        return specifications;
    }

    /**
     * Specification builder.
     */
    private Specification<T> createSpecification(QueryDtoPart queryDtoPart) {
        if (queryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_EQUAL.getValue())) {
            return (root, query, builder) -> {
                Expression<T> path = buildPath(root, queryDtoPart.getAttribute());
                Predicate p;
                if(path.getJavaType().equals(LocalDateTime.class)){
                    if(queryDtoPart.isMultipleValues())
                        p = path.as(LocalDateTime.class).in((List<LocalDateTime>) processValue(path,queryDtoPart,true));
                    else
                        p = builder.equal(
                          path.as(LocalDateTime.class),
                          processValue(path,queryDtoPart,true)
                        );
                }else if (path.getJavaType().equals(LocalDate.class)) {
                    if (queryDtoPart.isMultipleValues())
                        p = path.as((LocalDate.class)).in((List<LocalDate>) processValue(path, queryDtoPart,true));
                    else
                        p = builder.equal(
                                path.as((LocalDate.class)),
                                processValue(path, queryDtoPart,true)
                        );
                } else if (path.getJavaType().equals(LocalTime.class)) {
                    if (queryDtoPart.isMultipleValues())
                        p = path.as((LocalTime.class)).in((List<LocalTime>) processValue(path, queryDtoPart,true));
                    else
                        p = builder.equal(
                                path.as((LocalTime.class)),
                                processValue(path, queryDtoPart,true)
                        );
                } else if (path.getJavaType().equals(Boolean.class)) {
                    if (queryDtoPart.isMultipleValues())
                        p = path.as((Boolean.class)).in((List<Boolean>) processValue(path, queryDtoPart,true));
                    else
                        p = builder.equal(
                                path.as((Boolean.class)),
                                processValue(path, queryDtoPart,true)
                        );
                } else if (path.getJavaType().equals(String.class)) {
                    if (queryDtoPart.isMultipleValues())
                        p = path.as((String.class)).in(processValue(path, queryDtoPart,true));
                    else
                        p = builder.like(
                                path.as((String.class)),
                                "%" + processValue(path, queryDtoPart,true) + "%"
                        );
                } else {
                    if (queryDtoPart.isMultipleValues())
                        p = path.as(BigDecimal.class).in((List<BigDecimal>) processValue(path, queryDtoPart,true));
                    else
                        p = builder.equal(
                                path.as((BigDecimal.class)),
                                processValue(path, queryDtoPart,true)
                        );
                }
                if (queryDtoPart.isNegate())
                    return builder.not(p);
                return p;
            };
        } else if (queryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_BETWEEN.getValue())) {
            return (root, query, builder) -> {
                Expression<T> path = buildPath(root, queryDtoPart.getAttribute());
                Predicate p;
                if(path.getJavaType().equals(LocalDateTime.class)){
                    p = builder.between(
                            path.as((LocalDateTime.class)),
                            (LocalDateTime) processValue(path,queryDtoPart,true),
                            (LocalDateTime) processValue(path,queryDtoPart,false)
                    );
                }else if (path.getJavaType().equals(LocalDate.class)) {
                    p = builder.between(
                            path.as((LocalDate.class)),
                            (LocalDate) processValue(path,queryDtoPart,true),
                            (LocalDate) processValue(path,queryDtoPart,false)
                    );
                } else if (path.getJavaType().equals(LocalTime.class)) {
                    p = builder.between(
                            path.as((LocalTime.class)),
                            (LocalTime) processValue(path,queryDtoPart,true),
                            (LocalTime) processValue(path,queryDtoPart,false)
                    );
                } else if (path.getJavaType().equals(String.class)) {
                    p = builder.between(
                            path.as((String.class)),
                            queryDtoPart.getValue(),
                            queryDtoPart.getValue2()
                    );
                } else {
                    p = builder.between(
                            path.as((BigDecimal.class)),
                            new BigDecimal(queryDtoPart.getValue()),
                            new BigDecimal(queryDtoPart.getValue2())
                    );
                }
                if (queryDtoPart.isNegate())
                    return builder.not(p);
                return p;
            };
        } else if (queryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_LESSER_EQUAL.getValue())) {
            return (root, query, builder) -> {
                Expression<T> path = buildPath(root, queryDtoPart.getAttribute());
                Predicate p;
                if (path.getJavaType().equals(LocalDateTime.class)) {
                    p = builder.lessThanOrEqualTo(
                            path.as((LocalDateTime.class)),
                            (LocalDateTime) processValue(path, queryDtoPart,true)
                    );
                }else if (path.getJavaType().equals(LocalDate.class)) {
                    p = builder.lessThanOrEqualTo(
                            path.as((LocalDate.class)),
                            (LocalDate) processValue(path, queryDtoPart, true)
                    );
                } else if (path.getJavaType().equals(LocalTime.class)) {
                    p = builder.lessThanOrEqualTo(
                            path.as((LocalTime.class)),
                            (LocalTime) processValue(path, queryDtoPart,true)
                    );
                } else if (path.getJavaType().equals(String.class)) {
                    p = builder.lessThanOrEqualTo(
                            path.as((String.class)),
                            queryDtoPart.getValue()
                    );
                } else {
                    p = builder.lessThanOrEqualTo(
                            path.as((BigDecimal.class)),
                            new BigDecimal(queryDtoPart.getValue())
                    );
                }
                if (queryDtoPart.isNegate())
                    return builder.not(p);
                return p;
            };
        } else if (queryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_LESSER.getValue())) {
            return (root, query, builder) -> {
                Expression<T> path = buildPath(root, queryDtoPart.getAttribute());
                Predicate p;
                if (path.getJavaType().equals(LocalDateTime.class)) {
                    p = builder.lessThan(
                            path.as((LocalDateTime.class)),
                            (LocalDateTime) processValue(path, queryDtoPart,true)
                    );
                }else if (path.getJavaType().equals(LocalDate.class)) {
                    p = builder.lessThan(
                            path.as((LocalDate.class)),
                            (LocalDate) processValue(path, queryDtoPart,true)
                    );
                } else if (path.getJavaType().equals(LocalTime.class)) {
                    p = builder.lessThan(
                            path.as((LocalTime.class)),
                            (LocalTime) processValue(path, queryDtoPart, true)
                    );
                } else if (path.getJavaType().equals(String.class)) {
                    p = builder.lessThan(
                            path.as((String.class)),
                            queryDtoPart.getValue()
                    );
                } else {
                    p = builder.lessThan(
                            path.as((BigDecimal.class)),
                            new BigDecimal(queryDtoPart.getValue())
                    );
                }
                if (queryDtoPart.isNegate())
                    return builder.not(p);
                return p;
            };
        } else if (queryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_GREATER_EQUAL.getValue())) {
            return (root, query, builder) -> {
                Expression<T> path = buildPath(root, queryDtoPart.getAttribute());
                Predicate p;
                if (path.getJavaType().equals(LocalDateTime.class)) {
                    p = builder.greaterThanOrEqualTo(
                            path.as((LocalDateTime.class)),
                            (LocalDateTime) processValue(path, queryDtoPart, true)
                    );
                }else if (path.getJavaType().equals(LocalDate.class)) {
                    p = builder.greaterThanOrEqualTo(
                            path.as((LocalDate.class)),
                            (LocalDate) processValue(path, queryDtoPart, true)
                    );
                } else if (path.getJavaType().equals(LocalTime.class)) {
                    p = builder.greaterThanOrEqualTo(
                            path.as((LocalTime.class)),
                            (LocalTime) processValue(path, queryDtoPart, true)
                    );
                } else if (path.getJavaType().equals(String.class)) {
                    p = builder.greaterThanOrEqualTo(
                            path.as((String.class)),
                            queryDtoPart.getValue()
                    );
                } else {
                    p = builder.greaterThanOrEqualTo(
                            path.as((BigDecimal.class)),
                            new BigDecimal(queryDtoPart.getValue())
                    );
                }
                if (queryDtoPart.isNegate())
                    return builder.not(p);
                return p;
            };
        } else if (queryDtoPart.getOperator().equals(QueryConstantsEnum.CONDITION_GREATER.getValue())) {
            return (root, query, builder) -> {
                Expression<T> path = buildPath(root, queryDtoPart.getAttribute());
                Predicate p;
                if (path.getJavaType().equals(LocalDateTime.class)) {
                    p = builder.greaterThan(
                            path.as((LocalDateTime.class)),
                            (LocalDateTime) processValue(path, queryDtoPart, true)
                    );
                }else if (path.getJavaType().equals(LocalDate.class)) {
                    p = builder.greaterThan(
                            path.as((LocalDate.class)),
                            (LocalDate) processValue(path, queryDtoPart, true)
                    );
                } else if (path.getJavaType().equals(LocalTime.class)) {
                    p = builder.greaterThan(
                            path.as((LocalTime.class)),
                            (LocalTime) processValue(path, queryDtoPart,true)
                    );
                } else if (path.getJavaType().equals(String.class)) {
                    p = builder.greaterThan(
                            path.as((String.class)),
                            queryDtoPart.getValue()
                    );
                } else {
                    p = builder.greaterThan(
                            path.as((BigDecimal.class)),
                            new BigDecimal(queryDtoPart.getValue())
                    );
                }
                if (queryDtoPart.isNegate())
                    return builder.not(p);
                return p;
            };
        }
        return null;
    }

    /**
     * Method to save Path to access nested properties in entity.
     */
    private Path<T> buildPath(Root<T> root, String property) {
        Path<T> p = root;
        for (String s : property.split("\\.")) {
            if (Collection.class.isAssignableFrom(p.get(s).getJavaType())) {
                p = ((From) p).join(s);
            } else {
                p = p.get(s);
            }
        }
        return p;
    }

    private Object processValue(
            Expression<T> property,
            QueryDtoPart queryDtoPart,
            Boolean firstValue
    ) {
        String value = firstValue ? queryDtoPart.getValue() : queryDtoPart.getValue2();
        if(property.getJavaType().equals(LocalDateTime.class)){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateTimeFormat);
            if(queryDtoPart.isMultipleValues())
                return Arrays.stream(value.split(queryDtoPart.getDelimiter()))
                        .map(x -> LocalDateTime.parse(x,dtf)).collect(Collectors.toList());
            else
                return LocalDateTime.parse(value,dtf);

        }else if (property.getJavaType().equals(LocalDate.class)) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateFormat);
            if (queryDtoPart.isMultipleValues()) {
                return Arrays.asList(value.split(queryDtoPart.getDelimiter()))
                        .stream().map(x ->
                                LocalDate.parse(x, dtf)
                        ).collect(Collectors.toList());
            } else {
                return LocalDate.parse(value, dtf);
            }
        } else if (property.getJavaType().equals(LocalTime.class)) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(timeFormat);
            if (queryDtoPart.isMultipleValues()) {
                return Arrays.stream(
                        value.split(queryDtoPart.getDelimiter())
                ).map(
                        x -> LocalTime.parse(x, dtf)
                ).collect(Collectors.toList());
            } else {
                return LocalTime.parse(value, dtf);
            }
        } else if (property.getJavaType().equals(Boolean.class)) {
            if (queryDtoPart.isMultipleValues()) {
                return Arrays.stream(
                        value.split(queryDtoPart.getDelimiter())
                ).map(
                        x -> Boolean.parseBoolean(x)
                ).collect(Collectors.toList());
            }
            return Boolean.parseBoolean(value);
        } else if (!property.getJavaType().equals(String.class)) {
            if (queryDtoPart.isMultipleValues()) {
                return Arrays.stream(
                                value.split(queryDtoPart.getDelimiter())
                        ).map(
                                x -> new BigDecimal(x)
                        ).collect(Collectors.toList());
            }
            return new BigDecimal(value);
        } else {
            if (queryDtoPart.isMultipleValues()) {
                return Arrays.asList(value.split(queryDtoPart.getDelimiter()));
            }
            return value;
        }
    }

}