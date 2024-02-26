package com.example.demo.Service;

import com.example.demo.Entity.Admin;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.CouponRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Repository.WebsiteRepository;
import com.example.demo.utili.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private WebsiteRepository websiteRepository;

    public Admin RegisterNewAdmin(String username, String password, String email) {
        if (adminRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists.");
        }
        //String salt = generateSalt();
        // String passwordHash = passwordEncoder.encode(password + salt);
        Admin admin = new Admin();
        admin.setEmail(email);
        //  admin.setPasswordHash(passwordHash);
        admin.setUsername(username);
        // admin.setSalt(salt);
        String password_hash = HashUtil.sha256(password);
        admin.setPasswordHash(password_hash);
        return adminRepository.save(admin);
    }

    public Admin getAdminByAdminName(String adminName) {
        return adminRepository.findByUsername(adminName).orElse(null);
    }

    public Integer getTotalUsers() {
        return (int) userRepository.count(); // 使用 UserRepository 的 count 方法获取用户总数
    }


    public Integer getTotalCoupons() {
        return (int) couponRepository.count(); // 使用 CouponRepository 的 count 方法获取优惠券总数
    }

    public Integer getTotalShops() {
        return (int) websiteRepository.count(); // 使用 WebsiteRepository 的 count 方法获取商店总数
    }

    public Admin getAdminById(long id) {
        return adminRepository.findById(id).orElse(null);
    }
}

