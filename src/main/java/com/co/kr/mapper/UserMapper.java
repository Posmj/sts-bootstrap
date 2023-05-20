package com.co.kr.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    //delete
    public void mbRemove(Map<String, String> map);
    
}