package org.dpoeqb.querybuilder.interfaces;

import org.dpoeqb.querybuilder.dtos.OperationDto;
import org.springframework.data.jpa.domain.Specification;
import org.dpoeqb.querybuilder.dtos.OperationResultDto;

import java.util.List;

public interface IOperationSolver{

    List<OperationResultDto> solveQueryOperations(Specification<Class<?>> spec, List<OperationDto> operationDtoList);
}
