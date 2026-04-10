package com.xxz.loginhouduan.service.impl;

import com.xxz.loginhouduan.entity.Message;
import com.xxz.loginhouduan.repository.MessageRepository;
import com.xxz.loginhouduan.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public List<Message> getMessages(String user1, String user2) {
        return messageRepository.findMessagesBetween(user1, user2);
    }

    @Override
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }
}
