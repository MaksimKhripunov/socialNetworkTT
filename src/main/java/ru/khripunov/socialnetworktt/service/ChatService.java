package ru.khripunov.socialnetworktt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ru.khripunov.socialnetworktt.model.Chat;
import ru.khripunov.socialnetworktt.model.Message;
import ru.khripunov.socialnetworktt.model.Person;
import ru.khripunov.socialnetworktt.repository.ChatRepository;


import java.util.*;


@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    private final MessageService messageService;
    private final PeopleService peopleService;

    private final FriendService friendService;



    public List<Message> findByUsername(String username, Jwt principal){

        String usernameOfSender = peopleService.checkJWT(principal);

        Person sender = (Person) peopleService.loadUserByUsername(usernameOfSender);
        Person recipient = (Person) peopleService.loadUserByUsername(username);

        Chat chat = chatRepository.findByCompanions(sender.getId(), recipient.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Chat not found"));


        return chat.getMessagesList();

    }

    public Chat checkChat(String username, String content, Jwt principal) {

        String usernameOfSender = peopleService.checkJWT(principal);

        Person sender = (Person) peopleService.loadUserByUsername(usernameOfSender);
        Person recipient = (Person) peopleService.loadUserByUsername(username);

        if(!recipient.getMessageOnlyFriends() || friendService.checkRelationships(sender.getId(), recipient.getId()) || Objects.equals(sender, recipient)) {
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
        messageService.save(message);
    }

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

}
