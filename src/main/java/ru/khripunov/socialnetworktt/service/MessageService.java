package ru.khripunov.socialnetworktt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.khripunov.socialnetworktt.model.Message;
import ru.khripunov.socialnetworktt.repository.MessageRepository;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public Message save(Message message) {
        return messageRepository.save(message);
    }
}
