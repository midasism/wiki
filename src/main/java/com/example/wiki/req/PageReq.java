package com.example.wiki.req;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public class PageReq {
    /**
     * 页码
     */
    @NotNull(message = "页码不能为空")
    private int page;

    /**
     * 每页条数
     */
    @NotNull(message = "每页条数不能为空")
    @Max(value = 100, message = "每页条数不能超过1000")
    private int size;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "PageReq{" +
                "page=" + page +
                ", size=" + size +
                '}';
    }
}