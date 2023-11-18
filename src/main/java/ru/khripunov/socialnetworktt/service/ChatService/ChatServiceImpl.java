package ru.khripunov.socialnetworktt.service.ChatService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ru.khripunov.socialnetworktt.model.Chat;
import ru.khripunov.socialnetworktt.model.Message;
import ru.khripunov.socialnetworktt.model.Person;
import ru.khripunov.socialnetworktt.repository.ChatRepository;
import ru.khripunov.socialnetworktt.service.FriendService.FriendServiceImpl;
import ru.khripunov.socialnetworktt.service.MessageService.MessageServiceImpl;
import ru.khripunov.socialnetworktt.service.PeopleService.PeopleServiceImpl;


import java.util.*;


@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    private final MessageServiceImpl messageServiceImpl;
    private final PeopleServiceImpl peopleServiceImpl;

    private final FriendServiceImpl friendServiceImpl;


    @Override
    public List<Message> findByUsername(String username, Jwt principal){

        Person sender = (Person) peopleServiceImpl.loadUserByUsername(principal.getSubject());
        Person recipient = (Person) peopleServiceImpl.loadUserByUsername(username);

        Chat chat = chatRepository.findByCompanions(sender.getId(), recipient.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Chat not found"));


        return chat.getMessagesList();

    }

    @Override
    public Chat checkChat(String username, String content, Jwt principal) {


        Person sender = (Person) peopleServiceImpl.loadUserByUsername(principal.getSubject());
        Person recipient = (Person) peopleServiceImpl.loadUserByUsername(username);

        if(!recipient.getMessageOnlyFriends() || friendServiceImpl.checkRelationships(sender.getId(), recipient.getId()) || Objects.equals(sender, recipient)) {
            Optional<Chat> chat;

            try {
                chat = chatRepository.findByCompanions(sender.getId(), recipient.getId());
                saveMessage(chat.get(), recipient, sender, content);

                return chat.get();
            } catch (Exception e) {
                Chat newChat = new Chat();
                newChat.setFirstCompanion(sender.getId());
                newChat.setSecondCompanion(recipient.getId());
                chatRepository.save(newChat);

                saveMessage(newChat, recipient, sender, content);
                return newChat;
            }
        }

        return null;
    }

    @Override
    public ArrayList<ArrayList<String>> checkHistoryOfChat(String username, Jwt principal) {
        ArrayList<ArrayList<String>> chat = new ArrayList<>();

        List<Message> list = findByUsername(username, principal);
        for(Message message : list) {
            ArrayList<String> mess = new ArrayList<>();
            mess.add(message.getSenderName());
            mess.add(message.getContent());
            chat.add(mess);
        }
        return chat;
    }


    private void saveMessage(Chat chat, Person recipient, Person sender, String content){
        Message message = new Message();
        message.setRecipientId(recipient.getId());
        message.setSenderId(sender.getId());
        message.setRecipientName(recipient.getUsername());
        message.setSenderName(sender.getUsername());
        message.setContent(content);
        message.setTimeOfReceipt(new Date());
        if (chat.getMessagesList() == null){
            chat.setMessagesList(new ArrayList<>());
        }
        chat.addMessage(message);
        messageServiceImpl.save(message);
    }



}
