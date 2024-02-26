package com.example.demo.Controller;
import java.util.Optional;

import com.example.demo.Repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.Entity.Admin;
import com.example.demo.Service.AdminService;
import jakarta.servlet.http.HttpSession;
@Controller
public class LoginController {
@Autowired
AdminService adminService;
@Autowired
AdminRepository adminRepository;

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("encryptedPassword") String encryptedPassword,
                        HttpSession session,Model model) {
        // 在这里添加验证逻辑
        boolean isValidUser = authenticate(username, encryptedPassword);
        if (isValidUser) {
            // 验证成功，重定向到主页或者相应的页面
        	session.setAttribute("username", username);
        	return "redirect:/admin/coupon?shopId=1";
        } else {
            // 验证失败，重定向回登录页面，可能带有错误信息
            return "redirect:/login?error=true";
        }
    }
    @GetMapping("/")
    public String loginin() {
    	return "/login";
    }//默认跳转登录界面
    @GetMapping("/login")
    public String loginin1() {
    	return "/login";
    }
    private boolean authenticate(String username, String encryptedPassword) {
        Optional<Admin> adminOptional = adminRepository.findByUsername(username);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            // 假设这里的getPasswordHash()返回的是数据库中存储的密码哈希
            String storedPasswordHash = admin.getPasswordHash();
            // 这里比较用户输入的加密密码与存储的密码哈希
            // 如果在存储时使用了特定的哈希算法，请确保这里使用相同的算法对用户输入的密码进行加密
            return storedPasswordHash.equals(encryptedPassword);
        }
        // 如果找不到用户，返回false
        return false;
    }

}
