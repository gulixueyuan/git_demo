package com.atguigu.msmservice.service;

import java.util.Map;

public interface MsmService {
    //发送短信
    //boolean send(Map<String, Object> param, String phone);

    //发送手机短信验证码
    public boolean send(String phone,String random);
}
