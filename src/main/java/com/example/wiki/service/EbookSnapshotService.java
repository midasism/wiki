package com.example.wiki.service;

import com.example.wiki.mapper.OtherEbookSnapshotMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EbookSnapshotService {
    @Resource
    private OtherEbookSnapshotMapper otherEbookSnapshotMapper;

    public void initEbookSnapShot() {
        otherEbookSnapshotMapper.initEbookSnapShot();
    }
}
