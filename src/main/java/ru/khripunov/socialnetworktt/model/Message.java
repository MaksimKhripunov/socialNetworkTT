package ru.khripunov.socialnetworktt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient_id")
    private Long recipientId;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "content")
    private String content;

    @Column(name = "time_of_receipt")
    private Date timeOfReceipt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", foreignKey = @ForeignKey(name = "id"))
    private Chat chat;




    @Override
    public String toString(){
        return "Chat [id=" + id + ", chat id=" + chat.getId() + ", recipient id=" + recipientId + ", sender id=" + senderId + ", recipient name=" + recipientName
                + ", sender name=" + senderName + ", content=" + content + ", time of receipt=" + timeOfReceipt + "]";
    }
}
