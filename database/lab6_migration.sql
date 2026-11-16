-- ЛР №6 (Spring Data JPA): міграція схеми, створеної в ЛР №5.
-- Виконати ОДИН раз у pgAdmin (Query Tool) для бази krutishe_crm.

-- 1. Ціна замовлення
ALTER TABLE public.orders
    ADD COLUMN IF NOT EXISTS price numeric(10, 2) NOT NULL DEFAULT 0;

UPDATE public.orders SET price = 2500 WHERE price = 0 AND status = 'NEW';
UPDATE public.orders SET price = 3200 WHERE price = 0 AND status = 'CONFIRMED';
UPDATE public.orders SET price = 4100 WHERE price = 0 AND status = 'COMPLETED';

-- 2. Захист на рівні БД: один аніматор не може мати два активні замовлення на одну дату
CREATE UNIQUE INDEX IF NOT EXISTS ux_orders_animator_date_active
    ON public.orders (animator_id, event_date)
    WHERE status IN ('NEW', 'CONFIRMED');

-- 3. Прискорення пошуку замовлень за аніматором
CREATE INDEX IF NOT EXISTS ix_orders_animator_id ON public.orders (animator_id);