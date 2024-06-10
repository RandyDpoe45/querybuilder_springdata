package org.dpoeqb.querybuilder.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AggregationOperation {
    private String attribute;
    private String operation;
    private Boolean groupByThis;
    private String orderDirection;
}
