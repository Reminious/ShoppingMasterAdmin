package com.example.demo.Repository;
import com.example.demo.Entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    Page<Coupon> findByWebsiteId(Integer websiteId, Pageable pageable);

}
