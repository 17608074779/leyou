package com.leyou.search.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("page")
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest request){
        System.out.println(request);
        SearchResult result = this.searchService.search(request);
        if(result == null || CollectionUtils.isEmpty(result.getItems())){
            System.out.println("searchController为空");
            return ResponseEntity.notFound().build();
        }
        System.out.println("完全没出错-------------------------");
        return ResponseEntity.ok(result);
    }




}
