package ru.khripunov.socialnetworktt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_companion")
    private Long firstCompanion;

    @Column(name = "second_companion")
    private Long secondCompanion;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chat")
    private List<Message> messagesList=new ArrayList<>();



    public void addMessage(Message message) {
        messagesList.add(message);
        message.setChat(this);
    }
    public void removeMessage(Message message) {
        messagesList.remove(message);
        message.setChat(null);
    }

    @Override
    public String toString(){
        return "Chat [id=" + id + ", first companion=" + firstCompanion + ", second companion=" + secondCompanion + "]";
    }
}
