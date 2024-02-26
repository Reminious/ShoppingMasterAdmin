package com.example.demo.utili;

import com.example.demo.Entity.Coupon;
import com.example.demo.Repository.CouponRepository;
import com.example.demo.Repository.WebsiteRepository;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class CouponScraper {
    private final CouponRepository couponRepository;
    private final WebsiteRepository websiteRepository;
    private Integer websiteId;

    @Autowired
    public CouponScraper(CouponRepository couponRepository,
                         WebsiteRepository websiteRepository) {
        this.couponRepository = couponRepository;
        this.websiteRepository = websiteRepository;
    }

    public CouponScraper(CouponRepository couponRepository,
                         WebsiteRepository websiteRepository,
                         Integer websiteId) {
        this(couponRepository, websiteRepository); // 调用另一个构造函数
        this.websiteId = websiteId;
    }

    @Async
    public CompletableFuture<Integer> getCouponsAsync() {
        int result = getCoupons();
        return CompletableFuture.completedFuture(result);
    }

    public int getCoupons() {
        WebDriver driver = null;
        try {
            String[] urls = websiteId == null ? websiteRepository.findAllUrls().toArray(new String[0]) :
                    new String[]{Objects.requireNonNull(websiteRepository.findById(websiteId).orElse(null)).getUrl()};
            String[] shopNames = urls.length == 1 ? new String[]{Objects.requireNonNull(websiteRepository.findById(websiteId).orElse(null)).getShopName()} :
                    new String[]{"all shops"};

            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                String shopName = shopNames[i];
                String projectPath = System.getProperty("user.dir");
                String path =  projectPath + "/scripts/chromedriver.exe";
                System.setProperty("webdriver.chrome.driver", path);
                ChromeOptions options = new ChromeOptions();
                driver = new ChromeDriver(options);
                driver.get(url);
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                System.out.println("Start of coupons for: " + shopName);
                int coupons_count = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div._1abe9s9d"))).size();
                for (int index = 0; index < coupons_count; index++) {
                    List<WebElement> coupons = driver.findElements(By.cssSelector("div._1abe9s9d"));
                    WebElement coupon = coupons.get(index);
                    String couponDescription;
                    LocalDate expirationDate;
                    String discount;
                    String verified;
                    String codeOrDeal;
                    String terms;
                    String promoCode;
                    String promoUrl;

                    Set<String> initialWindows = driver.getWindowHandles();
                    String currentWindow = driver.getWindowHandle();

                    try {
                        couponDescription = coupon.findElement(new By.ByXPath(".//h3[contains(@class, \"_17t8r7p9\")]")).getText();
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        couponDescription = "No description";
                    }

                    try {
                        String expiration_date_str = coupon.findElement(By.cssSelector("div._11tdtd10._17t8r7pd.couponShape div:last-child")).getText();
                        //clean_expiration_date_str = expiration_date_str.strip(": ")
                        String clean_expiration_date_str = expiration_date_str.replaceAll("[^0-9/]", "");
                        expirationDate = LocalDate.parse(clean_expiration_date_str, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        expirationDate = LocalDate.parse("01/01/2100", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    }

                    try {
                        discount = coupon.findElement(By.cssSelector("span._1rzykvb0")).getText() + " " + coupon.findElement(By.cssSelector("div._1295sb41 > span")).getText();
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        discount = "No discount";
                    }

                    try {
                        verified = coupon.findElement(By.cssSelector("div._1l9o1jz9")).getText();
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        verified = "No verification status";
                    }

                    try {
                        codeOrDeal = coupon.findElement(By.cssSelector("div._1l9o1jz0._1l9o1jz2._1l9o1jzd._1l9o1jzf._1l9o1jzg")).getText();
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        codeOrDeal = "Unknown code or deal";
                    }

                    try {
                        WebElement termsButton = coupon.findElement(By.cssSelector("button.ekdzs0"));
                        termsButton.click();
                        Thread.sleep(1000);
                        terms = coupon.findElement(By.cssSelector("div._1mq6bor6")).getText();
                    } catch (org.openqa.selenium.NoSuchElementException | InterruptedException e) {
                        terms = "No terms";
                    }

                    try {
                        WebElement dealButton = coupon.findElement(By.cssSelector("div._17t8r7ph div[role='button']"));
                        dealButton.click();

                        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));
                        wait1.until(ExpectedConditions.numberOfWindowsToBe(initialWindows.size() + 1));

                        Set<String> allWindowHandles = driver.getWindowHandles();
                        String newWindowHandle = null;
                        for (String windowHandle : allWindowHandles) {
                            if (!initialWindows.contains(windowHandle)) {
                                newWindowHandle = windowHandle;
                                break;
                            }
                        }
                        driver.switchTo().window(newWindowHandle);

                        WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
                        try {
                            wait2.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[role='dialog']")));
                        }catch (TimeoutException e){
                            System.out.println("No dialog found");
                            continue;
                        }

                        Thread.sleep(1000); // 使用Thread.sleep()方法暂停执行1秒

                        try{
                            WebElement promoCodeElement = wait2.until(ExpectedConditions.presenceOfElementLocated(
                                    By.cssSelector("span[data-testid='voucherPopup-codeHolder-voucherType-code'] h4")));
                            Thread.sleep(1000); // 使用Thread.sleep()方法暂停执行1秒
                            promoCode = promoCodeElement.getText();
                        }catch (TimeoutException | NoSuchElementException e){
                            promoCode = "No promo code needed";
                        }
                        WebElement closeButton = driver.findElement(By.cssSelector("button[aria-label='close']"));
                        closeButton.click();
                        Thread.sleep(1000); // 使用Thread.sleep()方法暂停执行1秒
                        driver.switchTo().window(currentWindow);
                        Thread.sleep(3000);
                        promoUrl = driver.getCurrentUrl();
                        driver.close();
                        driver.switchTo().window(newWindowHandle);
                    } catch (Exception e) {
                        System.out.println("Error processing coupon " + ": " + e.getMessage());
                        return 1; // 返回1表示出现错误
                    }

                    Coupon couponEntity = new Coupon();
                    couponEntity.setDescription(couponDescription);
                    couponEntity.setExpirationDate(expirationDate);
                    couponEntity.setDiscount(discount);
                    couponEntity.setVerified(verified);
                    couponEntity.setCodeOrDeal(codeOrDeal);
                    couponEntity.setTerms(terms);
                    couponEntity.setPromoCode(promoCode);
                    couponEntity.setPromoUrl(promoUrl);
                    couponEntity.setWebsite(websiteRepository.findByUrl(url));
                    couponRepository.save(couponEntity);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in getCoupons method: " + e.getMessage());
            return 1; // 返回1表示出现错误
        }finally {
            if (driver != null) {
                driver.quit();
            }
        }
        return 0;
    }
}

