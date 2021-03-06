package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父节点id查询子节点
     * @param pid
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoriesByPid(@RequestParam(value = "pid", defaultValue = "0")Long pid){
        if(pid==null || pid<0){
            //400：参数不合法
            return ResponseEntity.badRequest().build();
        }
        List<Category> categories = categoryService.queryCategoriesByPid(pid);
        if(CollectionUtils.isEmpty(categories)){
            // 404: 资源未找到
            return ResponseEntity.notFound().build();
        }
        // 200: 返回数据成功
        return ResponseEntity.ok(categories);
    }


    @GetMapping("names")
    public ResponseEntity<List<String>> queryNamesByIds(@RequestParam("ids")List<Long> ids){
        List<String> names = this.categoryService.queryNameByIds(ids);
        if(CollectionUtils.isEmpty(names)){
            // 404: 资源未找到
            return ResponseEntity.notFound().build();
        }
        // 200: 返回数据成功
        return ResponseEntity.ok(names);
    }

}
