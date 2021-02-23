package com.atguigu.eduservice.service;

import com.atguigu.eduservice.entity.EduComment;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface EduCommentService  extends IService<EduComment> {

    //条件查询带分页
    //void page(Page<EduComment> pageParam, String courseId);
}
