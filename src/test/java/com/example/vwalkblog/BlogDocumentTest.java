package com.example.vwalkblog;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.vwalkblog.dto.BlogDto;
import com.example.vwalkblog.pojo.*;
import com.example.vwalkblog.service.BlogCategoryService;
import com.example.vwalkblog.service.BlogService;
import com.example.vwalkblog.service.CategoryService;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootTest
public class BlogDocumentTest {
    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private BlogService blogService;
    @Autowired
    private BlogCategoryService blogCategoryService;
    @Autowired
    private CategoryService categoryService;

    // 批量导入文档
    @Test
    public void testBulkRequest() throws IOException {
        // 1. 创建Request对象
        BulkRequest bulkRequest = new BulkRequest();


        LambdaQueryWrapper<Blog> lqw = new LambdaQueryWrapper();
        lqw.select(Blog::getId,Blog::getCreateUser,Blog::getTitle,Blog::getDescription,Blog::getCover,Blog::getCreateTime);
        LambdaQueryWrapper<BlogCategory> lqwBC = new LambdaQueryWrapper<>();
        lqwBC.select(BlogCategory::getCategoryId);
        List<Blog> blogs = blogService.list(lqw);
//        System.out.println("blogs = " + blogs);
        blogs.stream().forEach(blog -> {
            BlogDto blogDto_ = new BlogDto();
            BeanUtils.copyProperties(blog,blogDto_);
//            System.out.println("blog = " + blog);
//            System.out.println("blogDto = " + blogDto_);
            Long blogId = blog.getId();
            lqwBC.clear();
            lqwBC.eq(BlogCategory::getBlogId,blogId);
            List<BlogCategory> blogCategories = blogCategoryService.list(lqwBC);
            List<Category> categories = blogCategories.stream().map(blogCategory -> {
                Long categoryId = blogCategory.getCategoryId();
                Category category = categoryService.getById(categoryId);
                return category;
            }).collect(Collectors.toList());
            blogDto_.setCategories(categories);
//            System.out.println("category = " + blogDto_);
            // 2. 准备参数，添加多个新增的Request
            bulkRequest.add(new IndexRequest("vwalk_blog")
                    .id(blogId.toString())
                    .source(JSON.toJSONString(blogDto_), XContentType.JSON));
        });
        // 3. 发送请求
        client.bulk(bulkRequest, RequestOptions.DEFAULT);


        /*blogDtos.stream().forEach(blogDto -> {
            // 2. 准备参数，添加多个新增的Request
            bulkRequest.add(new IndexRequest("vwalk_blog")
                    .id(blogDto.getId().toString())
                    .source(JSON.toJSONString(blogDto), XContentType.JSON));
        });
        // 3. 发送请求
        client.bulk(bulkRequest, RequestOptions.DEFAULT);*/
    }


    @Test
    public void searchBlog(){
        RequestParams requestParams = new RequestParams();
        requestParams.setKey("城市");
        requestParams.setPage(1);
        requestParams.setSize(5);
        requestParams.setSortBy("desc");
        PageResult search = this.search(requestParams);
        System.out.println(search);
    }

    public PageResult search(RequestParams requestParams) {
        SearchRequest request = new SearchRequest("vwalk_blog");

//        buildBasicQuery(requestParams, request);
        System.out.println(requestParams);
        String key = requestParams.getKey();

        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        // 关键字搜索
        if (key == null || "".equals(key)){
            boolQuery.must(QueryBuilders.matchAllQuery());
        }else {
            boolQuery.must(QueryBuilders.matchQuery("all",key));
        }
//        // 城市过滤
//        if (city != null && !city.equals("")){
//            boolQuery.filter(QueryBuilders.termsQuery("city",city));
//        }
//        // 品牌过滤
//        if (brand != null && !brand.equals("")){
//            boolQuery.filter(QueryBuilders.termsQuery("brand",brand));
//        }
//        // 星级过滤
////        if (starName != null && !"".equals(starName)){
////            boolQueryBuilder.filter(QueryBuilders.termQuery("starName",brand));
////        }
//        // 价格
//        if (minPrice != null && maxPrice != null){
//            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(minPrice).lte(maxPrice));
//        }
//        // 算分控制
//        FunctionScoreQueryBuilder functionScoreQuery =
//                QueryBuilders.functionScoreQuery(
//                        // 原始查询，相关性算分
//                        boolQuery,
//                        // function score的数组
//                        new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
//                                // 其中的一个function score 元素
//                                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
//                                        // 过滤条件
//                                        QueryBuilders.termsQuery("isAD",true),
//                                        // 算分函数
//                                        ScoreFunctionBuilders.weightFactorFunction(10)
//                                )
//                        });
//
//        request.source().query(functionScoreQuery);
        Integer page = requestParams.getPage();
        Integer size = requestParams.getSize();
        request.source().highlighter(new HighlightBuilder()
                .field("title").requireFieldMatch(false)
                .field("description").requireFieldMatch(false));
        request.source().from((page - 1) * size).size(size);
//        request.source().sort(sortBy, SortOrder.DESC);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
//            System.out.println(response);
            return handleResponse(response);
        }catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    private static void buildBasicQuery(RequestParams requestParams, SearchRequest request) {

    }

    private static PageResult handleResponse(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        System.out.println("共搜索到 "+total+" 条数据");
        SearchHit[] hits = searchHits.getHits();
        PageResult pageResult = new PageResult();
        pageResult.setTotal(total);
        List<BlogDto> blogDtos = Arrays.stream(hits).map(hit -> {
            String json = hit.getSourceAsString();
            BlogDto blogDto = JSON.parseObject(json, BlogDto.class);
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            // 根据名字获取高亮结果
            if (!CollectionUtils.isEmpty(highlightFields)) {
                HighlightField highlightFieldTitle = highlightFields.get("title");
                HighlightField highlightFieldDescription = highlightFields.get("description");
                if (Objects.nonNull(highlightFieldTitle)) {
                    String title = highlightFieldTitle.getFragments()[0].string();
                    // 覆盖非高亮
                    blogDto.setTitle(title);
                }
                if (Objects.nonNull(highlightFieldDescription)) {
                    String description = highlightFieldDescription.getFragments()[0].string();
                    // 覆盖非高亮
                    blogDto.setDescription(description);
                }
            }
            return blogDto;
        }).collect(Collectors.toList());
        pageResult.setRecords(blogDtos);
        return pageResult;
    }
}
