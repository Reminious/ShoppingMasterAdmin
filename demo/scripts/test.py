from datetime import datetime

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException, NoSuchElementException
from selenium.webdriver.chrome.service import Service
import pandas as pd
import pyperclip
import time
import mysql.connector

df = pd.DataFrame({
    "URL": ["https://www.cuponation.com.sg/asus-discount-codes"],
    "Shop Name": ["ASUS"]
})


def connect_to_db():
    return mysql.connector.connect(
        host="localhost",
        user="root",
        passwd="123123",
        database="testdb")


def insert_coupon(webid, descrip, exp_date, disc, veri, cod, ter, p_code):
    db = connect_to_db()
    cursor = db.cursor()
    sql = "INSERT INTO coupon (website_id,description,expiration_date,discount,verified,code_or_deal,terms,promo_code) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)"
    val = (webid, descrip, exp_date, disc, veri, cod, ter, p_code)
    cursor.execute(sql, val)
    db.commit()
    cursor.close()
    db.close()


def find_website_id(sn):
    db = connect_to_db()
    cursor = db.cursor()
    sql = "SELECT id FROM website WHERE shop_name = %s"
    val = (sn,)
    cursor.execute(sql, val)
    result = cursor.fetchone()
    cursor.close()
    db.close()
    return result[0]


for i, row in df.iterrows():
    url = df.iloc[i]['URL']
    shop_name = df.iloc[i]['Shop Name']
    path = "G:/demo/scripts/chromedriver.exe"
    s = Service(executable_path=path)
    driver = webdriver.Chrome(service=s)
    driver.get(url)
    WebDriverWait(driver, 10).until(
        EC.presence_of_all_elements_located((By.CSS_SELECTOR, 'div._1abe9s9d'))
    )

    print("start of coupons for: ", shop_name)

    coupons_count = len(driver.find_elements(By.CSS_SELECTOR, 'div._1abe9s9d'))

    for index in range(coupons_count):
        coupons = driver.find_elements(By.CSS_SELECTOR, 'div._1abe9s9d')
        coupon = coupons[index]
        initial_windows = driver.window_handles
        current_window = driver.current_window_handle
        try:
            coupon_description = coupon.find_element(By.XPATH, './/h3[contains(@class, "_17t8r7p9")]').text
        except NoSuchElementException:
            coupon_description = "No description"

        try:
            expiration_date_str = coupon.find_element(By.CSS_SELECTOR,
                                                      'div._11tdtd10._17t8r7pd.couponShape div:last-child').text
            clean_expiration_date_str = expiration_date_str.strip(": ")
            expiration_date = datetime.strptime(clean_expiration_date_str, '%d/%m/%Y').date()
        except NoSuchElementException:
            expiration_date = datetime.strptime('01/01/2100', '%d/%m/%Y').date()

        try:
            discount1 = coupon.find_element(By.CSS_SELECTOR, 'span._1rzykvb0').text
            discount2 = coupon.find_element(By.CSS_SELECTOR, 'div._1295sb41 > span').text
            discount = discount1 + ' ' + discount2
        except NoSuchElementException:
            discount = "No discount"

        try:
            verified = coupon.find_element(By.CSS_SELECTOR, 'div._1l9o1jz9').text
        except NoSuchElementException:
            verified = "No verification status"

        try:
            code_or_deal = coupon.find_element(By.CSS_SELECTOR,
                                               'div._1l9o1jz0._1l9o1jz2._1l9o1jzd._1l9o1jzf._1l9o1jzg').text
        except NoSuchElementException:
            code_or_deal = "Unknown code or deal"

        try:
            terms_button = coupon.find_element(By.CSS_SELECTOR, 'button.ekdzs0')
            terms_button.click()
            time.sleep(1)
            terms = coupon.find_element(By.CSS_SELECTOR, 'div._1mq6bor6').text
        except NoSuchElementException:
            terms = "No terms"
        print(
            f"Coupon {index}: {coupon_description}, Expiration: {expiration_date}, Discount: {discount}, Verified: {verified}, Code or Deal: {code_or_deal}, Terms: {terms}")

        try:
            deal_button = coupon.find_element(By.CSS_SELECTOR, 'div._17t8r7ph div[role="button"]')
            deal_button.click()
            WebDriverWait(driver, 10).until(lambda d: len(d.window_handles) > len(initial_windows))
            new_window = [window for window in driver.window_handles if window not in initial_windows][0]
            driver.switch_to.window(new_window)
            try:
                WebDriverWait(driver, 10).until(
                    EC.presence_of_element_located((By.CSS_SELECTOR, 'div[role="dialog"]'))
                )
            except TimeoutException:
                print("Dialog did not appear for coupon at index", index)
                continue
            time.sleep(1)

            try:
                promo_code_button = WebDriverWait(driver, 2).until(
                    EC.presence_of_element_located(
                        (By.CSS_SELECTOR, 'span[data-testid="voucherPopup-codeHolder-voucherType-code-copyButton"]'))
                )
                promo_code_button.click()
                time.sleep(1)
                promo_code = pyperclip.paste()
                print(f'Promo Code for coupon {index}: {promo_code}')
            except TimeoutException:
                promo_code = "No promo code needed"
                print(f"No promo code needed for coupon {index}")

            close_button = driver.find_element(By.CSS_SELECTOR, 'button[aria-label="close"]')
            close_button.click()
            time.sleep(1)
            driver.switch_to.window(current_window)
            driver.close()
            driver.switch_to.window(new_window)
        except Exception as e:
            print(f"Error processing coupon at index {index}: {e}")
            continue

        insert_coupon(find_website_id(shop_name), coupon_description, expiration_date, discount, verified, code_or_deal,
                      terms, promo_code)
        print("--------------------------------------------------")

    driver.quit()
