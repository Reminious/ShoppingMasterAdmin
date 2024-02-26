package com.example.demo.Service;

import com.example.demo.Entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.FavoriteRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FavoriteService {
	@Autowired
	private FavoriteRepository favoriteRepository;

    
    public List<Favorite> getAllFavorite() {
        return favoriteRepository.findAll();
    }

    // 根据用户查询所有favorite记录
    public List<Favorite> getFavoritesByUser(User user) {
        return favoriteRepository.findByUser(user);
    }
    
 // 根据用户id查询所有favorite记录
    public List<Favorite> getFavoritesByUserId(Integer userId) {
        return favoriteRepository.findByUserId(userId);
    }
    // 其他方法...
    @Transactional
    public Map<String, Integer> countCouponsByShopWithJoinFetch() {
        // 使用 JOIN FETCH 一次性获取关联实体信息
        List<Favorite> favorites = favoriteRepository.findAllWithJoinFetch();
        Map<String, Integer> shopCountMap = new HashMap<>();

        for (Favorite favorite : favorites) {
            Coupon coupon = favorite.getVoucher();
            if (coupon != null) {
                Website website = coupon.getWebsite();
                if (website != null) {
                    String shopName = website.getShopName();
                    shopCountMap.put(shopName, shopCountMap.getOrDefault(shopName, 0) + 1);
                }
            }
        }

        return shopCountMap;
        // 其他方法...
    }
}
