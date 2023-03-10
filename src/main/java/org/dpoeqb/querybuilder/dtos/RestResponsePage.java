package org.dpoeqb.querybuilder.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class RestResponsePage<T> {

    private List<T> content;

    private Long totalElements;

    private Integer totalPages;

    private Boolean first;

    private Boolean last;

    private Boolean empty;

    public RestResponsePage(Page<T> page){
        this.content = page.getContent();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
    }
}
