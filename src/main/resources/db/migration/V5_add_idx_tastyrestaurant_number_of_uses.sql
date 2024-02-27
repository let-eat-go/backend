# tasty_restaurant 테이블에 인덱스 추가
alter table tasty_restaurant
    add index idx_tasty_restaurant_number_of_uses (number_of_uses);

# chatroom index 이름 변경
drop index idx_status on chat_room;

create index idx_chatroom_status
    on chat_room (status);