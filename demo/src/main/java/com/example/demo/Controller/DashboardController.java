package com.example.demo.Controller;

import com.example.demo.Service.AdminService;
import com.example.demo.Service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final FavoriteService favoriteService;

    @Autowired
    private final AdminService adminService;


    @Autowired
    public DashboardController(FavoriteService favoriteService, AdminService adminService) {
        this.favoriteService = favoriteService;
		this.adminService =  adminService;
    }

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        // 获取未排序的 shopCountMap
        Map<String, Integer> shopCountMap = favoriteService.countCouponsByShopWithJoinFetch();

        // 对 shopCountMap 进行按值降序排序
        Map<String, Integer> sortedShopCountMap = shopCountMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        
        Integer totalUsers = adminService.getTotalUsers();
        Integer totalCoupons = adminService.getTotalCoupons();
        Integer totalShops = adminService.getTotalShops();

        // 将排序后的 shopCountMap 传递给 Thymeleaf 模板
        model.addAttribute("shopCountMap", sortedShopCountMap);
        
        // 将用户总数、优惠券总数和商店总数传递给 Thymeleaf 模板
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalCoupons", totalCoupons);
        model.addAttribute("totalShops", totalShops);
        
        // 将排序后的 shopCountMap 传递给 Thymeleaf 模板，仍然使用相同的参数名
        model.addAttribute("shopCountMap", sortedShopCountMap);

        return "dashboard";
    }
}
