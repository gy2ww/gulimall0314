package com.gy.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gy.api.bean.PmsSearchParam;
import com.gy.api.bean.PmsSearchSkuInfo;
import com.gy.api.bean.PmsSkuAttrValue;
import com.gy.api.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;

import javax.annotation.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gaoyong on 2020/4/14.
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    private JestClient jestClient;
    private Map<String, List<String>> highlight;


    @Override
    public List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam) {
        List<PmsSearchSkuInfo> pmsSearchSkuInfoList = new ArrayList<>();
        String dslStr = getDslStr(pmsSearchParam);
        //根据查询条件从es中查询数据
        Search search = new Search.Builder(dslStr).addIndex("gmall0412pms").addType("pmsSkuInfo").build();
        //用es的客户端执行
        SearchResult searchResult = null;
        try {
            searchResult = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取返回结果中的结果集
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = searchResult.getHits(PmsSearchSkuInfo.class);
        //遍历
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            //source是返回结果中存放数据的地方
            PmsSearchSkuInfo source = hit.source;
            //因为在es中高亮的标签和source是平级的，所以我们要把highlight中的高亮的关键字赋给source中的相同字段
            Map<String, List<String>> highlight = hit.highlight;
            //如果不用关键字搜索那么highlight是为空的，所以要判断一下，要不会出现空指针
            if(null!=highlight){
                //取出higlight中的第一个高亮元素
                String skuName = highlight.get("skuName").get(0);
                //赋给source中的字段
                source.setSkuName(skuName);
            }

            pmsSearchSkuInfoList.add(source);
        }

        return pmsSearchSkuInfoList;
    }

    public String getDslStr(PmsSearchParam pmsSearchParam){

        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] skuAttrValueList = pmsSearchParam.getValueId();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //filter / term /terms
        //条件1 首先判断搜索条件是否为空
        if(null != skuAttrValueList){
            for (String pmsSkuAttrValue : skuAttrValueList) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId",pmsSkuAttrValue);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
     //条件2
        if(StringUtils.isNotBlank(catalog3Id)){
            TermQueryBuilder termQueryBuilder1 = new TermQueryBuilder("catalog3Id",catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder1);
        }

        //Must 、Match 搜索框关键字
        if(StringUtils.isNotBlank(keyword)){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName",keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }
        //查询
        searchSourceBuilder.query(boolQueryBuilder);
        //查询每页条数分页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(100);
        //排序对数据按照什么进行排序
        searchSourceBuilder.sort("price", SortOrder.DESC);
        //高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //es默认高亮的样式是<em></em>就是把关键字斜体显示，我们可以修改样式
        highlightBuilder.preTags("<span style='color:red;'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        //es的聚合函数，去重数据
       /* TermsAggregationBuilder name = AggregationBuilders.terms("name").field("skuAttrValueList.valueId");
        searchSourceBuilder.aggregation(name);*/
        //转化为dsl的json格式的语句
        String dslstr = searchSourceBuilder.toString();
        System.out.println(dslstr);
        return dslstr;
    }
}
