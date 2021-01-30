package com.ruigu.R5system.goods.pojo;

import lombok.Data;

@Data
public class ResponseModel {

    private int code;
    private Object data;
    private String errMsg;
    private String message;
    private String msg;
    private Boolean result;
}
