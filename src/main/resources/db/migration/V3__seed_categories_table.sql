INSERT INTO categories (name)
VALUES ('Еда'),
       ('Кафе'),
       ('Транспорт'),
       ('Жилье'),
       ('Здоровье'),
       ('Развлечения'),
       ('Одежда'),
       ('Связь'),
       ('Подарки'),
       ('Прочее');

CREATE UNIQUE INDEX idx_unique_system_category
    ON categories (name)
    WHERE user_id IS NULL;