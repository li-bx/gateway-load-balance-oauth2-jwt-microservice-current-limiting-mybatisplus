package com.hd.gateway.conf;

/**
 * @Author: liwei
 */

import com.alibaba.fastjson.JSON;
import com.hd.common.RetResult;
import com.hd.common.model.TokenInfo;
import com.hd.gateway.utils.JwtUtils;
import com.hd.gateway.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;


//全局过滤器，实现GlobalFilter接口，和Ordered接口即可。
@Component
@Slf4j
public class CheckTokenGlobalGatewayFilter implements GlobalFilter, Ordered {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RedisTemplate redisTemplate;


    @Value("${config.session-timeout}")
    int sessionTimeout;

    //TODO: 通过网关访问swagger时，关闭权限检查，开发阶段使用
    @Value("${config.check-token}")
    boolean checkPermission=true;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String uri=exchange.getRequest().getPath().value();
        String microName=uri.substring(1,uri.indexOf("/",1));
        if(!checkPermission||microName.compareTo("auserver")==0){
            return chain.filter(exchange);
        }
        List<String> authorization = exchange.getRequest().getHeaders().get("Authorization");
        if(authorization==null || authorization.size()==0){
            return ResponseUtil.makeJsonResponse(exchange.getResponse(),
                    new RetResult(HttpStatus.UNAUTHORIZED.value(), "没有登录令牌!", null));
        }
        String bearerTk = authorization.get(0);
        //TODO:  判断token 登录
        TokenInfo tokenInfo;
        try {
            tokenInfo = jwtUtils.decodeToken(bearerTk.replace("Bearer ", ""));
            //url第一个分段一遍用作服务名识别，此处去掉
            tokenInfo.setUri(uri.substring(uri.indexOf("/",1)));
            tokenInfo.setMethod(exchange.getRequest().getMethodValue().toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.makeJsonResponse(exchange.getResponse(),
                    new RetResult(HttpStatus.UNAUTHORIZED.value(), "登录校验失败!", null));
        }
        //TODO: 根据应用，判断token超时
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //先判断本地缓存是否存在此token
             Object redisObj = redisTemplate.opsForValue().get(String.format("token:%s",bearerTk.replace("Bearer ", "")));

            Boolean tokenExist=(redisObj!=null);

            if(tokenExist){
                //更新token tll
                redisTemplate.expire(String.format("token:%s",bearerTk.replace("Bearer ", "")),sessionTimeout*60, TimeUnit.SECONDS);
            }
            else {
                Long mSec = System.currentTimeMillis() - sdf.parse(tokenInfo.getLoginTime()).getTime();
                //20分钟
                if (mSec > (60000 * sessionTimeout)) {
                    return ResponseUtil.makeJsonResponse(exchange.getResponse(),
                            new RetResult(HttpStatus.UNAUTHORIZED.value(), "登录校验失败!", null));
                }
                //保存token
                redisTemplate.opsForValue().set(String.format("token:%s",bearerTk.replace("Bearer ", "")),tokenInfo, sessionTimeout*60,TimeUnit.SECONDS);
            }


        } catch (ParseException e) {
            e.printStackTrace();
            return ResponseUtil.makeJsonResponse(exchange.getResponse(),
                    new RetResult(HttpStatus.UNAUTHORIZED.value(), "登录校验失败!", null));
        }

        ServerHttpRequest request = exchange.getRequest().mutate().header("token-info", JSON.toJSONString(tokenInfo)).header("Authorization","").build();
        ServerWebExchange buildExchange = exchange.mutate().request(request).build();

        //TODO: 记录访问日志
        return chain.filter(buildExchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 300;
    }
}