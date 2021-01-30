package com.ruigu.R5system.goods.mapper;

import com.ruigu.R5system.goods.pojo.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface googsMapper {
    @Select("selcect * from goods where id=#{cid}")
    public Goods selcetGooodsById(int cid);

    @Update("update goods set id=#{cid},name=#{}")
    public Goods updateGoodsByIdAndName(@Param("cid") int cid, @Param("name")String name);
}
