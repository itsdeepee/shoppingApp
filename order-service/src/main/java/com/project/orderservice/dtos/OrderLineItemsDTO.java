package com.project.orderservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderLineItemsDTO {
    private String skuCode;
    private BigDecimal price;
    private  Integer quantity;
}
