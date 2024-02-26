package com.example.demo.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Entity.Admin;
import com.example.demo.Service.AdminService;

@Controller
public class RegisterController {
	
    @Autowired
    private AdminService adminService; 
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("admin", new Admin()); 
        return "register";
    }
    @PostMapping("/register")
    public String registerAdmin(@ModelAttribute Admin admin, RedirectAttributes redirectAttributes, Model model) {
        try {
            Admin registeredAdmin = adminService.RegisterNewAdmin(admin.getUsername(), admin.getPassword(), admin.getEmail());
            redirectAttributes.addFlashAttribute("successMessage", "注册成功！请登录。");
            return "redirect:/login"; // 假设你有一个登录页面
        } catch (RuntimeException e) {
            // 处理异常，例如用户名已存在
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("admin", admin);
            return "register"; // 重新显示注册表单
        }
    }
}
