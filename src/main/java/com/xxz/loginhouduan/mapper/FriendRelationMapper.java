package com.xxz.loginhouduan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxz.loginhouduan.entity.FriendRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface FriendRelationMapper extends BaseMapper<FriendRelation> {

    @Select("SELECT su.id, su.login_name AS username " +
            "FROM friend_relation fr " +
            "JOIN sys_user su ON fr.friend_id = su.id " +
            "WHERE fr.user_id = #{userId}")
    List<Map<String, Object>> getFriends(@Param("userId") Long userId);
}

