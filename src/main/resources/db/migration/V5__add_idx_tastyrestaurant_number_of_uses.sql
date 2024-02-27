# tasty_restaurant 테이블에 인덱스 추가
alter table tasty_restaurant
    add index idx_tasty_restaurant_number_of_uses (number_of_uses);