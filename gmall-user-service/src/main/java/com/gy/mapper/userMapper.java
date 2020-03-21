package com.gy.mapper;


import com.gy.api.bean.UmsMember;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created by gaoyong on 2020/3/15.
 */
public interface userMapper extends Mapper<UmsMember> {

    List<UmsMember> queryAllUser();
}
