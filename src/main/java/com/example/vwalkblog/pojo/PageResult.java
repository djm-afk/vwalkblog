package com.example.vwalkblog.pojo;

import com.example.vwalkblog.dto.BlogDto;
import lombok.Data;

import java.util.List;

@Data
public class PageResult {
    private Long total;
    private List<BlogDto> records;

    public PageResult(){}
    public PageResult(Long total, List<BlogDto> records) {
        this.total = total;
        this.records = records;
    }
}
