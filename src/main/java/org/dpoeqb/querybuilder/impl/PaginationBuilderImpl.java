/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dpoeqb.querybuilder.impl;


import org.dpoeqb.querybuilder.dtos.SortPropertyDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.dpoeqb.querybuilder.dtos.PaginationDto;
import org.dpoeqb.querybuilder.dtos.QueryConstantsEnum;
import org.dpoeqb.querybuilder.interfaces.IPaginationBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author randy
 */
public class PaginationBuilderImpl implements IPaginationBuilder {

    @Override
    public Pageable createPagination(PaginationDto paginationDto) {
        if (paginationDto == null)
            return Pageable.unpaged();

        if (paginationDto.getPageNumber() != null && paginationDto.getPageSize() != null) {
            if (paginationDto.getPageSize() < 1)
                return Pageable.unpaged();

            if (paginationDto.getSortPropertyDtoList() != null && !paginationDto.getSortPropertyDtoList().isEmpty()) {
                List<Sort.Order> orders = new ArrayList<>();
                for(SortPropertyDto dto : paginationDto.getSortPropertyDtoList()){
                    Direction x = dto.getSortDirection().equals(QueryConstantsEnum.DESC_SORT_DIRECTION.getValue()) ?
                            Direction.DESC : Direction.ASC;
                    orders.add(new Sort.Order(x, dto.getSortProperty()));
                }
                return PageRequest.of(
                        paginationDto.getPageNumber(),
                        paginationDto.getPageSize(),
                        Sort.by(orders)
                );
            }
            return PageRequest.of(paginationDto.getPageNumber(), paginationDto.getPageSize());
        }
        return Pageable.unpaged();
    }

}
