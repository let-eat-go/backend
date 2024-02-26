# chat_room 테이블의 status 컬럼에 인덱스 생성
alter table chat_room
    add index idx_status (status);