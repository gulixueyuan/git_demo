package com.atguigu.eduorder.service;

import com.atguigu.eduorder.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author testjava
 * @since 2021-01-26
 */
public interface OrderService extends IService<Order> {

    //生成订单
    String createOrder(String courseId, String memberIdByJwtToken);
}
