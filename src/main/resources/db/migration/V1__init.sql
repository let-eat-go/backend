CREATE TABLE `BATCH_JOB_EXECUTION`
(
    `JOB_EXECUTION_ID` bigint      NOT NULL,
    `VERSION`          bigint        DEFAULT NULL,
    `JOB_INSTANCE_ID`  bigint      NOT NULL,
    `CREATE_TIME`      datetime(6) NOT NULL,
    `START_TIME`       datetime(6)   DEFAULT NULL,
    `END_TIME`         datetime(6)   DEFAULT NULL,
    `STATUS`           varchar(10)   DEFAULT NULL,
    `EXIT_CODE`        varchar(2500) DEFAULT NULL,
    `EXIT_MESSAGE`     varchar(2500) DEFAULT NULL,
    `LAST_UPDATED`     datetime(6)   DEFAULT NULL,
    PRIMARY KEY (`JOB_EXECUTION_ID`),
    KEY `JOB_INST_EXEC_FK` (`JOB_INSTANCE_ID`),
    CONSTRAINT `JOB_INST_EXEC_FK` FOREIGN KEY (`JOB_INSTANCE_ID`) REFERENCES `BATCH_JOB_INSTANCE` (`JOB_INSTANCE_ID`)
);

CREATE TABLE `BATCH_JOB_EXECUTION_CONTEXT`
(
    `JOB_EXECUTION_ID`   bigint        NOT NULL,
    `SHORT_CONTEXT`      varchar(2500) NOT NULL,
    `SERIALIZED_CONTEXT` text,
    PRIMARY KEY (`JOB_EXECUTION_ID`),
    CONSTRAINT `JOB_EXEC_CTX_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `BATCH_JOB_EXECUTION` (`JOB_EXECUTION_ID`)
);

CREATE TABLE `BATCH_JOB_EXECUTION_PARAMS`
(
    `JOB_EXECUTION_ID` bigint       NOT NULL,
    `PARAMETER_NAME`   varchar(100) NOT NULL,
    `PARAMETER_TYPE`   varchar(100) NOT NULL,
    `PARAMETER_VALUE`  varchar(2500) DEFAULT NULL,
    `IDENTIFYING`      char(1)      NOT NULL,
    KEY `JOB_EXEC_PARAMS_FK` (`JOB_EXECUTION_ID`),
    CONSTRAINT `JOB_EXEC_PARAMS_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `BATCH_JOB_EXECUTION` (`JOB_EXECUTION_ID`)
);

CREATE TABLE `BATCH_JOB_EXECUTION_SEQ`
(
    `ID`         bigint  NOT NULL,
    `UNIQUE_KEY` char(1) NOT NULL,
    UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
);

CREATE TABLE `BATCH_JOB_INSTANCE`
(
    `JOB_INSTANCE_ID` bigint       NOT NULL,
    `VERSION`         bigint DEFAULT NULL,
    `JOB_NAME`        varchar(100) NOT NULL,
    `JOB_KEY`         varchar(32)  NOT NULL,
    PRIMARY KEY (`JOB_INSTANCE_ID`),
    UNIQUE KEY `JOB_INST_UN` (`JOB_NAME`, `JOB_KEY`)
);

CREATE TABLE `BATCH_JOB_SEQ`
(
    `ID`         bigint  NOT NULL,
    `UNIQUE_KEY` char(1) NOT NULL,
    UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
);

CREATE TABLE `BATCH_STEP_EXECUTION`
(
    `STEP_EXECUTION_ID`  bigint       NOT NULL,
    `VERSION`            bigint       NOT NULL,
    `STEP_NAME`          varchar(100) NOT NULL,
    `JOB_EXECUTION_ID`   bigint       NOT NULL,
    `CREATE_TIME`        datetime(6)  NOT NULL,
    `START_TIME`         datetime(6)   DEFAULT NULL,
    `END_TIME`           datetime(6)   DEFAULT NULL,
    `STATUS`             varchar(10)   DEFAULT NULL,
    `COMMIT_COUNT`       bigint        DEFAULT NULL,
    `READ_COUNT`         bigint        DEFAULT NULL,
    `FILTER_COUNT`       bigint        DEFAULT NULL,
    `WRITE_COUNT`        bigint        DEFAULT NULL,
    `READ_SKIP_COUNT`    bigint        DEFAULT NULL,
    `WRITE_SKIP_COUNT`   bigint        DEFAULT NULL,
    `PROCESS_SKIP_COUNT` bigint        DEFAULT NULL,
    `ROLLBACK_COUNT`     bigint        DEFAULT NULL,
    `EXIT_CODE`          varchar(2500) DEFAULT NULL,
    `EXIT_MESSAGE`       varchar(2500) DEFAULT NULL,
    `LAST_UPDATED`       datetime(6)   DEFAULT NULL,
    PRIMARY KEY (`STEP_EXECUTION_ID`),
    KEY `JOB_EXEC_STEP_FK` (`JOB_EXECUTION_ID`),
    CONSTRAINT `JOB_EXEC_STEP_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `BATCH_JOB_EXECUTION` (`JOB_EXECUTION_ID`)
);

CREATE TABLE `BATCH_STEP_EXECUTION_CONTEXT`
(
    `STEP_EXECUTION_ID`  bigint        NOT NULL,
    `SHORT_CONTEXT`      varchar(2500) NOT NULL,
    `SERIALIZED_CONTEXT` text,
    PRIMARY KEY (`STEP_EXECUTION_ID`),
    CONSTRAINT `STEP_EXEC_CTX_FK` FOREIGN KEY (`STEP_EXECUTION_ID`) REFERENCES `BATCH_STEP_EXECUTION` (`STEP_EXECUTION_ID`)
);

CREATE TABLE `BATCH_STEP_EXECUTION_SEQ`
(
    `ID`         bigint  NOT NULL,
    `UNIQUE_KEY` char(1) NOT NULL,
    UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
);

CREATE TABLE `chat_message`
(
    `id`               bigint       NOT NULL AUTO_INCREMENT,
    `created_at`       datetime(6)           DEFAULT NULL,
    `updated_at`       datetime(6)           DEFAULT NULL,
    `content`          varchar(255) NOT NULL,
    `is_read`          bit(1)       NOT NULL DEFAULT b'0',
    `chatting_room_id` bigint       NOT NULL,
    `sender_id`        bigint       NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_chatroom_chatmessage` (`chatting_room_id`),
    KEY `fk_sender_chatmessage` (`sender_id`),
    CONSTRAINT `fk_chatroom_chatmessage` FOREIGN KEY (`chatting_room_id`) REFERENCES `chat_room` (`id`),
    CONSTRAINT `fk_sender_chatmessage` FOREIGN KEY (`sender_id`) REFERENCES `member` (`id`)
);

CREATE TABLE `chat_room`
(
    `id`         bigint                NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    `status`     enum ('OPEN','CLOSE') NOT NULL,
    `meeting_id` bigint                NOT NULL,
    PRIMARY KEY (`id`),
    KEY `Fk_chatroom_meeting` (`meeting_id`),
    KEY `idx_chatroom_status` (`status`),
    CONSTRAINT `Fk_chatroom_meeting` FOREIGN KEY (`meeting_id`) REFERENCES `meeting` (`id`)
);

CREATE TABLE `meeting`
(
    `id`                   bigint                                                                                                                                                                                NOT NULL AUTO_INCREMENT,
    `created_at`           datetime(6)                                                                                    DEFAULT NULL,
    `updated_at`           datetime(6)                                                                                    DEFAULT NULL,
    `description`          varchar(255)                                                                                                                                                                          NOT NULL,
    `max_participants`     int                                                                                                                                                                                   NOT NULL,
    `age_preference`       enum ('ANY','TEEN','TWENTIES','THIRTIES','FORTIES','FIFTIES','SIXTIES','SEVENTIES','EIGHTIES') DEFAULT NULL,
    `alcohol_preference`   enum ('DISALLOWED','ALLOWED','ANY')                                                            DEFAULT NULL,
    `gender_preference`    enum ('MALE','FEMALE','ANY')                                                                   DEFAULT NULL,
    `purpose`              enum ('SOCIAL','MEAL_ONLY','DRINKING')                                                         DEFAULT NULL,
    `status`               enum ('BEFORE','IN_PROGRESS','COMPLETED','CANCELED')                                           DEFAULT NULL,
    `min_participants`     int                                                                                                                                                                                   NOT NULL,
    `name`                 varchar(255)                                                                                                                                                                          NOT NULL,
    `restaurant_category`  enum ('SNACK','STREET_FOOD','BUFFET','PUB','ASIAN_CUISINE','WESTERN_CUISINE','JAPANESE_CUISINE','CHINESE_CUISINE','FAST_FOOD','FAMILY_RESTAURANT','PIZZA','CHICKEN','KOREAN_CUISINE') NOT NULL,
    `start_date_time`      datetime(6)                                                                                                                                                                           NOT NULL,
    `host_id`              bigint                                                                                                                                                                                NOT NULL,
    `region_id`            bigint                                                                                         DEFAULT NULL,
    `tasty_restaurant_id`  bigint                                                                                         DEFAULT NULL,
    `current_participants` int                                                                                                                                                                                   NOT NULL,
    `cancel_reason`        varchar(255)                                                                                   DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_meeting_host` (`host_id`),
    KEY `FK_meeting_region` (`region_id`),
    KEY `FK_meeting_tasty_restaurant` (`tasty_restaurant_id`),
    KEY `idx_meeting_restaurantCategory` (`restaurant_category`),
    KEY `idx_meeting_createdAt` (`created_at`),
    CONSTRAINT `FK_meeting_host` FOREIGN KEY (`host_id`) REFERENCES `member` (`id`),
    CONSTRAINT `FK_meeting_region` FOREIGN KEY (`region_id`) REFERENCES `region` (`id`),
    CONSTRAINT `FK_meeting_tasty_restaurant` FOREIGN KEY (`tasty_restaurant_id`) REFERENCES `tasty_restaurant` (`id`)
);

CREATE TABLE `meeting_participant`
(
    `id`         bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    `meeting_id` bigint NOT NULL,
    `member_id`  bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_meeting_participant_meeting` (`meeting_id`),
    KEY `FK_meeting_participant_member` (`member_id`),
    CONSTRAINT `FK_meeting_participant_meeting` FOREIGN KEY (`meeting_id`) REFERENCES `meeting` (`id`),
    CONSTRAINT `FK_meeting_participant_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
);

CREATE TABLE `member`
(
    `id`                 bigint                          NOT NULL AUTO_INCREMENT,
    `created_at`         datetime(6)  DEFAULT NULL,
    `updated_at`         datetime(6)  DEFAULT NULL,
    `deleted_at`         datetime(6)  DEFAULT NULL,
    `email`              varchar(255)                    NOT NULL,
    `introduce`          varchar(255)                    NOT NULL,
    `login_type`         enum ('LOCAL','GOOGLE')         NOT NULL,
    `manner_temperature` double                          NOT NULL,
    `nickname`           varchar(255)                    NOT NULL,
    `password`           varchar(255)                    NOT NULL,
    `phone_number`       varchar(255)                    NOT NULL,
    `profile_image`      varchar(255) DEFAULT NULL,
    `role`               enum ('ROLE_ADMIN','ROLE_USER') NOT NULL,
    `profile_filename`   varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_email` (`email`),
    UNIQUE KEY `UK_phone_number` (`phone_number`)
);

CREATE TABLE `notification`
(
    `id`          bigint                   NOT NULL AUTO_INCREMENT,
    `content`     varchar(255)             NOT NULL,
    `is_read`     bit(1)                   NOT NULL,
    `type`        enum ('REMIND','CANCEL') NOT NULL,
    `receiver_id` bigint                   NOT NULL,
    `created_at`  datetime(6) DEFAULT NULL,
    `updated_at`  datetime(6) DEFAULT NULL,
    `related_url` varchar(255)             NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_notification_receiver` (`receiver_id`),
    CONSTRAINT `fk_notification_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `member` (`id`)
);

CREATE TABLE `region`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) DEFAULT NULL,
    `updated_at` datetime(6) DEFAULT NULL,
    `name`       varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_region_name` (`name`)
);

CREATE TABLE `review`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `score`       double NOT NULL,
    `meeting_id`  bigint NOT NULL,
    `reviewee_id` bigint NOT NULL,
    `reviewer_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_review_meeting` (`meeting_id`),
    KEY `fk_reviewee` (`reviewee_id`),
    KEY `fk_reviewer` (`reviewer_id`),
    CONSTRAINT `fk_review_meeting` FOREIGN KEY (`meeting_id`) REFERENCES `meeting` (`id`),
    CONSTRAINT `fk_reviewee` FOREIGN KEY (`reviewee_id`) REFERENCES `member` (`id`),
    CONSTRAINT `fk_reviewer` FOREIGN KEY (`reviewer_id`) REFERENCES `member` (`id`)
);

CREATE TABLE `tasty_restaurant`
(
    `id`             bigint                                                                                                                                                                                NOT NULL AUTO_INCREMENT,
    `created_at`     datetime(6) DEFAULT NULL,
    `updated_at`     datetime(6) DEFAULT NULL,
    `api_id`         bigint                                                                                                                                                                                NOT NULL,
    `category`       enum ('SNACK','STREET_FOOD','BUFFET','PUB','ASIAN_CUISINE','WESTERN_CUISINE','JAPANESE_CUISINE','CHINESE_CUISINE','FAST_FOOD','FAMILY_RESTAURANT','PIZZA','CHICKEN','KOREAN_CUISINE') NOT NULL,
    `land_address`   varchar(255)                                                                                                                                                                          NOT NULL,
    `latitude`       double                                                                                                                                                                                NOT NULL,
    `longitude`      double                                                                                                                                                                                NOT NULL,
    `name`           varchar(255)                                                                                                                                                                          NOT NULL,
    `number_of_uses` int                                                                                                                                                                                   NOT NULL,
    `phone_number`   varchar(255)                                                                                                                                                                          NOT NULL,
    `restaurant_url` varchar(255)                                                                                                                                                                          NOT NULL,
    `road_address`   varchar(255)                                                                                                                                                                          NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tasty_restaurant_api_id` (`api_id`),
    KEY `idx_tasty_restaurant_api_id` (`api_id`)
);

