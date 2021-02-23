package com.atguigu.eduorder.service.impl;

import com.atguigu.commonutils.ordervo.CourseWebVoOrder;
import com.atguigu.commonutils.ordervo.UcenterMemberOrder;
import com.atguigu.eduorder.client.EduClient;
import com.atguigu.eduorder.client.UcenterClient;
import com.atguigu.eduorder.entity.Order;
import com.atguigu.eduorder.mapper.OrderMapper;
import com.atguigu.eduorder.service.OrderService;
import com.atguigu.eduorder.utils.OrderNoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2021-01-26
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private EduClient eduClient;

    @Autowired
    private UcenterClient ucenterClient;

    //生成订单
    @Override
    public String createOrder(String courseId, String memberId) {
        //System.out.println("==================memberId:"+memberId);
        //System.out.println("==================courseId:"+courseId);
        //通过远程调用根据用户id获取用户信息
        UcenterMemberOrder userInfoOrder = ucenterClient.getUserInfoOrderById(memberId);
        //System.out.println("==================userInfoOrder:"+userInfoOrder);

        //通过远程调用根据课程id获取课程信息
        CourseWebVoOrder courseInfoOrder = eduClient.getCourseInfoOrderById(courseId);
        //System.out.println("==================courseInfoOrder:"+courseInfoOrder);
        //创建Order对象，向order对象里面赋值
        Order order = new Order();
        order.setOrderNo(OrderNoUtil.getOrderNo());           //订单号
        order.setCourseId(courseId);                         //课程id
        order.setCourseTitle(courseInfoOrder.getTitle());    //课程标题
        order.setCourseCover(courseInfoOrder.getCover());    //课程封面图片路径
        order.setTeacherName("test");                        //讲师名称
        order.setTotalFee(courseInfoOrder.getPrice());      //课程销售价格
        order.setMemberId(memberId);                        //用户id
        order.setMobile(userInfoOrder.getMobile());         //用户手机号
        order.setNickname(userInfoOrder.getNickname());     //用户昵称
        order.setStatus(0);                                 //支付状态   （0：未支付  1：已支付）
        order.setPayType(1);                                //支付类型 微信1
        baseMapper.insert(order);
        return order.getOrderNo();      //返回订单号
    }
}
