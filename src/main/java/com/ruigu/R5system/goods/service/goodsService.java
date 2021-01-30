package com.ruigu.R5system.goods.service;

import com.ruigu.R5system.goods.mapper.googsMapper;
import com.ruigu.R5system.goods.pojo.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class goodsService {
    @Autowired
    private googsMapper googsMapper;


    public void select(int cid) {
        int id=cid;
        Goods goods=googsMapper.selcetGooodsById(id);




    }

    
}
