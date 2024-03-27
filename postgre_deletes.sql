delete from "customer".customers;

delete from "order".orders;
delete from "order".order_address;
delete from "order".order_items;
delete from "order".payment_outbox;
delete from "order".restaurant_approval_outbox;

delete from "restaurant".order_approval;
delete from "restaurant".order_outbox;
delete from "restaurant".restaurant_products;
delete from "restaurant".restaurants;
delete from "restaurant".products;

delete from "payment".payments;
delete from "payment".credit_entry;
delete from "payment".credit_history;
delete from "payment".order_outbox;


