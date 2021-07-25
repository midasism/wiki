package com.example.wiki.mapper;

import com.example.wiki.resp.DocQueryResp;
import org.apache.ibatis.annotations.Param;

public interface OtherDocMapper {
    public int incrementViewCount(@Param("id") long id);

    public DocQueryResp queryCount(@Param("id") long id);

    public int incrementVoteCount(@Param("id") long id);

    public void updateEbookInfo();
}
