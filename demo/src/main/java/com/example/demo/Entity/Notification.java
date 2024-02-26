package com.example.demo.Entity;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Getter
@Setter
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admin sender;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receiver;

    @Column(name = "sendtime", nullable = false)
    private String sendTime; // 将 sendTime 类型改为 String

    @Column(name = "details", nullable = false)
    private String details;

    @Column(name = "ischecked")
    private String isChecked;

    // Constructors, getters, and setters

    // 新增方法，用于获取和设置时间
    public LocalDateTime getSendTimeAsLocalDateTime() {
        // 使用 DateTimeFormatter 解析字符串为 LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(sendTime, formatter);
    }

    public void setSendTimeAsLocalDateTime(LocalDateTime localDateTime) {
        // 使用 DateTimeFormatter 格式化 LocalDateTime 为字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.sendTime = localDateTime.format(formatter);
    }
}
