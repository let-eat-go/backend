# notification type에 COMPLETED 추가 (enum)
alter table notification
    modify type enum ('REMIND', 'CANCEL', 'COMPLETED') not null;