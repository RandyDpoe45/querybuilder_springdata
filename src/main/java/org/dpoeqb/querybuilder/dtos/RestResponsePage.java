package org.dpoeqb.querybuilder.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class RestResponsePage<T> {

    private List<T> elements;

    private Long totalElements;

    private Integer totalPages;

}
