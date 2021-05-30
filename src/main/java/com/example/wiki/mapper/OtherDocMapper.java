package com.example.wiki.mapper;

import org.apache.ibatis.annotations.Param;

public interface OtherDocMapper {
    public int incrementViewCount(@Param("id") long id);
}
