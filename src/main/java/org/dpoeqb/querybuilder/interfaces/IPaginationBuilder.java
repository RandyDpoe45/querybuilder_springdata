/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dpoeqb.querybuilder.interfaces;

import org.dpoeqb.querybuilder.dtos.PaginationDto;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author randy
 */
public interface IPaginationBuilder {
    
    Pageable createPagination(PaginationDto paginationDto);
}
