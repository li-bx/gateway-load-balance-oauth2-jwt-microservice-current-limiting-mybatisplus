package com.hd.microsysservice.utils;

import com.hd.common.model.TokenInfo;
import com.hd.microsysservice.mapper.SyUserMapperCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @Author: liwei
 * @Description:
 */
@Component
public class UserCommonUtil {

    @Autowired
    SyUserMapperCommon syUserMapperCommon;
    @Autowired
    RedisTemplate redisTemplate;


    //    @Cacheable(value = "centerId2userId", key = "'centerUserId:'+#centerUserId", unless = "#result == null")
    @Cacheable(value = "centerId2userId", key = "#centerUserId", unless = "#result == null")
    public String getUserIdByCenterUserIdFromCach(Long centerUserId) {
        String userId = syUserMapperCommon.getUserIdByCenterUserId(centerUserId);
        return userId;
    }

    /**
     * 返回在线用户数目
     * @return
     */
    public Integer getOnLineUserCount() {
        Set<String> keys = redisTemplate.keys(String.format("edgeOut:%s", "*"));
        Integer onlineCount=0;
        for (String key : keys)
        {
            Boolean edgeOut=(Boolean)  redisTemplate.opsForValue().get(key);
            if(!edgeOut){
                onlineCount++;
            }
        }
        return  onlineCount;
    }
}
