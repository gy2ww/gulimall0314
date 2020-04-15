package com.gy.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gy.api.bean.PmsSearchSkuInfo;
import com.gy.api.bean.PmsSkuInfo;
import com.gy.api.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchServiceApplicationTests {

	@Reference
	private SkuService skuService;
	@Resource
	private JestClient jestClient;
	@Test
	public void contextLoads() throws IOException, InvocationTargetException, IllegalAccessException {
       List<PmsSearchSkuInfo> pmsSearchSkuInfoList = new ArrayList<>();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //filter / term /terms
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId","83");
        boolQueryBuilder.filter(termQueryBuilder);

        TermQueryBuilder termQueryBuilder1 = new TermQueryBuilder("skuAttrValueList.valueId","86");
        boolQueryBuilder.filter(termQueryBuilder1);
        //Must 、Match
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName","华为");
        boolQueryBuilder.must(matchQueryBuilder);
        //查询
        searchSourceBuilder.query(boolQueryBuilder);
        //转化为dsl的json格式的语句
        String dslstr = searchSourceBuilder.toString();
        //根据查询条件从es中查询数据
        Search search = new Search.Builder(dslstr).addIndex("gmall0412pms").addType("pmsSkuInfo").build();
        //用es的客户端执行
        SearchResult searchResult = jestClient.execute(search);
        //获取返回结果中的结果集
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = searchResult.getHits(PmsSearchSkuInfo.class);
        //遍历
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            //source是返回结果中存放数据的地方
            PmsSearchSkuInfo source = hit.source;
            pmsSearchSkuInfoList.add(source);
        }
        System.out.println(pmsSearchSkuInfoList.size());
    }
	@Test
   public void put() throws IOException {
       List<PmsSkuInfo> skuInfoList = new ArrayList<PmsSkuInfo>();
       List<PmsSearchSkuInfo> searchSkuInfoList = new ArrayList<PmsSearchSkuInfo>();
       //从数据库中取值
       skuInfoList = skuService.getAllSku();
       //转化为es的数据结构
       for (PmsSkuInfo pmsSkuInfo : skuInfoList) {
           PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
           //把一个对象的属性值复制给另一个对象，前提是两个实体类中的属性名大部分相同
           BeanUtils.copyProperties(pmsSkuInfo,pmsSearchSkuInfo);
           searchSkuInfoList.add(pmsSearchSkuInfo);
       }

       //放入es
       for (PmsSearchSkuInfo pmsSearchSkuInfo : searchSkuInfoList) {
           Index put = new Index.Builder(pmsSearchSkuInfo).index("gmall0412pms").type("pmsSkuInfo").id(pmsSearchSkuInfo.getId()).build();
           jestClient.execute(put);
       }
   }
}
