package com.leyou.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.ibatis.annotations.Param;
import org.elasticsearch.common.xcontent.ToXContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;


@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();

        //根据分类id查询分类名称
        List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));

        //根据品牌id查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        //根据spuId查询所有的sku
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spu.getId());
        //初始化价格集合
        List<Long> prices = new ArrayList<>();
        //收集sku必要字段信息
        List<Map<String, Object>> skuMapList = new ArrayList<>();
        for (Sku sku : skus) {
            prices.add(sku.getPrice());

            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            //获取sku中的图片，数据库的图片可能是多张是以","进行分隔，所以也以逗号来切割返回图片数组，获取第一张图片
            map.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);

            skuMapList.add(map);
        }


        //根据spu中的Cid3获取所有的搜索规格参数
        List<SpecParam> params = this.specificationClient.queryParams(null, spu.getCid3(), null, true);

        //根据spuId查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        //把通用的规格参数值进行反序列化
        Map<String,Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String,Object>>(){});
        //把特殊的规格参数值，进行反序列化
        Map<String, List<Object>> specialSecMap = MAPPER.readValue(spuDetail.getSpecialSpec(),new TypeReference<Map<String, List<Object>>>(){});

        //
        Map<String, Object> specs = new HashMap<>();
        params.forEach(param -> {
            //判断规格参数的类，是否是通用的规格参数
            if(param.getGeneric()){
                //如果是通用类型的参数，从genericSpecMap获取规格参数值
                String value = genericSpecMap.get(param.getId().toString()).toString();
                //判断是否是数值类型，如果是数值类型，应返回一个区间
                if(param.getNumeric()){
                    value = chooseSegment(value, param);
                }
                specs.put(param.getName(), value);
            }else{
                //如果是特殊的规格参数，从specialSpecMap中获取值
                List<Object> value = specialSecMap.get(param.getId().toString());
                specs.put(param.getName(),value);
            }
        });


        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        //拼接all字段，需要分类名称和品牌名称
        goods.setAll(spu.getTitle() + " " + StringUtils.join(names," ") + " " + brand.getName());
        //获取spu下的所有sku的价格
        goods.setPrice(prices);
        //获取spu下的所有sku，并转化为json字符串
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        //获取查询的所有规格参数{name:value}
        goods.setSpecs(specs);
        return goods;
    }


    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

}
