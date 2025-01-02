package com.chatop.My_Chatop_Api.repositories;

import com.chatop.My_Chatop_Api.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
