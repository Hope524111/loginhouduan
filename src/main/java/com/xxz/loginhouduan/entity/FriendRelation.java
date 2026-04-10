package com.xxz.loginhouduan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("friend_relation")
public class FriendRelation {
    private Long id;
    private Long userId;
    private Long friendId;
    private Date createdTime;
}
