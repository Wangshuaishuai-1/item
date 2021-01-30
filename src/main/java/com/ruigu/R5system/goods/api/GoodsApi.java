package com.ruigu.R5system.goods.api;

import com.ruigu.R5system.goods.pojo.Goods;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

interface GoodsApi {

    @GetMapping("/{id}")
    public void Selectggods(@PathVariable int id, @RequestBody Goods goods);
}
