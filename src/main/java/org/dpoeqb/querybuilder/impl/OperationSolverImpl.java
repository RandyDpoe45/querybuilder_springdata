package org.dpoeqb.querybuilder.impl;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.dpoeqb.querybuilder.dtos.AggregationOperation;
import org.dpoeqb.querybuilder.dtos.OperationDto;
import org.dpoeqb.querybuilder.dtos.QueryConstantsEnum;
import org.dpoeqb.querybuilder.interfaces.IOperationSolver;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class OperationSolverImpl implements IOperationSolver {

    private final EntityManager em;

    public OperationSolverImpl(EntityManager entityManager) {
        this.em = entityManager;
    }

    @Override
    public <T> List<?> solveQueryOperations(Class<T> type, Specification<T> spec, OperationDto operationDto) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<?> query = cb.createQuery();
        Root<T> root = query.from(type);
        List<AggregationOperation> selectAttributes = operationDto.getSelectAttributes();
        List<Expression<?>> selectExpressions = buildSelect(query, root, cb, operationDto.getSelectAttributes());
        List<Expression<?>> groupByAttributes = new ArrayList<>();
        List<Expression<?>> orderByAttributes = new ArrayList<>();
        List<String> orderDirections = new ArrayList<>();
        for (int i = 0; i < selectAttributes.size(); i++) {
            if (selectAttributes.get(i).getGroupByThis())
                groupByAttributes.add(selectExpressions.get(i));
            if (!Objects.isNull(selectAttributes.get(i).getOrderDirection())) {
                orderByAttributes.add(selectExpressions.get(i));
                orderDirections.add(selectAttributes.get(i).getOrderDirection());
            }
        }
        if (Objects.nonNull(spec))
            query.where(spec.toPredicate(root, query, cb));
        if (!groupByAttributes.isEmpty())
            query.groupBy(groupByAttributes);
        if (!orderByAttributes.isEmpty())
            query.orderBy(buildSorting(orderByAttributes, orderDirections, cb));

        TypedQuery<?> typedQuery = em.createQuery(query);
        if (Objects.nonNull(operationDto.getPageSize()) && Objects.nonNull(operationDto.getPageNumber())) {
            typedQuery.setMaxResults(operationDto.getPageSize());
            typedQuery.setFirstResult(operationDto.getPageSize() * operationDto.getPageNumber());
        }
        return typedQuery.getResultList();
    }

    private List<Expression<?>> buildSelect(
            CriteriaQuery<?> query,
            Root<?> root,
            CriteriaBuilder cb,
            List<AggregationOperation> aggregationOperations
    ) {
        List<Expression<?>> expressions = new ArrayList<>();
        for (AggregationOperation aggregationOperation : aggregationOperations) {
            expressions.add(buildOperation(root, cb, aggregationOperation));
        }
        query.multiselect(expressions.toArray(new Expression[0]));
        return expressions;
    }

    private Expression<?> buildOperation(Root<?> root, CriteriaBuilder cb, AggregationOperation aggregationOperation) {
        //sum, avg, diff, prod, quot, count
        Expression<?> basePath = buildPath(root, aggregationOperation.getAttribute(), cb);
        if (aggregationOperation.getOperation().equals(QueryConstantsEnum.OPERATION_COUNT.getValue())) {
            return cb.count(basePath);
        } else if (aggregationOperation.getOperation().equals(QueryConstantsEnum.OPERATION_COUNT_DISC.getValue())) {
            return cb.countDistinct(basePath);
        } else if (aggregationOperation.getOperation().equals(QueryConstantsEnum.OPERATION_SELECT.getValue())) {
            return basePath;
        } else if (!aggregationOperation.getOperation().equals(QueryConstantsEnum.OPERATION_SELECT.getValue())) {
            Expression<Number> path = (Expression<Number>) basePath;
            if (aggregationOperation.getOperation().equals(QueryConstantsEnum.OPERATION_SUM.getValue())) {
                return cb.sum(path.as(BigDecimal.class));
            } else if (aggregationOperation.getOperation().equals(QueryConstantsEnum.OPERATION_AVG.getValue())) {
                return cb.avg(path.as(BigDecimal.class));
            } else if (aggregationOperation.getOperation().equals(QueryConstantsEnum.OPERATION_MAX.getValue())) {
                return cb.max(path);
            } else if (aggregationOperation.getOperation().equals(QueryConstantsEnum.OPERATION_MIN.getValue())) {
                return cb.min(path);
            }
        }
        throw new RuntimeException("Unsupported operation " + aggregationOperation.getOperation());
    }

    private Expression<?> buildPath(Root<?> root, String property, CriteriaBuilder cb) {
        Path<?> p = root;
        int index = 0;
        String[] tokens = property.split("\\.");
        while (index < tokens.length) {
            if (tokens[index].startsWith("j#")) {
                List<Expression<?>> expressions = new ArrayList<>();
                expressions.add(p.get(tokens[index].substring(2)));
                index++;
                while (index < tokens.length) {
                    expressions.add(cb.literal(tokens[index]));
                    index++;
                }
                return cb.function(
                        "jsonb_extract_path_text",
                        String.class,
                        expressions.toArray(new Expression[0])
                );
            } else if (Collection.class.isAssignableFrom(p.get(tokens[index]).getJavaType())) {
                p = ((From<?, ?>) p).join(tokens[index]);
                index++;
            } else {
                p = p.get(tokens[index]);
                index++;
            }
        }
        return p;
    }

    protected List<Order> buildSorting(List<Expression<?>> sortPropertyList, List<String> sortDirections, CriteriaBuilder cb) {
        List<Order> orders = new ArrayList<>();
        if (sortPropertyList == null || sortPropertyList.isEmpty()) {
            return orders;
        }
        for (int i = 0; i < sortPropertyList.size(); i++) {
            Order order = sortDirections.get(i).equals(QueryConstantsEnum.ASC_SORT_DIRECTION.getValue()) ?
                    cb.asc(sortPropertyList.get(i))
                    : cb.desc(sortPropertyList.get(i));
            orders.add(order);
        }
        return orders;
    }

}
