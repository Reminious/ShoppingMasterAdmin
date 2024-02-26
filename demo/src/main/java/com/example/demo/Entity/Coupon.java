package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "coupon")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "website_id", nullable = false)
    private Website website;

    @Column(name = "description")
    private String description;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "discount")
    private String discount;

    @Column(name = "verified")
    private String verified;

    @Column(name = "code_or_deal")
    private String codeOrDeal;

    @Column(name = "terms", length = 511)
    private String terms;

    @Column(name = "promo_code")
    private String promoCode;

    @Column(name = "promo_url", length = 1023)
    private String promoUrl;

}