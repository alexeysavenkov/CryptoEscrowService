insert into user (id, email, password, enabled) values (1, 'user@gmail.com', '$2a$10$.pHIP7r7InNNpKer0DBene1t2HNRXUrMwIn7hBSMZVEyYkm1zDV/S', true);
insert into user (id, email, password, enabled) values (2, 'admin@admin.com', '$2a$10$.pHIP7r7InNNpKer0DBene1t2HNRXUrMwIn7hBSMZVEyYkm1zDV/S', true);


insert into user_role (user_id, authority) values (2, 'ROLE_ADMIN');