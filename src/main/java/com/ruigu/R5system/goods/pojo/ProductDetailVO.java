package com.ruigu.R5system.goods.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class ProductDetailVO {
    @Id
    private int categoryId;
    private String categoryName;
    private String displayName;
    private String logo;
    private String model;
    private String prodCode;
    private String skuCode;
    private String unit;
    private int invoiceFlag;
    private int price;
}
