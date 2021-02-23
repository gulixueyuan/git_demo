package com.atguigu.eduorder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.eduorder.entity.Order;
import com.atguigu.eduorder.entity.PayLog;
import com.atguigu.eduorder.mapper.PayLogMapper;
import com.atguigu.eduorder.service.OrderService;
import com.atguigu.eduorder.service.PayLogService;
import com.atguigu.eduorder.utils.HttpClient;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.wxpay.sdk.WXPayUtil;
import com.netflix.ribbon.proxy.annotation.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付日志表 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2021-01-26
 */
@Service
public class PayLogServiceImpl extends ServiceImpl<PayLogMapper, PayLog> implements PayLogService {

    @Autowired
    private OrderService orderService;

    //生成二维码
    @Override
    public Map createNative(String orderNo) {
        try{
            //1.根据订单号查询订单信息
            QueryWrapper<Order> wrapper = new QueryWrapper<>();
            wrapper.eq("order_no",orderNo);
            Order order = orderService.getOne(wrapper);

            //2.使用map设置生成二维码需要参数
            Map m = new HashMap();
            m.put("appid","wx74862e0dfcf69954");
            m.put("mch_id", "1558950191");
            m.put("nonce_str", WXPayUtil.generateNonceStr());
            m.put("body", order.getCourseTitle());     //课程标题
            m.put("out_trade_no", orderNo);         //订单号
            m.put("total_fee", order.getTotalFee().multiply(new BigDecimal("100")).longValue()+"");     //把价格转为字符串
            m.put("spbill_create_ip", "127.0.0.1");
            m.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify\n");
            m.put("trade_type", "NATIVE");       //支付类型

            //3.发送httpclient请求，传递参数xml格式，微信提供固定的地址
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //设置xml格式的参数
            client.setXmlParam(WXPayUtil.generateSignedXml(m,"T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);      //支持https
            //执行post请求发送
            client.post();

            //4.得到发送的请求并返回结果
            //返回内容是使用xml格式返回的
            String xml = client.getContent();

            //把xml格式转换map集合，把map集合返回
            Map<String,String> resultMap = WXPayUtil.xmlToMap(xml);

            //把返回数据的集合封装
            Map map = new HashMap();
            map.put("out_trade_no", orderNo);
            map.put("course_id", order.getCourseId());
            map.put("total_fee", order.getTotalFee());
            map.put("result_code", resultMap.get("result_code"));          //返回二维码操作的状态码
            map.put("code_url", resultMap.get("code_url"));          //二维码地址
            return map;
        }catch (Exception e){
            throw new GuliException(20001,"生成二维码失败");
        }


    }

    //根据订单号查询订单支付的状态
    @Override
    public Map<String, String> queryPayStatus(String orderNo) {
        try{
            //1、封装参数
            Map m = new HashMap<>();
            m.put("appid", "wx74862e0dfcf69954");
            m.put("mch_id", "1558950191");
            m.put("out_trade_no", orderNo);
            m.put("nonce_str", WXPayUtil.generateNonceStr());

            //2.发送httpclient
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(m, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);
            client.post();

            //3.得到请求并返回内容
            String xml = client.getContent();
            Map<String,String> resultMap = WXPayUtil.xmlToMap(xml);
            //转成Map再返回
            return resultMap;
        }catch (Exception e){
            return null;
        }
    }

    //向支付表添加记录，更新订单状态
    @Override
    public void updateOrdersStatus(Map<String, String> map) {
        //根据map的key获取订单号
        String orderNo = map.get("out_trade_no");
        //根据订单号查询订单信息
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no",orderNo);
        Order order = orderService.getOne(wrapper);

        //更新订单状态
        if(order.getStatus().intValue() == 1){
            return;
        }
        order.setStatus(1);      //1:已支付
        orderService.updateById(order);

        //添加支付记录到支付表
        PayLog payLog = new PayLog();
        payLog.setOrderNo(order.getOrderNo());        //支付订单号
        payLog.setPayTime(new Date());           //订单支付时间
        payLog.setPayType(1);                           //支付类型
        payLog.setTotalFee(order.getTotalFee());          //支付金额
        payLog.setTradeState(map.get("trade_state"));    //支付状态
        payLog.setTransactionId(map.get("transaction_id"));    //订单流水号
        payLog.setAttr(JSONObject.toJSONString(map));
        baseMapper.insert(payLog);//插入到支付日志表


    }
}
