package ru.khripunov.socialnetworktt.service.MessageService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.khripunov.socialnetworktt.model.Message;
import ru.khripunov.socialnetworktt.repository.MessageRepository;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    @Override
    public Message save(Message message) {
        return messageRepository.save(message);
    }
}
