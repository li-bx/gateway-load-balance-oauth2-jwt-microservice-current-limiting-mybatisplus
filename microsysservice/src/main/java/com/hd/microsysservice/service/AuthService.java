package com.hd.microsysservice.service;

import com.hd.common.model.TokenInfo;

public interface AuthService {
    String auth(TokenInfo tokenInfo);
}
