CREATE TABLE coupon
(
    id            UUID         NOT NULL,
    code          VARCHAR(255) NOT NULL,
    country       VARCHAR(255) NOT NULL,
    max_usage     INTEGER      NOT NULL,
    current_usage INTEGER      NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    version       INTEGER      NOT NULL,
    CONSTRAINT pk_coupon PRIMARY KEY (id)
);

CREATE TABLE coupon_redemption
(
    id          UUID         NOT NULL,
    coupon_id   UUID         NOT NULL,
    user_id     UUID         NOT NULL,
    redeemed_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    ip          VARCHAR(255) NOT NULL,
    country     VARCHAR(2)   NOT NULL,
    version     INTEGER      NOT NULL,
    CONSTRAINT pk_couponredemption PRIMARY KEY (id)
);

ALTER TABLE coupon
    ADD CONSTRAINT uc_coupon_code UNIQUE (code);

ALTER TABLE coupon_redemption
    ADD CONSTRAINT unique_coupon_redemption_coupon_id_user_id UNIQUE (coupon_id, user_id);

CREATE INDEX index_coupon_country ON coupon (country);

CREATE INDEX index_coupon_created_at ON coupon (created_at);

CREATE INDEX index_coupon_redemption_coupon_id ON coupon_redemption (coupon_id);

CREATE INDEX index_coupon_redemption_redeemed_at ON coupon_redemption (redeemed_at);

CREATE INDEX index_coupon_redemption_user_id ON coupon_redemption (user_id);