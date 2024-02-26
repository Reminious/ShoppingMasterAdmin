package com.example.demo.Controller;

import com.example.demo.Repository.CouponRepository;
import com.example.demo.Repository.WebsiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/admin/shop")
public class ShopController {
    @Autowired
    private WebsiteRepository websiteRepository;
    @Autowired
    private CouponRepository couponRepository;
    @GetMapping
    public String listAllShop(Model model) {
        model.addAttribute("webs", websiteRepository.findAll());
        return "shops";
    }
}
