package com.ruigu.R5system.goods.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruigu.R5system.goods.pojo.Goods;
import io.jsonwebtoken.Jwts;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController("")
public class GoodsController {

    /** 
    * @Description: GoodsController 
    * @Param: [id, goods] 
    * @return: org.springframework.http.ResponseEntity<com.ruigu.R5system.goods.pojo.Goods> 
    * @Author: Mr.Wang 
    * @Date: 2020/7/17 
    */ 
    @GetMapping("/{id}")
    public ResponseEntity<Goods> Selectggods(@PathVariable int id, @RequestBody Goods goods){

        Goods goods1=goods;
        Long data=System.currentTimeMillis();
        Date data1=new Date(data);
        String token= String.valueOf(Jwts.builder().setIssuedAt(data1).setSubject(goods1.getId()));
        String useToken=Jwts.parser().parseClaimsJws(token).getSignature();
        Page<Goods> goods2 = PageHelper.startPage(1, 5);
        goods2.getPages();
        PageInfo<Goods> objects = new PageInfo<>(goods2);

        return ResponseEntity.status(200).varyBy("","").body(goods1);

    }
}
