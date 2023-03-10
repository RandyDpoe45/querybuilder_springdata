package org.dpoeqb.querybuilder.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class QueryPaginationDto {

    private QueryDto queryDto;

    private PaginationDto paginationDto;

}
