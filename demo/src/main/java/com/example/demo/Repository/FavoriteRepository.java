package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Entity.Favorite;
import com.example.demo.Entity.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    // 根据用户查询所有favorite记录
    List<Favorite> findByUser(User user);
    List<Favorite> findByUserId(Integer userId);
    // 其他查询方法...
    @Query("SELECT f FROM Favorite f JOIN FETCH f.voucher v JOIN FETCH v.website w")
    List<Favorite> findAllWithJoinFetch();
}
