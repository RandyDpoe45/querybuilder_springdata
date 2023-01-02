package org.wesdom.querybuilder.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class QueryDto {

    private boolean distinct = false;
    private List<QueryDtoPart> queryDtoPartList;
}
