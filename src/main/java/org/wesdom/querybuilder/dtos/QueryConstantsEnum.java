/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wesdom.querybuilder.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author randy
 */
@Getter
@AllArgsConstructor
public enum QueryConstantsEnum {
    CONDITION_EQUAL("eq"),
    CONDITION_GREATER_EQUAL("geq"),
    CONDITION_GREATER("ge"),
    CONDITION_LESSER("le"),
    CONDITION_LESSER_EQUAL("leq"),
    CONDITION_BETWEEN("between"),
    IN("in"),
    DESC_SORT_DIRECTION("desc"),
    ASC_SORT_DIRECTION("asc"),
    OPERATION_COUNT("count"),
    OPERATION_SUM("sum"),
    OPERATION_AVG("avg"),
    OPERATION_DIFF("diff"),
    OPERATION_PROD("prod"),
    OPERATION_QUOT("quot"),
    OPERATION_MAX("max"),
    OPERATION_MIN("min");
    
    private final String value;
    
}
