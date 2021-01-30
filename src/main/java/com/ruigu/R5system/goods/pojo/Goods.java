package com.ruigu.R5system.goods.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
/*
* 实体类：*/
@Entity
@Table(name = "tb_goods")
public class Goods {
    @Id
    private String id;

}
