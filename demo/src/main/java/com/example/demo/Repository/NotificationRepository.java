package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    // Custom query methods if needed
}
