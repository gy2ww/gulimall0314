package com.gy.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.gy.api.bean.UmsMember;
import com.gy.api.bean.UmsMemberReceiveAddress;
import com.gy.api.service.userService;
import com.gy.mapper.userMapper;
import com.gy.mapper.UserReceiveAddressMapper;
import com.gy.util.MD5Util;
import com.gy.util.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;
import javax.annotation.Resource;
import java.util.List;

/**
 * Created by gaoyong on 2020/3/15.
 */
@Service
public class userServiceImpl implements userService {

    private static final String KEY="user:";
    private static final String PWD=":password";
    private static final String TOKEN = ":token";
    @Resource
    private userMapper userMapper;
    @Resource
    private UserReceiveAddressMapper userReceiveAddressMapper;

    @Resource
    private RedisUtils redisUtils;
    @Override
    public List<UmsMember> getAllUser() {
       List<UmsMember> umsMemberList = null; // userMapper.queryAllUser();
        umsMemberList = userMapper.selectAll();
        return umsMemberList;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(Long memberId) {

        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo(memberId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses =  userReceiveAddressMapper.selectByExample(example);

        return umsMemberReceiveAddresses;
    }

    @Override
    public int modifyUserAddressDetail(Long id) {
        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("id",id);
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setProvince("河北省");
        umsMemberReceiveAddress.setCity("衡水市");
        umsMemberReceiveAddress.setName("高大帅");
        umsMemberReceiveAddress.setRegion("桃城区");
        umsMemberReceiveAddress.setDetailAddress("北方工业园");
        int i = userReceiveAddressMapper.updateByExampleSelective(umsMemberReceiveAddress, example);
        return i;
    }

    @Override
    public UmsMember loginCheck(UmsMember umsMember) {

        Jedis jedis = null;
        try{
            jedis = redisUtils.getJedis();
            if(null!=jedis){
                if(StringUtils.isNotBlank(umsMember.getUsername()) && StringUtils.isNotBlank(umsMember.getPassword())){
                    String password = MD5Util.md5Encrypt32Lower(umsMember.getPassword());
                    String userInfo = jedis.get(KEY + umsMember.getUsername() + password + PWD);
                    if(StringUtils.isNotBlank(userInfo)){
                        UmsMember umsMemberFormCaChe = JSON.parseObject(userInfo, UmsMember.class);
                        return umsMemberFormCaChe;
                    }
                }
            }
            if(StringUtils.isNotBlank(umsMember.getPassword())){
                //jedis没有连接成功或者redis中没有要查询的信息,就从数据库查询
                String passwd = MD5Util.md5Encrypt32Lower(umsMember.getPassword());
                umsMember.setPassword(passwd);
                UmsMember umsMemberInfo = userMapper.selectOne(umsMember);
                if(null != umsMemberInfo && jedis != null){
                    //存入缓存
                    String umsMemberFormDb = JSON.toJSONString(umsMemberInfo);
                    jedis.setex(KEY+umsMemberInfo.getUsername()+passwd+PWD,60*60*24,umsMemberFormDb);

                }
                //返回前端
                return umsMemberInfo;
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return null;
    }

    @Override
    public void pushCache(Long id, String token) {

        Jedis jedis = redisUtils.getJedis();

        if(null != jedis){
            //先删除再存储
            jedis.del(KEY+id+TOKEN);
            jedis.setex(KEY+id+TOKEN,60*60,token);
        }
    }

    @Override
    public int insertUserInfo(UmsMember umsMember) {
        int i=0;
        if(null!=umsMember){
            i = userMapper.insertSelective(umsMember);
        }
        return i;
    }

    @Override
    public UmsMember getUserInfo(UmsMember umsMember1) {
        UmsMember umsMember = userMapper.selectOne(umsMember1);
        return umsMember;
    }
}
