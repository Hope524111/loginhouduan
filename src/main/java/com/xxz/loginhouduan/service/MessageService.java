package com.xxz.loginhouduan.service;

import com.xxz.loginhouduan.entity.Message;

import java.util.List;

public interface MessageService {
    List<Message> getMessages(String user1, String user2);
    Message saveMessage(Message message);
}
