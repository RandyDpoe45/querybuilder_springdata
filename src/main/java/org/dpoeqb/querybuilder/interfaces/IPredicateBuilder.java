/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dpoeqb.querybuilder.interfaces;

import org.springframework.data.jpa.domain.Specification;
import org.dpoeqb.querybuilder.dtos.QueryDto;

/**
 *
 * @author randy
 */
public interface IPredicateBuilder<T> {
    
    Specification<T> createPredicate(QueryDto queryDto);
}
