package org.dpoeqb.querybuilder.interfaces;

import org.dpoeqb.querybuilder.dtos.OperationDto;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface IOperationSolver{

    <T> List<?> solveQueryOperations(Class<T> type, Specification<T> spec, OperationDto operationDtoList);
}
