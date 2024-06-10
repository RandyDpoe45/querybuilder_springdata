package org.dpoeqb.querybuilder.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class OperationDto {
    private List<AggregationOperation> selectAttributes;
    private QueryDto baseQuery;
    private QueryDto aggregationQuery;
    private Integer pageSize = null;
    private Integer pageNumber = null;
}
