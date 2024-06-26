package com.hd.gateway.conf;

/**
 * @Author: liwei
 */

import com.alibaba.fastjson.JSON;
import com.hd.common.RetResult;
import com.hd.common.model.TokenInfo;
import com.hd.gateway.service.MicroSysService;
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

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


//全局过滤器，实现GlobalFilter接口，和Ordered接口即可。
@Component
@Slf4j
public class CheckAuthGlobalGatewayFilter implements GlobalFilter, Ordered {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    MicroSysService microSysService;

//    @Value("${config.auth-uri}")
//    String authUri;

    //TODO: 通过网关访问swagger时，关闭权限检查，开发阶段使用
    @Value("${config.check-permission}")
    boolean checkPermission=true;

    @Value("${config.session-timeout}")
    int sessionTimeout;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String uri=exchange.getRequest().getPath().value();
        String microName=uri.substring(1,uri.indexOf("/",1));
        if(!checkPermission||microName.compareTo("auserver")==0){
            return chain.filter(exchange);
        }
        //TODO: PERMISSION 判断
        //获取header的参数
        String tokenInfoJson = exchange.getRequest().getHeaders().getFirst("token-info");
        TokenInfo tokenInfo= JSON.parseObject(tokenInfoJson,TokenInfo.class);

        String user = tokenInfo.getAccount();
        //HttpUtil callService = GatewayApplication.applicationContext.getBean(HttpUtil.class);

        RetResult retResult;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("account", tokenInfo.getAccount());
            params.put("scopes", tokenInfo.getScopes());
            params.put("uri", tokenInfo.getUri());
            params.put("method", tokenInfo.getMethod());
            params.put("enterId", tokenInfo.getEnterpriseId());
            //retResult=new RetResult(0,"",true);
            //retResult= HttpUtil.httpPost(authUri+"/sys/authbridge?account={account}&scopes={scopes}&uri={uri}&method={method}&enterId={enterId}",params);
            retResult = microSysService.authbridge(tokenInfo);

        } catch (Exception e) {
            retResult = new RetResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), "-1");
        }

        boolean permitted=false;
        Long userId=-1L;
        Long orgId=-1L;
        if(retResult.getCode()==HttpStatus.OK.value() && retResult.getData()!=null){
            String returnValue= (String) retResult.getData();
            if(returnValue.compareTo("-1")!=0){
                userId=Long.parseLong(returnValue.split(":")[0]);
                orgId=Long.parseLong(returnValue.split(":")[1]);
                permitted=true;
            }
        }

        if (!permitted)  {
            //TODO: 记录拒绝访问日志
            retResult = new RetResult(HttpStatus.FORBIDDEN.value(), "授权异常!"+retResult.getMsg(), null);
            return ResponseUtil.makeJsonResponse(exchange.getResponse(), retResult);
        }
        else {
            //uri权限验证成功，并且是第一次登录，才保存token
            if(CheckTokenGlobalGatewayFilter.toSaveToken.get()){
                //把tokeninfo中center user id改成业务系统的user id
                tokenInfo.setId(userId.toString());
                tokenInfo.setOrgId(orgId.toString());
                List<String> authorization = exchange.getRequest().getHeaders().get("Authorization");
                String bearerTk = authorization.get(0);
                redisTemplate.opsForValue().set(String.format("token:%s",bearerTk.replace("Bearer ", "")),tokenInfo, sessionTimeout*60,TimeUnit.SECONDS);
                redisTemplate.opsForValue().set(String.format("edgeOut:%s",bearerTk.replace("Bearer ", "")),false,  (sessionTimeout+5)*60, TimeUnit.SECONDS);
            }
        }
        //TODO: 记录访问日志
        //处理中文账号
        try {
            tokenInfo.setAccount(URLEncoder.encode(tokenInfo.getAccount(),"UTF-8") );
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ServerHttpRequest request = exchange.getRequest().mutate().header("token-info", JSON.toJSONString(tokenInfo)).build();
        ServerWebExchange buildExchange = exchange.mutate().request(request).build();
        return chain.filter(buildExchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 301;
    }
}