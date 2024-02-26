package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Entity.Notification;
import com.example.demo.Repository.NotificationRepository;

import java.util.List;

@Service
public class NotificationService {
	@Autowired
	private NotificationRepository notificationRepository;
   


    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Notification saveNotification(Notification notification) {
        // Additional logic can be added before saving
        return notificationRepository.save(notification);
    }

    // Other service methods as needed
}
