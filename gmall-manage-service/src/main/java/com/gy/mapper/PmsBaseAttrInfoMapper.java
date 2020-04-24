package com.gy.mapper;


import com.gy.api.bean.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created by gaoyong on 2020/3/23.
 */
public interface PmsBaseAttrInfoMapper extends Mapper<PmsBaseAttrInfo>{


    /**
     * 根据valueId筛选sku对应的平台属性集合
     * @param valueIdStr
     * @return
     */
    List<PmsBaseAttrInfo> selectAttrValueListByValueId(@Param("valueIdStr") String valueIdStr);
}
