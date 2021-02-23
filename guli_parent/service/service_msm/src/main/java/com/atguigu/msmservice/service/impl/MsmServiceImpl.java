package com.atguigu.msmservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.utils.StringUtils;
import com.atguigu.msmservice.service.MsmService;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MsmServiceImpl implements MsmService {

    //发送手机短信验证码
    @Override
    public boolean send(String phone,String random){
        //System.out.println(33333);
        CCPRestSmsSDK sdk=new CCPRestSmsSDK();
        //生产环境请求地址：app.cloopen.com    请求端口
        sdk.init( "app.cloopen.com" , "8883");
        //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
        sdk.setAccount("8a216da8759c87cc0175acebec1704c2","f8947d45261b41469f04e98fce588232");
        //请使用管理控制台中已创建应用的APPID
        sdk.setAppId("8a216da8759c87cc0175acebedd704c9");
        //生产环境请求地址：app.cloopen.com
        /*String serverIp = "app.cloopen.com";
        //请求端口
        String serverPort = "8883";
        //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
        String accountSId = "8aaf0708759c7fce0175d56a5acb124b";
        String accountToken = "75357554e3a14545933abf130cddb15b";
        //请使用管理控制台中已创建应用的APPID
        String appId = "8aaf0708759c7fce0175d56a5be01252";

        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);*/
        /*sdk.setBodyTyp(BodyType.Type_JSON);*/
        String templateId= "1";        //模板ID
        String[] datas = {random,"5"};    //验证码，持续几分钟
        HashMap<String, Object> result = sdk.sendTemplateSMS(phone,templateId,datas);
        //System.out.println(result);
        if("000000".equals(result.get("statusCode"))){
            return true;
        }else{
            return false;
        }

    }

    //发送短信
    /*@Override
    public boolean send(Map<String, Object> param, String phone) {
        if(StringUtils.isEmpty(phone)) return false;

        DefaultProfile profile =
                DefaultProfile.getProfile("default", "LTAI4FvvVEWiTJ3GNJJqJnk7", "9st82dv7EvFk9mTjYO1XXbM632fRbG");
        IAcsClient client = new DefaultAcsClient(profile);

        //设置相关固定参数
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        //设置发送相关的参数
        request.putQueryParameter("PhoneNumbers",phone);     //手机号
        request.putQueryParameter("SignName","我的谷粒在线教育网站");      //申请的阿里云 签名名称
        request.putQueryParameter("TemplateCode","SMS_210077865");      //申请的阿里云 模板code
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));    //验证码转换成json格式传递

        try {
            //最终发送
            CommonResponse response = client.getCommonResponse(request);
            boolean success = response.getHttpResponse().isSuccess();
            return success;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }*/



}
