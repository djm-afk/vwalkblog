package com.example.vwalkblog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.vwalkblog.common.BaseContextThreadLocal;
import com.example.vwalkblog.controller.exceptionC.ex.BlogEx;
import com.example.vwalkblog.dto.BlogDto;
import com.example.vwalkblog.pojo.*;
import com.example.vwalkblog.respR.Result;
import com.example.vwalkblog.service.*;
import com.example.vwalkblog.mapper.BlogMapper;
import org.apache.logging.log4j.util.Strings;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 32580
 * @description 针对表【blog(blog)】的数据库操作Service实现
 * @createDate 2023-03-09 15:17:39
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService{

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BlogCategoryService blogCategoryService;

    @Autowired
    private CommentsService commentsService;

    @Autowired
    private UserService userService;
    @Autowired
    private RestHighLevelClient client;


    @Value("${elastic.index}")
    private String index;


    // 新增blog(及elastic)
    @Override
    @Transactional
    public boolean saveWithType(BlogDto blogDto) {
        boolean saveBlog = this.save(blogDto);
        Long blogDtoId = blogDto.getId();
        List<Category> categories = blogDto.getCategories();
        categories.stream().forEach(category -> {
            Long categoryId = category.getId();
            BlogCategory blogCategory = new BlogCategory();
            blogCategory.setBlogId(blogDtoId);
            blogCategory.setCategoryId(categoryId);
            blogCategoryService.save(blogCategory);
        });
        blogDto.getCategories().stream().forEach(category -> {
            Long id = category.getId();
            Category byId = categoryService.getById(id);
            BeanUtils.copyProperties(byId,category);
        });
        blogDto.setImage(null);
        blogDto.setCreateuserdetail(userService.getById(BaseContextThreadLocal.getCurrentId()));
        // 1. 创建Request对象
        IndexRequest request = new IndexRequest(index).id(blogDto.getId().toString());
        // 2. 准备JSON文档
        request.source(JSON.toJSONString(blogDto), XContentType.JSON);
        // 3. 发送请求
        try {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
//            System.out.println("responseStatus "+response.status());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return saveBlog;
    }

    // 根据blogId查询blog
    @Override
    public Result<BlogDto> getBlogById(Long blogId){
        Blog blog = this.getById(blogId);
        BlogDto blogDto = new BlogDto();
        BeanUtils.copyProperties(blog,blogDto,"image");
        String image = blog.getImage();
        blogDto.setImages(image.split(","));
        LambdaQueryWrapper<BlogCategory> lqwbc = new LambdaQueryWrapper<>();
        lqwbc.eq(BlogCategory::getBlogId,blogId);
        List<BlogCategory> blogCategories = blogCategoryService.list(lqwbc);
        List<Category> categories = blogCategories.stream().map(blogCategory -> {
            Long categoryId = blogCategory.getCategoryId();
            Category category = categoryService.getById(categoryId);
            return category;
        }).collect(Collectors.toList());
        blogDto.setCategories(categories);
        return Result.success(blogDto);
    }

    // 查询blog列表
    @Override
    public Result<Page<BlogDto>> selectByPage(String keywords,Integer page,Integer pageSize){
        LambdaQueryWrapper<Blog> lqw = new LambdaQueryWrapper();
        lqw.select(Blog::getId,Blog::getCreateUser,Blog::getTitle,Blog::getDescription,Blog::getCover,Blog::getCreateTime);
        lqw.or(!Strings.isEmpty(keywords)).like(Blog::getTitle,keywords);
        lqw.or(!Strings.isEmpty(keywords)).like(Blog::getDescription,keywords);
        lqw.orderByDesc(Blog::getCreateTime);
        Page page_ = this.page(new Page(page, pageSize), lqw);
        long pages = page_.getPages();
        if (pages < page){
            page_ = this.page(new Page(pages, pageSize), lqw);
        }
        Page<BlogDto> blogDtoPage = new Page<>();
        BeanUtils.copyProperties(page_,blogDtoPage,"records");
        // 处理records
        List<Blog> records = page_.getRecords();
        LambdaQueryWrapper<BlogCategory> lqw_bc = new LambdaQueryWrapper();
        lqw_bc.select(BlogCategory::getCategoryId);
        List<BlogDto> list = new ArrayList<>();
        records.stream().forEach(record -> {
            Long blogId = record.getId();
            BlogDto dishDto = new BlogDto();
            BeanUtils.copyProperties(record,dishDto);
            lqw_bc.clear();
            lqw_bc.eq(BlogCategory::getBlogId,blogId);
            List<BlogCategory> blogCategories = blogCategoryService.list(lqw_bc);
            List<Category> categories = blogCategories.stream().map(blogCategory -> {
                Long categoryId = blogCategory.getCategoryId();
                Category category = categoryService.getById(categoryId);
                return category;
            }).collect(Collectors.toList());
            dishDto.setCategories(categories);
            list.add(dishDto);
        });
        blogDtoPage.setRecords(list);
        return Result.success(blogDtoPage);
    }

    // 根据userId查询blog列表
    @Override
    public Result<List<BlogDto>> getBlogByUserId(Long userId){
        LambdaQueryWrapper<Blog> lqwb = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<BlogCategory> lqwbc = new LambdaQueryWrapper<>();
        lqwbc.select(BlogCategory::getCategoryId);
        lqwb.select(Blog::getId,Blog::getCreateUser,Blog::getTitle,Blog::getCover,Blog::getCreateTime,Blog::getDescription);
        lqwb.eq(Blog::getCreateUser,userId);
        List<Blog> list = this.list(lqwb);
        ArrayList<BlogDto> blogDtoList = (ArrayList<BlogDto>) list.stream().map(blog -> {
            BlogDto blogDto = new BlogDto();
            BeanUtils.copyProperties(blog,blogDto,"image");
//            String image = blog.getImage();
//            blogDto.setImages(image.split(","));
            Long blogId = blogDto.getId();
            lqwbc.clear();
            lqwbc.eq(BlogCategory::getBlogId,blogId);
            List<BlogCategory> blogCategories = blogCategoryService.list(lqwbc);
            List<Category> categories = blogCategories.stream().map(blogCategory -> {
                Long categoryId = blogCategory.getCategoryId();
                Category category = categoryService.getById(categoryId);
                return category;
            }).collect(Collectors.toList());
            blogDto.setCategories(categories);
            return blogDto;
        }).collect(Collectors.toList());
        return Result.success(blogDtoList);
    }

    // 删除blog及其分类、评论(及elastic)
    @Override
    @Transactional
    public boolean removeBlog(Long[] ids){
        List<Long> idList = Arrays.asList(ids);
        // 删除blog
        boolean removeBlogs = this.removeBatchByIds(idList);
        // 删除blog-category表中blog对应的分类
        LambdaUpdateWrapper<BlogCategory> luwBC = new LambdaUpdateWrapper<>();
        idList.stream().forEach(blogId -> {
            luwBC.eq(BlogCategory::getBlogId,blogId);
            blogCategoryService.remove(luwBC);
        });
        // 删除comments表中blog对应comment
        LambdaUpdateWrapper<Comments> luwC = new LambdaUpdateWrapper<>();
        idList.stream().forEach(blogId -> {
            luwC.eq(Comments::getBlogId,blogId);
            commentsService.remove(luwC);
        });
        Arrays.stream(ids).forEach(id -> {
            // 1. 创建Request对象
            DeleteRequest request = new DeleteRequest(index, String.valueOf(id));
            // 2. 发送请求
            try {
                DeleteResponse delete = client.delete(request, RequestOptions.DEFAULT);
                if ("NOT_FOUND".equalsIgnoreCase(String.valueOf(delete.getResult()))){
                    throw new BlogEx("Elastic索引库中无此数据");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return removeBlogs;
    }

    // elastic查看blog列表
    @Override
    public Result<PageResult> selectByElastic(RequestParams requestParams) {
        SearchRequest request = new SearchRequest(index);
//        System.out.println(requestParams);
        String key = requestParams.getKey();
        // 关键字搜索
        if (key == null || "".equals(key)){
            request.source()
                    .query(QueryBuilders.matchAllQuery());
        }else {
            request.source()
                    .query(QueryBuilders.matchQuery("all",key));
        }
        Integer page = requestParams.getPage();
        Integer size = requestParams.getSize();
        request.source()
                .highlighter(new HighlightBuilder()
                .field("title").requireFieldMatch(false)
                .field("description").requireFieldMatch(false));
        request.source().from(0).size(20);
        request.source().sort("createTime", SortOrder.DESC);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return Result.success(handleResponse(response));
        }catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // 根据userId查询blog列表(elastic)
    @Override
    public Result<PageResult> getBlogByUserIdElastic(Long userId){
        SearchRequest request = new SearchRequest(index);
        request.source()
                .query(QueryBuilders.matchQuery("createUser",userId));
//        Integer page = requestParams.getPage();
//        Integer size = requestParams.getSize();
//        request.source().from((page - 1) * size).size(size);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return Result.success(handleResponse(response));
        }catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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
