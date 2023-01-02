package org.wesdom.querybuilder.interfaces;

import org.springframework.data.jpa.domain.Specification;
import org.wesdom.querybuilder.dtos.OperationDto;
import org.wesdom.querybuilder.dtos.OperationResultDto;

import java.util.List;

public interface IOperationSolver{

    List<OperationResultDto> solveQueryOperations(Specification<Class<?>> spec, List<OperationDto> operationDtoList);
}
