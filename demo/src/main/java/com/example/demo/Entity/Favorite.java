package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Getter
@Setter
@Entity
@Table(name = "favorite")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // 因为 user_id 是外键，所以使用 @ManyToOne 注解
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // 因为 voucher_id 是外键，所以使用 @ManyToOne 注解
    @JoinColumn(name = "voucher_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Coupon voucher;
}

