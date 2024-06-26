package com.hd.auserver.config;

import com.hd.auserver.entity.AccountEntity;
import com.hd.auserver.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    AccountService accountService;

    public MyUserDetailService(){

    }
    @Autowired
    private HttpServletRequest request; //自动注入request bean 代理模式

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserDetails userDetails = null;
        try {
            List<GrantedAuthority> authList = getAuthorities();
            String enterpriseId="";
            String deviceType="web";
            //TODO: 获取用户密码 ，模拟试验，所有用户密码都是1234；

            //注意此处使用动态代理获取request对象，然后获取当初的请求参数；这里主要获取企业id，进行sas模式的用户管理
            String []objGrantType = request.getParameterMap().get("grant_type");
            if(objGrantType!=null && ((String)(objGrantType[0])).compareTo("password")==0){
                //密码模式，从request url获取enterpriseId
                enterpriseId=(String)(request.getParameterMap().get("enterId")[0]);
                if(request.getParameterMap().get("deviceType")!=null){
                    deviceType=(String)(request.getParameterMap().get("deviceType")[0]);
                }
            }else {
                //授权码显式或隐式模式，从defaultSavedRequest，之前存储的
                HttpSession session = request.getSession(true);
                Object objSavedReq = session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
                if(objSavedReq!=null){
                    DefaultSavedRequest defaultSavedRequest=  (DefaultSavedRequest)objSavedReq;
                    enterpriseId = defaultSavedRequest.getParameterValues("enterId")[0];
                    if(defaultSavedRequest.getParameterValues("deviceType")!=null){
                        deviceType=(String)(defaultSavedRequest.getParameterValues("deviceType")[0]);
                    }
                }
            }
            if(!enterpriseId.isEmpty()){
                String finalEnterpriseId = enterpriseId;
                List<AccountEntity> accountEntityList = accountService.listByMap(new HashMap<String, Object>() {
                    {
                        put("account", userName);
                        put("enterprise", finalEnterpriseId);
                        put("delete_flag", 0);
                    }
                });
                if(accountEntityList.size()>0){
                        //authList角色这里不使用，只使用用户和当前scope进行判断permission
                    //userDetails = new UserInfo(userName, passwordEncoder.encode("1234"),authList);
                    userDetails = new UserInfo(userName, accountEntityList.get(0).getPassword(),authList);
                    ((UserInfo)userDetails).setEnterpriseId(enterpriseId);
                    ((UserInfo)userDetails).setDeviceType(deviceType);
                    ((UserInfo)userDetails).setId(accountEntityList.get(0).getId().toString());
                }
                else {
                    userDetails = new UserInfo("xxxxxx2xxxx34xxxxxxx$%$xxxxxx","xxxxxxxxxxxxx3$$%^%xxxxxxxxx#$xxxxxxxxxxxxxxxxxxxxxxxxxx",authList);
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
//        if(userDetails==null){
//                throw new UsernameNotFoundException("缺少公司标识!");
//        }
        return userDetails;
    }
//    增加前缀ROLE_，可以通过三种方式校验权限：
//    @PreAuthorize("hasRole('ADMIN')")                //允许
//    @PreAuthorize("hasRole('ROLE_ADMIN')")           //允许
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")      //允许
    private List<GrantedAuthority> getAuthorities(){
        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
        //authList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return authList;
    }
}
