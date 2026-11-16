-- ЛР №6: додаткові тестові дані. Виконати ОДИН раз ПІСЛЯ lab6_migration.sql.

INSERT INTO animators (name, role, experience, notes) VALUES
                                                          ('Софія',  'Ведучий',  7, 'Квести та ігри на природі'),
                                                          ('Максим', 'Аніматор', 4, 'Шоу мильних бульбашок'),
                                                          ('Олег',   'Аніматор', 1, 'Новачок, допомагає з реквізитом');

INSERT INTO programs (name, description) VALUES
                                             ('Паперове шоу',          'Костюми й декорації з паперу, майстер-клас для дітей'),
                                             ('Шоу мильних бульбашок', 'Гігантські бульбашки та світлові ефекти');

-- Сценарій УСПІХУ (COMMIT): Максим -> Олег
INSERT INTO orders (client_name, event_date, animator_id, program_id, status, price)
SELECT 'Ірина Бондар', DATE '2026-12-05', a.id, p.id, 'CONFIRMED', 3500
FROM animators a, programs p WHERE a.name = 'Максим' AND p.name = 'Шоу мильних бульбашок';
INSERT INTO orders (client_name, event_date, animator_id, program_id, status, price)
SELECT 'Сергій Мельник', DATE '2026-12-12', a.id, p.id, 'NEW', 3000
FROM animators a, programs p WHERE a.name = 'Максим' AND p.name = 'Паперове шоу';

-- Сценарій КОНФЛІКТУ (ROLLBACK): Софія -> Іван (в Івана вже є 13.12)
INSERT INTO orders (client_name, event_date, animator_id, program_id, status, price)
SELECT 'Наталія Гончар', DATE '2026-12-06', a.id, p.id, 'CONFIRMED', 2800
FROM animators a, programs p WHERE a.name = 'Софія' AND p.name = 'Roblox Квест';
INSERT INTO orders (client_name, event_date, animator_id, program_id, status, price)
SELECT 'Дмитро Кравчук', DATE '2026-12-13', a.id, p.id, 'CONFIRMED', 2900
FROM animators a, programs p WHERE a.name = 'Софія' AND p.name = 'Стіч-паті';
INSERT INTO orders (client_name, event_date, animator_id, program_id, status, price)
SELECT 'Оксана Лисенко', DATE '2026-12-20', a.id, p.id, 'NEW', 3100
FROM animators a, programs p WHERE a.name = 'Софія' AND p.name = 'Супергеройська вечірка';
INSERT INTO orders (client_name, event_date, animator_id, program_id, status, price)
SELECT 'Віктор Шульга', DATE '2026-12-13', a.id, p.id, 'CONFIRMED', 3300
FROM animators a, programs p WHERE a.name = 'Іван' AND p.name = 'Паперове шоу';