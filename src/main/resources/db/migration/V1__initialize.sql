-- Initial database schema for Messaging API

CREATE TABLE "user"
(
    id                uuid UNIQUE NOT NULL,
    subscription_type text        NOT NULL,
    CONSTRAINT user_pkey PRIMARY KEY (id)
);

CREATE TABLE user_resource
(
    resource_uri text                        NOT NULL,
    user_id      uuid,
    created      timestamp without time zone NOT NULL DEFAULT NOW()
);

CREATE TABLE resource
(
    uri         text UNIQUE NOT NULL,
    type        text        NOT NULL,
    application text        NOT NULL,
    CONSTRAINT resource_pkey PRIMARY KEY (uri)
);