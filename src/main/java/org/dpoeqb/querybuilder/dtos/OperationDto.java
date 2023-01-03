package org.dpoeqb.querybuilder.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class OperationDto {
    private String operation; //sum, avg, diff, prod, quout
    private String attribute;
    private BigDecimal auxValue = BigDecimal.ONE;
    private String orderOperation = null;
    private List<String> labelList;
    private PaginationDto paginationDto;
}
