package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.mapper.EduTeacherMapper;
import com.atguigu.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-02-24
 */
@Service
public class EduTeacherServiceImpl extends ServiceImpl<EduTeacherMapper, EduTeacher> implements EduTeacherService {

    @Autowired
    private EduTeacherService teacherService;

    @Override
    @Cacheable(value = "teacherList",key = "'selectIndexList'")
    public List<EduTeacher> selectAllTeacher() {
        //前四个名师
        QueryWrapper<EduTeacher> wrapperTeacher = new QueryWrapper<>();
        wrapperTeacher.orderByDesc("id");
        wrapperTeacher.last("limit 4");
        List<EduTeacher> teacherList = teacherService.list(wrapperTeacher);
        return teacherList;
    }

    //分页查询讲师的方法
    @Override
    public Map<String, Object> getTeacherFrontList(Page<EduTeacher> pageParam) {
        QueryWrapper<EduTeacher> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        //把分页的数据封装到pageTeacher对象里面
        baseMapper.selectPage(pageParam,wrapper);
        //System.out.println("111111");

        List<EduTeacher> records = pageParam.getRecords();     //每页数据的集合
        long current = pageParam.getCurrent();    //当前页
        long pages = pageParam.getPages();       //总页数
        long size = pageParam.getSize();         //页大小
        long total = pageParam.getTotal();       //总记录数
        boolean hasNext = pageParam.hasNext();       //当前是否有下一页
        boolean hasPrevious = pageParam.hasPrevious();    //当前是否有上一页

        //把分页数据获取出来放到map集合
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);
        //System.out.println("map:"+map.get("items"));
        return map;
    }
}
