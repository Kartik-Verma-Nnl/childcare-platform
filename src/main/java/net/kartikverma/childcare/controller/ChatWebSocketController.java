package net.kartikverma.childcare.controller;

import net.kartikverma.childcare.dto.request.ChatMessageRequest;
import net.kartikverma.childcare.dto.response.ChatMessageResponse;
import net.kartikverma.childcare.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatWebSocketController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

//    Client sends to /app/chat.send
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest request, Principal principal) {

        ChatMessageResponse response = chatService.saveMessage(principal.getName(), request);

//        Broadcast to everyone subscribed to this booking's chat topic
        messagingTemplate.convertAndSend(
                "/topic/chat/" + request.getBookingId(),
                response
        );
    }
}
