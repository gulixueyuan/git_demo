package com.atguigu.eduservice.controller.front;

import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.service.EduCourseService;
import com.atguigu.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/eduservice/teacherfront")
@CrossOrigin
public class TeacherFromtController {

    @Autowired
    private EduTeacherService teacherService;

    @Autowired
    private EduCourseService courseService;

    //分页查询讲师
    @ApiOperation(value = "分页查询讲师列表")
    @GetMapping("getTeacherFrontList/{page}/{limit}")
    public R getTeacherFrontList(@PathVariable long page,@PathVariable long limit){
        Page<EduTeacher> pageTeacher = new Page<>(page,limit);
        //System.out.println("222222222222222222222");
        Map<String,Object> map =teacherService.getTeacherFrontList(pageTeacher);
        //返回分页的所有数据
        return R.ok().data(map);
    }

    //讲师详情
    @GetMapping("getTeacherFrontInfoById/{teacherId}")
    public R getTeacherFrontInfoById(@PathVariable String teacherId){
        //根据讲师id查询讲师信息
        EduTeacher teacher = teacherService.getById(teacherId);
        //根据讲师id查询她所讲的课程
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id",teacherId);
        List<EduCourse> courseList = courseService.list(wrapper);

        return R.ok().data("teacher",teacher).data("courseList",courseList);
    }

}
