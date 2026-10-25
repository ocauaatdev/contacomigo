-- =====================================================================
-- V1__init.sql — schema inicial do ContaComigo
-- Espelha as entidades JPA (User, Conversation, Message, Transaction).
-- Regra de ouro: depois que este arquivo rodar uma vez, NUNCA edite.
-- Qualquer mudança futura vai em um arquivo novo (V2__..., V3__...).
-- =====================================================================

CREATE TABLE users (
                       id       UUID         NOT NULL,
                       name     VARCHAR(255) NOT NULL,
                       email    VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       CONSTRAINT pk_users PRIMARY KEY (id),
                       CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE conversation (
                              id         UUID         NOT NULL,
                              title      VARCHAR(255),
                              created_at TIMESTAMP(6) NOT NULL,
                              updated_at TIMESTAMP(6) NOT NULL,
                              user_id    UUID         NOT NULL,
                              CONSTRAINT pk_conversation PRIMARY KEY (id),
                              CONSTRAINT fk_conversation_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE message (
                         id              UUID         NOT NULL,
                         sender          VARCHAR(255) NOT NULL,
                         content         TEXT         NOT NULL,
                         created_at      TIMESTAMP(6) NOT NULL,
                         conversation_id UUID         NOT NULL,
                         CONSTRAINT pk_message PRIMARY KEY (id),
                         CONSTRAINT fk_message_conversation FOREIGN KEY (conversation_id) REFERENCES conversation (id),
                         CONSTRAINT ck_message_sender CHECK (sender IN ('USER', 'ASSISTANT'))
);

CREATE TABLE transaction (
                             id               UUID          NOT NULL,
                             type             VARCHAR(255)  NOT NULL,
                             amount           NUMERIC(38,2) NOT NULL,
                             description      VARCHAR(255),
                             payment_method   VARCHAR(255),
                             category         VARCHAR(255),
                             transaction_date DATE,
                             created_at       TIMESTAMP(6)  NOT NULL,
                             user_id          UUID          NOT NULL,
                             conversation_id  UUID,
                             CONSTRAINT pk_transaction PRIMARY KEY (id),
                             CONSTRAINT fk_transaction_user FOREIGN KEY (user_id) REFERENCES users (id),
                             CONSTRAINT fk_transaction_conversation FOREIGN KEY (conversation_id) REFERENCES conversation (id),
                             CONSTRAINT ck_transaction_type CHECK (type IN ('INCOME', 'EXPENSE')),
                             CONSTRAINT ck_transaction_payment_method CHECK (payment_method IN ('PIX', 'CREDIT', 'DEBIT', 'CASH')),
                             CONSTRAINT ck_transaction_category CHECK (category IN
                                                                       ('FOOD', 'TRANSPORT', 'ENTERTAINMENT', 'HEALTH', 'EDUCATION', 'UTILITIES', 'OTHER'))
);

-- O PostgreSQL NÃO cria índice automático nas colunas de chave estrangeira.
CREATE INDEX idx_conversation_user_updated ON conversation (user_id, updated_at DESC);
CREATE INDEX idx_message_conversation_created ON message (conversation_id, created_at DESC);
CREATE INDEX idx_transaction_user_date ON transaction (user_id, transaction_date DESC);
CREATE INDEX idx_transaction_conversation ON transaction (conversation_id);