package com.xxz.loginhouduan.controller;

import com.xxz.loginhouduan.entity.Message;
import com.xxz.loginhouduan.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // Get the list of messages between sender and receiver
    @GetMapping
    public List<Message> getMessages(@RequestParam String sender,
                                     @RequestParam String receiverId) {
        return messageService.getMessages(sender, receiverId);
    }

    // Send a message from sender to receiver
    @PostMapping
    public Message sendMessage(@RequestBody Message message) {
        System.out.println("📩 Received message: " + message);

        // Check if sender, receiver, and content are not null
        if (message.getSender() == null || message.getReceiver() == null || message.getContent() == null) {
            throw new IllegalArgumentException("sender/receiver/content cannot be null!");
        }

        // Save the message and return it
        return messageService.saveMessage(message);
    }
}
