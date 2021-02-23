package com.atguigu.educenter.controller;

import com.atguigu.commonutils.JwtUtils;
import com.atguigu.educenter.entity.UcenterMember;
import com.atguigu.educenter.service.UcenterMemberService;
import com.atguigu.educenter.utils.ConstantWxUtils;
import com.atguigu.educenter.utils.HttpClientUtils;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.google.gson.Gson;
import org.apache.catalina.startup.RealmRuleSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.util.HashMap;

@CrossOrigin
@Controller  //只是请求地址，不需要返回数据
@RequestMapping("/api/ucenter/wx")
public class WxApiController {

    @Autowired
    private UcenterMemberService memberService;

    //1 生成微信扫描二维码
    @GetMapping("login")
    public String getWxCode() {
        //固定地址，后面拼接参数
        //微信开发平台授权baseUrl   %s相当于？代表占位符
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";
        //对redirect_url进行URLEncoder编码
        String redirectUrl = ConstantWxUtils.WX_OPEN_REDIRECT_URL;
        try{
            redirectUrl = URLEncoder.encode(redirectUrl,"utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }

        //设置%s里面的值
        String url = String.format(
                baseUrl,
                ConstantWxUtils.WX_OPEN_APP_ID,
                ConstantWxUtils.WX_OPEN_REDIRECT_URL,
                "atguigu"
        );
        return "redirect:"+url;    //重定向到请求的微信地址中
    }

    //2 获取扫描二维码人的信息，添加数据
    @GetMapping("callback")
    public String callback(String code,String state){
        /*System.out.println("code:"+code);
        System.out.println("state:"+state);*/
        try {
            //(1) 获取code值，临时票据，它类似于验证码
            //(2) 拿着code请求固定的微信地址，得到两个值 access_token和openid
            String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                    "?appid=%s" +
                    "&secret=%s" +
                    "&code=%s" +
                    "&grant_type=authorization_code";
            //拼接三个参数：id  密钥  code值
            String accessTokenUrl = String.format(baseAccessTokenUrl,
                    ConstantWxUtils.WX_OPEN_APP_ID,
                    ConstantWxUtils.WX_OPEN_APP_SECRET,
                    code);
            //请求这个拼接好的地址，得到返回的两个值access_token和openid
            //使用httpclient发送请求，得到返回的结果
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);
            //System.out.println("accessTokenInfo"+accessTokenInfo);

            //从accessTokenInfo里面把access_token和openid取出来
            //把accessTokenInfo字符串转换成map集合，然后根据map集合的key获取对应的值
            //使用json转换工具Gson
            Gson gson = new Gson();
            HashMap map = gson.fromJson(accessTokenInfo,HashMap.class);
            String access_token = (String)map.get("access_token");
            String openid = (String)map.get("openid");

            //将扫码人信息添加进数据库里
            //判断数据库表中是否存在相同数据,根据openid查询判断
            UcenterMember member = memberService.getOpenIdMember(openid);
            if (member == null){
                //3 使用获取的access_token和openid给微信提供的固定地址再次发送请求，获取到扫描人的信息
                //访问微信的资源服务器，获取用户信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                //拼接二个参数：access_token openid
                String userInfoUrl = String.format(
                        baseUserInfoUrl,
                        access_token,
                        openid);
                //发送请求
                String userInfo = HttpClientUtils.get(userInfoUrl);
                //System.out.println("userInfo:"+userInfo);

                //获取返回的userInfo字符串扫描人的信息
                HashMap userInfoMap = gson.fromJson(userInfo,HashMap.class);
                String nickname = (String)userInfoMap.get("nickname");    //呢称
                String headimgurl = (String)userInfoMap.get("headimgurl");    //头像照片

                member = new UcenterMember();
                member.setOpenid(openid);
                member.setNickname(nickname);
                member.setAvatar(headimgurl);
                memberService.save(member);
            }
            //使用jwt根据member生成token字符串
            String jwtToken = JwtUtils.getJwtToken(member.getId(),member.getNickname());

            //最后返回首页面 通过路径传递token字符串
            return "redirect:http://localhost:3000?token="+jwtToken;
        }catch (Exception e){
            throw new GuliException(20001,"登录失败");
        }

    }

}
