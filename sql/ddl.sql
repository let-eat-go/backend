create table if not exists leteatgo.member
(
    id                 bigint auto_increment
        primary key,
    created_at         datetime(6)                      null,
    updated_at         datetime(6)                      null,
    deleted_at         datetime(6)                      null,
    email              varchar(255)                     not null,
    introduce          varchar(255)                     not null,
    login_type         enum ('LOCAL', 'GOOGLE')         not null,
    manner_temperature double                           not null,
    nickname           varchar(255)                     not null,
    password           varchar(255)                     not null,
    phone_number       varchar(255)                     not null,
    profile_image      varchar(255)                     null,
    role               enum ('ROLE_ADMIN', 'ROLE_USER') not null,
    constraint UK_email
        unique (email),
    constraint UK_phone_number
        unique (phone_number)
);

create table if not exists leteatgo.tasty_restaurant
(
    id             bigint auto_increment
        primary key,
    created_at     datetime(6)                                                                                                                                                                                       null,
    updated_at     datetime(6)                                                                                                                                                                                       null,
    api_id         bigint                                                                                                                                                                                            not null,
    category       enum ('SNACK', 'STREET_FOOD', 'BUFFET', 'PUB', 'ASIAN_CUISINE', 'WESTERN_CUISINE', 'JAPANESE_CUISINE', 'CHINESE_CUISINE', 'FAST_FOOD', 'FAMILY_RESTAURANT', 'PIZZA', 'CHICKEN', 'KOREAN_CUISINE') not null,
    land_address   varchar(255)                                                                                                                                                                                      not null,
    latitude       double                                                                                                                                                                                            not null,
    longitude      double                                                                                                                                                                                            not null,
    name           varchar(255)                                                                                                                                                                                      not null,
    number_of_uses int                                                                                                                                                                                               not null,
    phone_number   varchar(255)                                                                                                                                                                                      not null,
    restaurant_url varchar(255)                                                                                                                                                                                      not null,
    road_address   varchar(255)                                                                                                                                                                                      not null,
    constraint uk_tasty_restaurant_api_id
        unique (api_id)
);

create index idx_tasty_restaurant_api_id
    on leteatgo.tasty_restaurant (api_id);


create table if not exists leteatgo.region
(
    id         bigint auto_increment
        primary key,
    created_at datetime(6)  null,
    updated_at datetime(6)  null,
    name       varchar(255) not null
);

create index idx_region_name
    on leteatgo.region (name);

create table if not exists leteatgo.meeting
(
    id                  bigint auto_increment
        primary key,
    created_at          datetime(6)                                                                                                                                                                                       null,
    updated_at          datetime(6)                                                                                                                                                                                       null,
    description         varchar(255)                                                                                                                                                                                      not null,
    max_participants    int                                                                                                                                                                                               not null,
    age_preference      enum ('ANY', 'TEEN', 'TWENTIES', 'THIRTIES', 'FORTIES', 'FIFTIES', 'SIXTIES', 'SEVENTIES', 'EIGHTIES')                                                                                            null,
    alcohol_preference  enum ('DISALLOWED', 'ALLOWED', 'ANY')                                                                                                                                                             null,
    gender_preference   enum ('MALE', 'FEMALE', 'ANY')                                                                                                                                                                    null,
    purpose             enum ('SOCIAL', 'MEAL_ONLY', 'DRINKING')                                                                                                                                                          null,
    status              enum ('BEFORE', 'IN_PROGRESS', 'COMPLETED', 'CANCELED')                                                                                                                                           null,
    min_participants    int                                                                                                                                                                                               not null,
    name                varchar(255)                                                                                                                                                                                      not null,
    restaurant_category enum ('SNACK', 'STREET_FOOD', 'BUFFET', 'PUB', 'ASIAN_CUISINE', 'WESTERN_CUISINE', 'JAPANESE_CUISINE', 'CHINESE_CUISINE', 'FAST_FOOD', 'FAMILY_RESTAURANT', 'PIZZA', 'CHICKEN', 'KOREAN_CUISINE') not null,
    start_date_time     datetime(6)                                                                                                                                                                                       not null,
    host_id             bigint                                                                                                                                                                                            not null,
    region_id           bigint                                                                                                                                                                                            null,
    tasty_restaurant_id bigint                                                                                                                                                                                            null,
    constraint FK_meeting_host
        foreign key (host_id) references leteatgo.member (id),
    constraint FK_meeting_region
        foreign key (region_id) references leteatgo.region (id),
    constraint FK_meeting_tasty_restaurant
        foreign key (tasty_restaurant_id) references leteatgo.tasty_restaurant (id)
);

create index idx_meeting_createdAt
    on leteatgo.meeting (created_at);

create index idx_meeting_restaurantCategory
    on leteatgo.meeting (restaurant_category);

create table if not exists leteatgo.meeting_participant
(
    id         bigint auto_increment
        primary key,
    created_at datetime(6) null,
    updated_at datetime(6) null,
    meeting_id bigint      not null,
    member_id  bigint      not null,
    constraint FK_meeting_participant_meeting
        foreign key (meeting_id) references leteatgo.meeting (id),
    constraint FK_meeting_participant_member
        foreign key (member_id) references leteatgo.member (id)
);

create table if not exists leteatgo.chat_room
(
    id         bigint auto_increment
        primary key,
    created_at datetime(6)            null,
    updated_at datetime(6)            null,
    status     enum ('OPEN', 'CLOSE') not null,
    meeting_id bigint                 not null,
    constraint UK_79phtmpe8rh07un3hjuu2wd60
        unique (meeting_id),
    constraint fk_chatroom_meeting
        foreign key (meeting_id) references leteatgo.meeting (id)
);

create table if not exists leteatgo.chat_message
(
    id               bigint auto_increment
        primary key,
    created_at       datetime(6)      null,
    updated_at       datetime(6)      null,
    content          varchar(255)     not null,
    is_read          bit default b'0' not null,
    chatting_room_id bigint           not null,
    sender_id        bigint           not null,
    constraint fk_chatroom_chatmessage
        foreign key (chatting_room_id) references leteatgo.chat_room (id),
    constraint fk_sender_chatmessage
        foreign key (sender_id) references leteatgo.member (id)
);

create table if not exists leteatgo.notification
(
    id          bigint auto_increment
        primary key,
    content     varchar(255)              not null,
    is_read     bit                       not null,
    type        enum ('REMIND', 'CANCEL') not null,
    receiver_id bigint                    not null,
    constraint fk_notification_receiver
        foreign key (receiver_id) references leteatgo.member (id)
);

create table if not exists leteatgo.review
(
    id          bigint auto_increment
        primary key,
    score       double not null,
    meeting_id  bigint not null,
    reviewee_id bigint not null,
    reviewer_id bigint not null,
    constraint fk_review_meeting
        foreign key (meeting_id) references leteatgo.meeting (id),
    constraint fk_reviewee
        foreign key (reviewee_id) references leteatgo.member (id),
    constraint fk_reviewer
        foreign key (reviewer_id) references leteatgo.member (id)
);