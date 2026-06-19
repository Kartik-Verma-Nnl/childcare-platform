package net.kartikverma.childcare.service;

import net.kartikverma.childcare.dto.request.ChatMessageRequest;
import net.kartikverma.childcare.dto.response.ChatMessageResponse;
import net.kartikverma.childcare.model.Booking;
import net.kartikverma.childcare.model.ChatMessage;
import net.kartikverma.childcare.model.User;
import net.kartikverma.childcare.repository.BookingRepository;
import net.kartikverma.childcare.repository.ChatMessageRepository;
import net.kartikverma.childcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

//    Save a message and return it for broadcasting
    public ChatMessageResponse saveMessage(String senderEmail, ChatMessageRequest request) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

//        Verify sender is part of this booking (parent or caregiver)
        boolean isParent = booking.getParent().getId().equals(sender.getId());
        boolean isCaregiver = booking.getCaregiver().getUser().getId().equals(sender.getId());

        if(!isParent && !isCaregiver) {
            throw new RuntimeException("You are not a part of this booking");
        }

        ChatMessage message = ChatMessage.builder()
                .booking(booking)
                .sender(sender)
                .content(request.getContent())
                .build();

        chatMessageRepository.save(message);

        return mapToResponse(message);
    }

//    Get chat history for a booking
    public List<ChatMessageResponse> getChatHistory(Long boookingId) {
        return chatMessageRepository.findByBookingIdOrderBySentAtAsc(boookingId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ChatMessageResponse mapToResponse(ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .bookingId(message.getBooking().getId())
                .senderEmail(message.getSender().getEmail())
                .senderName(message.getSender().getName())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .build();
    }
}
