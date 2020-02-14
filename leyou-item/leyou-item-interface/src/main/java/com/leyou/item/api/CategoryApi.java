package com.leyou.item.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
public interface CategoryApi {

    @GetMapping("category")
    public List<String> queryNamesByIds(@RequestParam("ids")List<Long> ids);
}
