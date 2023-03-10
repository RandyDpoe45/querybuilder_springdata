package org.dpoeqb.querybuilder.impl;


import org.dpoeqb.querybuilder.dtos.OperationDto;
import org.dpoeqb.querybuilder.dtos.SortPropertyDto;
import org.dpoeqb.querybuilder.interfaces.IOperationSolver;
import org.springframework.data.jpa.domain.Specification;
import org.dpoeqb.querybuilder.dtos.OperationResultDto;
import org.dpoeqb.querybuilder.dtos.QueryConstantsEnum;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OperationSolverImpl implements IOperationSolver {

    private final EntityManager em;

    private final Class<?> type;

    public OperationSolverImpl(Class<?> type, EntityManager entityManager) {
        this.type = type;
        this.em = entityManager;
    }

    @Override
    public List<OperationResultDto> solveQueryOperations(Specification<Class<?>> spec, List<OperationDto> operationDtoList) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        List<OperationResultDto> result = new ArrayList<>();
        for (OperationDto operationDto : operationDtoList) {
            CriteriaQuery query = builder.createQuery();
            Root<Class<?>> root = query.from(this.type);
            Expression<Number> path = (Expression<Number>) buildPath(root, "id");
            if (spec != null) {
                Predicate predicate = spec.toPredicate(root, query, builder);
                if (predicate != null)
                    query.where(predicate);
            }
            List<Order> orders = buildSorting(operationDto.getPaginationDto().getSortPropertyDtoList(), root, builder);
            List<Expression> labels = buildGroupBy(operationDto, root);

            if(operationDto.getOrderOperation() != null){
                Order order = operationDto.getOrderOperation().equals(QueryConstantsEnum.ASC_SORT_DIRECTION.getValue()) ?
                        builder.asc(builder.literal(orders.size() + 1)) :
                        builder.desc(builder.literal(orders.size() + 1)) ;
                orders.add(order);
            }
            query = buildSelect(query, root, operationDto, builder);
            query.orderBy(orders);
            if(!labels.isEmpty())
                query.groupBy(labels);

            TypedQuery tq = em.createQuery(query);
            if(operationDto.getPaginationDto().getPageNumber() != null && operationDto.getPaginationDto().getPageSize() != null){
                tq.setMaxResults(operationDto.getPaginationDto().getPageSize());
                tq.setFirstResult(operationDto.getPaginationDto().getPageSize() * operationDto.getPaginationDto().getPageNumber());
            }

            Object res = tq.getResultList();
            result.add(new OperationResultDto(operationDto.getOperation(), operationDto.getAttribute(), res));
        }
        return result;
    }

    private List<Expression> buildGroupBy(OperationDto operationDto, Root<Class<?>> root){
        List<Expression> labels = new ArrayList<>();
        if(operationDto.getLabelList() == null || operationDto.getLabelList().isEmpty())
            return labels;
        for(String label: operationDto.getLabelList()){
            labels.add(buildPath(root,label));
        }
        return labels;
    }

    private CriteriaQuery buildSelect(CriteriaQuery query, Root<Class<?>> root, OperationDto operationDto, CriteriaBuilder cb1) {
        if (operationDto.getLabelList() == null || operationDto.getLabelList().isEmpty())
            return query.select(buildOperation(root,cb1,operationDto));
        List<Selection> selections = new ArrayList<>();
        for (String label : operationDto.getLabelList()) {
            selections.add(buildPath(root, label));
        }
        selections.add(buildOperation(root,cb1,operationDto));
        return query.multiselect(selections);
    }

    private Expression<?> buildOperation(Root<Class<?>> root, CriteriaBuilder cb, OperationDto operationDto){
        //sum, avg, diff, prod, quot, count
        if(!operationDto.getOperation().equals(QueryConstantsEnum.OPERATION_COUNT.getValue())){
            Expression<Number> path = (Expression<Number>) buildPath(root, operationDto.getAttribute());
            if(operationDto.getOperation().equals(QueryConstantsEnum.OPERATION_SUM.getValue())){
                return cb.sum(path.as(BigDecimal.class));
            }else if(operationDto.getOperation().equals(QueryConstantsEnum.OPERATION_AVG.getValue())){
                return cb.avg(path.as(BigDecimal.class));
            }else if(operationDto.getOperation().equals(QueryConstantsEnum.OPERATION_DIFF.getValue())){
                return cb.diff(path, operationDto.getAuxValue());
            }else if(operationDto.getOperation().equals(QueryConstantsEnum.OPERATION_PROD.getValue())){
                return cb.prod(path.as(BigDecimal.class), operationDto.getAuxValue());
            }else if(operationDto.getOperation().equals(QueryConstantsEnum.OPERATION_QUOT.getValue())){
                return cb.quot(path, operationDto.getAuxValue()).as(BigDecimal.class);
            }else if(operationDto.getOperation().equals(QueryConstantsEnum.OPERATION_MAX.getValue())){
                return cb.max(path);
            }else if(operationDto.getOperation().equals(QueryConstantsEnum.OPERATION_MIN.getValue())){
                return cb.min(path);
            }
        }
        return cb.countDistinct(buildPath(root, operationDto.getAttribute()));
    }

    private Path<?> buildPath(Root<?> root, String property) {
        Path<?> p = root;
        for (String s : property.split("\\.")) {
            if (Collection.class.isAssignableFrom(p.get(s).getJavaType())) {
                p = ((From) p).join(s);
            } else {
                p = p.get(s);
            }
        }
        return p;
    }

    protected List<Order> buildSorting(List<SortPropertyDto> sortPropertyDtoList, Root<?> root, CriteriaBuilder cb) {
        List<Order> orders = new ArrayList<>();
        if (sortPropertyDtoList == null || sortPropertyDtoList.isEmpty()) {
            return orders;
        }
        for (SortPropertyDto dto : sortPropertyDtoList) {
            Expression<?> p = buildPath(root, dto.getSortProperty());
            Order order = dto.getSortDirection().equals(QueryConstantsEnum.ASC_SORT_DIRECTION.getValue()) ?
                    cb.asc(p) : cb.desc(p);
            orders.add(order);
        }
        return orders;
    }

}
