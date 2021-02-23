package com.atguigu.eduservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@FeignClient(value = "service-ucenter")
public interface UcenterClient {

    @PostMapping("/educenter/member/getInfoUc/{id}")
    public com.atguigu.commonutils.vo.UcenterMember getInfo(@PathVariable String id);

}
