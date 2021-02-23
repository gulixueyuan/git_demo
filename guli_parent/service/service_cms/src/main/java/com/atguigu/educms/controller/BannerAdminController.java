package com.atguigu.educms.controller;


import com.atguigu.commonutils.R;
import com.atguigu.educms.entity.CrmBanner;
import com.atguigu.educms.service.CrmBannerService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 后台banner管理接口
 * </p>
 *
 * @author testjava
 * @since 2021-01-21
 */
@RestController
@RequestMapping("/educmse/banner")
@CrossOrigin
public class BannerAdminController {

    @Autowired
    private CrmBannerService bannerService;

    //分页查询banner
    @ApiOperation(value = "分页查询banner")
    @GetMapping("pageBanner/{page}/{limit}")
    public R pageBanner(@PathVariable long page, @PathVariable long limit){
        Page<CrmBanner> pageBanner = new Page<>(page,limit);
        bannerService.page(pageBanner,null);
        return R.ok().data("itcms",pageBanner.getRecords()).data("total",pageBanner.getTotal());
    }

    //添加banner
    @ApiOperation(value = "添加banner")
    @PostMapping("addBanner")
    public R addBanner(@RequestBody CrmBanner crmBanner){
        bannerService.save(crmBanner);
        return R.ok();
    }

    //修改banner
    @ApiOperation(value = "获取banner")
    @GetMapping("getInfo/{id}")
    public R getInfo(@PathVariable String id) {
        CrmBanner banner = bannerService.getById(id);
        return R.ok().data("item", banner);
    }

    @ApiOperation(value = "修改banner")
    @PostMapping("updateById")
    public R updateById(@RequestBody CrmBanner crmBanner){
        bannerService.updateById(crmBanner);
        return R.ok();
    }

    //删除banner
    @ApiOperation(value = "删除banner")
    @PostMapping("delBanner/{id}")
    public R delBanner(@PathVariable String id){
        bannerService.removeById(id);
        return R.ok();
    }


}

