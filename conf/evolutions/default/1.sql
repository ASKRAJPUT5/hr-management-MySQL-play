# --- !Ups

create table "people1"(
  "id" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  "name" varchar(50) not null,
  "phone" int not null,
   "email"  varchar(50) not null,
  "age" int not null
);

# --- !Downs

drop table "people1" if exists;
