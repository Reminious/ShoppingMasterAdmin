package com.example.demo.Controller;

import com.example.demo.Entity.Coupon;
import com.example.demo.Entity.Record;
import com.example.demo.Entity.Website;
import com.example.demo.Repository.CouponRepository;
import com.example.demo.Repository.RecordRepository;
import com.example.demo.Repository.WebsiteRepository;
import com.example.demo.utili.CouponScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/admin/coupon")
public class CouponController {
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private WebsiteRepository websiteRepository;
    @Autowired
    private RecordRepository recordRepository;

    @GetMapping
    public String listCoupon(@RequestParam(value = "shopId", required = false) Integer websiteId,
                             @RequestParam(value = "searchTerm", required = false) String searchTerm,
                             Pageable pageable,
                             Model model) {
        List<Website> websites;
        Integer shopId = websiteId;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            websites = websiteRepository.findByShopNameContaining(searchTerm);
            if (!websites.isEmpty()) {
                websiteId = websites.get(0).getId();
            }
        } else {
            websites = websiteRepository.findAll();
        }
        if (shopId != null) {
            websiteId = shopId;
        }
        model.addAttribute("websiteId", websiteId == null ? 0 : websiteId);
        model.addAttribute("coupons", couponRepository.findByWebsiteId(websiteId, pageable));
        model.addAttribute("webs", websites);
        model.addAttribute("searchTerm", searchTerm);
        return "tables";
    }

    @GetMapping("/create")
    public String createCoupon(@RequestParam("shopId") Integer websiteId, Model model) {
        Coupon coupon = new Coupon();
        Website website = websiteRepository.findById(websiteId).orElse(null);
        coupon.setWebsite(website);
        model.addAttribute("coupon", coupon);
        return "createForm";
    }

    @PostMapping("/create")
    public String createCoupon(@ModelAttribute Coupon coupon, RedirectAttributes redirectAttributes) {
        couponRepository.save(coupon);
        redirectAttributes.addAttribute("shopId", coupon.getWebsite().getId());
        return "redirect:/admin/coupon?shopId={shopId}";
    }

    @GetMapping("/update")
    public String updateCoupon(@RequestParam("couponId") Integer couponId,
                               @RequestParam("shopId") Integer shopId,
                               Model model) {
        Coupon coupon = couponRepository.findById(couponId).orElse(null);
        model.addAttribute("coupon", coupon);
        model.addAttribute("shopId", shopId);
        return "updateForm";
    }

    @PostMapping("/update")
    public String updateCoupon(@ModelAttribute Coupon coupon,
                               RedirectAttributes redirectAttributes) {
        couponRepository.save(coupon);
        redirectAttributes.addAttribute("shopId", coupon.getWebsite().getId());
        return "redirect:/admin/coupon?shopId={shopId}";
    }

    @GetMapping("/delete")
    public String deleteCoupon(@RequestParam("couponId") Integer couponId,
                               @RequestParam("shopId") Integer shopId,
                               RedirectAttributes redirectAttributes) {
        couponRepository.deleteById(couponId);
        redirectAttributes.addAttribute("shopId", shopId);
        return "redirect:/admin/coupon?shopId={shopId}";
    }

    @GetMapping("/runScript")
    public CompletableFuture<ModelAndView> runScript(@RequestParam(value = "shopId", required = false) Integer websiteId) {
        LocalDateTime startTime = LocalDateTime.now();
        Record record = new Record();
        String websiteName = websiteId != null ? websiteRepository.findById(websiteId).orElse(null).getShopName() : "all";
        record.setWebsiteName(websiteName);
        record.setStartTime(startTime);
        record.setStatus("Running");
        recordRepository.save(record);
        CouponScraper couponScraper = new CouponScraper(couponRepository, websiteRepository, websiteId);
        CompletableFuture<Integer> resultFuture = couponScraper.getCouponsAsync();
        return resultFuture.thenApply(execResult -> {
            if (execResult == 0) {
                record.setStatus("Success");
            } else {
                record.setStatus("Failed");
            }
            record.setExitCode(execResult);
            record.setCompleteTime(LocalDateTime.now());
            recordRepository.save(record);
            return new ModelAndView("redirect:/admin/coupon?shopId=" + websiteId);
        });
    }
}
