package com.example.demo.Controller;

import com.example.demo.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.Entity.Admin;
import com.example.demo.Entity.Coupon;
import com.example.demo.Entity.Favorite;
import com.example.demo.Entity.Notification;
import com.example.demo.Entity.User;
import com.example.demo.Service.AdminService;
import com.example.demo.Service.FavoriteService;
import com.example.demo.Service.UserService;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Controller
@RequestMapping("/notification")
public class NotificationController {
    @Autowired
    NotificationService notificationService;
    
    @Autowired
    AdminService adminService;
    
    @Autowired
    UserService userService;
    
    @Autowired
    FavoriteService favoriteService;
    
    @GetMapping("/notificationList")
    public String showNotifications(Model model) {
        List<Notification> notifications = notificationService.getAllNotifications();
        model.addAttribute("notifications", notifications);
        // You can access the session attributes here if needed
        return "notificationList";
    }

    @GetMapping("/createNotification")
    public String showCreateForm(Model model, HttpSession session) {
        model.addAttribute("notification", new Notification());
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username != null ? username : "DefaultUsername");

        return "createNotification";  // Thymeleaf template name
    }

    @PostMapping("/createNotification")
    public String createNotification(@ModelAttribute Notification notification,
                                      @RequestParam(name = "receiverId") Integer receiverId,
                                      HttpSession session) {
        // 格式化当前时间为字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentDateTimeString = LocalDateTime.now().format(formatter);

        // 设置字符串类型的发送时间
        notification.setSendTime(currentDateTimeString);
        notification.setIsChecked("no");

        // 通过 receiverId 查询 User 对象并设置到 notification 中
        User receiver = userService.getUserById(receiverId);
        notification.setReceiver(receiver);

        // 获取 sender 的用户名
        String senderUsername = (String) session.getAttribute("username");
        Admin sender = adminService.getAdminByAdminName(senderUsername);
        notification.setSender(sender);

        notificationService.saveNotification(notification);
        return "redirect:/notification/notificationList";
    }



    @PostMapping("/sendGlobalNotification")
    public String sendGlobalNotification(HttpSession session) {
        try {
            // 获取所有用户
            List<User> allUsers = userService.getAllUsers();

            // 获取发送者
            String senderUsername = (String) session.getAttribute("username");
            Admin sender = adminService.getAdminByAdminName(senderUsername);

            // 遍历所有用户并发送通知
            for (User user : allUsers) {
                // 创建新的通知对象
                Notification notification = new Notification();
                notification.setDetails("This is a global notification for all users!");
                notification.setSendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                notification.setIsChecked("no");
                notification.setSender(sender);
                notification.setReceiver(user);

                // 保存通知
                notificationService.saveNotification(notification);
            }

            return "templates/home";


        } catch (Exception e) {
            e.printStackTrace();
            return "errorPage"; // 返回错误页面的路径
        }
    }




    @RequestMapping(value = "/checkAndNotifyExpiringCoupons", method = {RequestMethod.GET, RequestMethod.POST})
    public String checkAndNotifyExpiringCoupons(HttpSession session) {
        try {

            // 获取当前用户
            String senderUsername = (String) session.getAttribute("username");
            Admin sender = adminService.getAdminByAdminName(senderUsername);

            // 获取所有用户的所有favorite信息
            List<Favorite> favorites = favoriteService.getAllFavorite();

            // 获取当前日期
            LocalDate currentDate = LocalDate.now();



            // 遍历所有favorite信息并检查过期coupon
            for (Favorite favorite : favorites) {
                Coupon coupon = favorite.getVoucher();
                User user = favorite.getUser();

                // 检查coupon是否在三天内过期
                if (coupon.getExpirationDate().isBefore(currentDate.plusDays(30))) {
                    // 创建通知对象
                    Notification notification = new Notification();
                    notification.setDetails("Your favorite coupon is expiring soon! Coupon details: " + coupon.toString());
                    notification.setSendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    notification.setIsChecked("no");
                    notification.setSender(sender); // 设置为系统发送

                    // 设置通知的接收者
                    notification.setReceiver(user);

                    // 保存通知
                    notificationService.saveNotification(notification);
                }
            }

            return "redirect:/admin/coupon?shopId=1";
        } catch (Exception e) {
            e.printStackTrace();
            return "errorPage"; // 返回错误页面的路径
        }
    }



     //Other controller methods as needed
}
