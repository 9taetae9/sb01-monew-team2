DROP TABLE IF EXISTS "comment_likes";
DROP TABLE IF EXISTS "comments";
DROP TABLE IF EXISTS "notifications";
DROP TABLE IF EXISTS "interest_keywords";
DROP TABLE IF EXISTS "subscriptions";
DROP TABLE IF EXISTS "article_views";
DROP TABLE IF EXISTS "articles";
DROP TABLE IF EXISTS "users";
DROP TABLE IF EXISTS "interests";
DROP TABLE IF EXISTS "keywords";

CREATE TABLE "interests"
(
    "id"               UUID             NOT NULL PRIMARY KEY,
    "name"             VARCHAR(100)     NOT NULL UNIQUE,
    "subscriber_count" BIGINT DEFAULT 0 NOT NULL,
    "created_at"       TIMESTAMP        NOT NULL,
    "updated_at"       TIMESTAMP        NULL
);

CREATE TABLE "keywords"
(
    "id"         UUID        NOT NULL PRIMARY KEY,
    "name"       VARCHAR(50) NOT NULL UNIQUE,
    "created_at" TIMESTAMP   NOT NULL,
    "updated_at" TIMESTAMP   NULL
);

CREATE TABLE "interest_keywords"
(
    "id"          UUID      NOT NULL PRIMARY KEY,
    "interest_id" UUID      NOT NULL,
    "keyword_id"  UUID      NOT NULL,
    "created_at"  TIMESTAMP NOT NULL,
    "updated_at"  TIMESTAMP NULL,
    FOREIGN KEY ("interest_id") REFERENCES "interests" ("id"),
    FOREIGN KEY ("keyword_id") REFERENCES "keywords" ("id")
);

CREATE TABLE "users"
(
    "id"         UUID                  NOT NULL PRIMARY KEY,
    "email"      VARCHAR(255)          NOT NULL UNIQUE,
    "nickname"   VARCHAR(100)          NOT NULL UNIQUE,
    "password"   VARCHAR(255)          NOT NULL,
    "deleted"    BOOLEAN DEFAULT false NOT NULL,
    "created_at" TIMESTAMP             NOT NULL,
    "updated_at" TIMESTAMP             NULL
);

CREATE TABLE "articles"
(
    "id"             UUID         NOT NULL PRIMARY KEY,
    "interest_id"    UUID         NOT NULL,
    "title"          VARCHAR(255) NOT NULL,
    "source"         VARCHAR(255) NOT NULL,
    "source_url"     VARCHAR(255) NOT NULL UNIQUE,
    "summary"        TEXT         NULL,
    "view_count"     BIGINT       NOT NULL DEFAULT 0,
    "published_date" TIMESTAMP    NOT NULL,
    "deleted"        BOOLEAN      NOT NULL DEFAULT false,
    "created_at"     TIMESTAMP    NOT NULL,
    "updated_at"     TIMESTAMP    NULL,
    FOREIGN KEY ("interest_id") REFERENCES "interests" ("id")
);

CREATE TABLE "article_views"
(
    "id"         UUID      NOT NULL PRIMARY KEY,
    "user_id"    UUID      NOT NULL UNIQUE,
    "article_id" UUID      NOT NULL UNIQUE,
    "viewed_at"  TIMESTAMP NOT NULL,
    "created_at" TIMESTAMP NOT NULL,
    "updated_at" TIMESTAMP NULL,
    FOREIGN KEY ("user_id") REFERENCES "users" ("id"),
    FOREIGN KEY ("article_id") REFERENCES "articles" ("id")
);

CREATE TABLE "subscriptions"
(
    "id"          UUID      NOT NULL PRIMARY KEY,
    "user_id"     UUID      NOT NULL,
    "interest_id" UUID      NOT NULL,
    "created_at"  TIMESTAMP NOT NULL,
    "updated_at"  TIMESTAMP NULL,
    FOREIGN KEY ("user_id") REFERENCES "users" ("id"),
    FOREIGN KEY ("interest_id") REFERENCES "interests" ("id")
);

CREATE TABLE "comments"
(
    "id"         UUID      NOT NULL PRIMARY KEY,
    "user_id"    UUID      NOT NULL,
    "article_id" UUID      NOT NULL,
    "content"    TEXT      NOT NULL,
    "like_count" BIGINT    NOT NULL DEFAULT 0,
    "deleted"    BOOLEAN   NOT NULL DEFAULT false,
    "created_at" TIMESTAMP NOT NULL,
    "updated_at" TIMESTAMP NOT NULL,
    FOREIGN KEY ("user_id") REFERENCES "users" ("id"),
    FOREIGN KEY ("article_id") REFERENCES "articles" ("id")
);

CREATE TABLE "comment_likes"
(
    "id"         UUID      NOT NULL PRIMARY KEY,
    "user_id"    UUID      NOT NULL UNIQUE,
    "comment_id" UUID      NOT NULL UNIQUE,
    "liked_at"   TIMESTAMP NOT NULL,
    "created_at" TIMESTAMP NOT NULL,
    "updated_at" TIMESTAMP NULL,
    FOREIGN KEY ("user_id") REFERENCES "users" ("id"),
    FOREIGN KEY ("comment_id") REFERENCES "comments" ("id")
);

CREATE TABLE "notifications"
(
    "id"            UUID         NOT NULL PRIMARY KEY,
    "user_id"       UUID         NOT NULL,
    "content"       VARCHAR(255) NOT NULL,
    "resource_type" VARCHAR(50)  NOT NULL,
    "resource_id"   UUID         NOT NULL,
    "confirmed"     BOOLEAN      NOT NULL DEFAULT false,
    "created_at"    TIMESTAMP    NULL,
    "updated_at"    TIMESTAMP    NULL,
    FOREIGN KEY ("user_id") REFERENCES "users" ("id")
);
