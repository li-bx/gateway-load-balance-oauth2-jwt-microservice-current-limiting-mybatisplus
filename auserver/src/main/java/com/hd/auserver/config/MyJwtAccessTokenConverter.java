package com.hd.auserver.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.text.SimpleDateFormat;
import java.util.*;

public class MyJwtAccessTokenConverter extends JwtAccessTokenConverter {
    /**
     * 用户信息JWT加密
     */
    @Override
    protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;

        UserInfo user = null;
        if (authentication.getUserAuthentication() != null) {
            user = (UserInfo) authentication.getUserAuthentication().getPrincipal();
        }
        Map<String, Object> data = new HashMap<String, Object>();
        if (user == null) {
            //client 模式,添加个默认user：client_secret； token解码端据此判断是否是client登录
            List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
            //authList.add(new SimpleGrantedAuthority("ROLE_UNUSE"));
            //如果是客户端模式，特殊前置+该clentid组成用户名
            user = new UserInfo("lseewiixiweqoie23898hbbde0$$--" + authentication.getOAuth2Request().getClientId(), "", authList);
            data.put("user_name", user.getUsername());
            //客户端模式，不使用user scope，没有对应的user
            //注意不能直接token.getScope().remove("user")，这里获取的是个unmodified的set，不可以修改
            if (token.getScope().contains("user")) {
                Set<String> scope = token.getScope();
                Set<String> scopeNew=new HashSet<>(scope);
                scopeNew.remove("user");
                token.setScope(scopeNew);
            }
            String enterpriseId=authentication.getOAuth2Request().getRequestParameters().get("enterId");
            user.setEnterpriseId(enterpriseId);
            user.setDeviceType("web");
            user.setId("0000000000000000000");
        }
        //Set<String> tokenScope = token.getScope();
        //将额外的参数信息存入，用于生成token
        data.put("login_time", user.getLoginTime());
        data.put("enterprise_id", user.getEnterpriseId());
        data.put("deviceType",user.getDeviceType());
        data.put("id",user.getId());
        data.putAll(token.getAdditionalInformation());
        //自定义TOKEN包含的信息
        token.setAdditionalInformation(data);
        return super.encode(accessToken, authentication);
    }

    /**
     * 用户信息JWT
     */
    @Override
    protected Map<String, Object> decode(String token) {
        //解析请求当中的token  可以在解析后的map当中获取到上面加密的数据信息
        Map<String, Object> decode = super.decode(token);
        //先判断是否过期
        if (System.currentTimeMillis() > ((Long) decode.get("exp") * 1000)) {
            throw new InvalidTokenException("Invalid token (expired): " + token);
        }

        String userName = (String) decode.get("user_name");
        String loginTime = (String) decode.get("login_time");
        //refresh token时，需要更新当前登录时间
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        loginTime= f.format(new Date());

        String enterpriseId = (String) decode.get("enterprise_id");
        String id=(String) decode.get("id");
        String deviceType=(String) decode.get("deviceType");

        String clientId = (String) decode.get("client_id");
        List<String> scopes = new ArrayList<>();
        List<LinkedHashMap<String, String>> scopesArray = (List<LinkedHashMap<String, String>>) decode.get("scope");
        Iterator<LinkedHashMap<String, String>> it = scopesArray.iterator();
        while (it.hasNext()) {
            Object temp1 = it.next();
            String temp2 = (String) temp1;
            scopes.add(temp2);
        }

        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        if (decode.get("authorities") != null) {
            List<LinkedHashMap<String, String>> authorities = (List<LinkedHashMap<String, String>>) decode.get("authorities");
            it = authorities.iterator();
            while (it.hasNext()) {
                Object temp1 = it.next();
                String temp2 = (String) temp1;
                SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(temp2);
                grantedAuthorityList.add(grantedAuthority);
            }
        }


        UserInfo userInfo = new UserInfo(userName, "", grantedAuthorityList);
        userInfo.setLoginTime(loginTime);
        userInfo.setEnterpriseId(enterpriseId);
        userInfo.setId(id);
        userInfo.setDeviceType(deviceType);
        //需要将解析出来的用户存入全局当中，不然无法转换成自定义的user类
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userInfo, null, grantedAuthorityList);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //此处必须设置，encode时，会取出principle转化userinfo
        decode.put("user_name", userInfo);
        return decode;
    }

    public void decodeJwtToken(String token) {
        decode(token);
    }
}
