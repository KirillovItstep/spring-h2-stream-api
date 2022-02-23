create table customer (
	id bigint not null, 
	name varchar(255), 
	tier integer, 
	primary key (id)
);

create table order_product_relationship (
	order_id bigint not null, 
	product_id bigint not null, 
	primary key (order_id, product_id)
);

create table product (
	id bigint not null, 
	category varchar(255), 
	name varchar(255), 
	price double, 
	primary key (id)
);

create table product_order (
	id bigint not null, 
	order_date date, 
	delivery_date date,
	status varchar(20),
	customer_id bigint, 
	primary key (id)
);

alter table order_product_relationship add constraint constraint1 
foreign key (product_id) references product;

alter table order_product_relationship add constraint constraint2 
foreign key (order_id) references product_order;

alter table product_order add constraint constraint3 
foreign key (customer_id) references customer;
