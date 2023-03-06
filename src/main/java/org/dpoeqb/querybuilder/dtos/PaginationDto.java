package org.dpoeqb.querybuilder.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class PaginationDto {

    private Integer pageSize = null;
    private Integer pageNumber = null;
    private List<SortPropertyDto> sortPropertyDtoList;
}
