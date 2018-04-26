
CREATE TABLE `dispute` (
  `transaction_id` int(11) unsigned NOT NULL,
  `amount_refunded` decimal(60,30) DEFAULT NULL,
  `time_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `time_resolved` timestamp NULL DEFAULT NULL
);


CREATE TABLE `message` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `transaction_id` int(10) unsigned NOT NULL,
  `user_id` int(11) unsigned NOT NULL,
  `message` varchar(500) NOT NULL DEFAULT '',
  `time_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);


CREATE TABLE `transaction` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `cryptocurrency` varchar(20) NOT NULL DEFAULT '',
  `amount` decimal(65,30) unsigned NOT NULL,
  `status` varchar(40) NOT NULL DEFAULT '',
  `terms_of_agreement` text NOT NULL,
  `time_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);


CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(256) NOT NULL DEFAULT '',
  `password` varchar(256) NOT NULL DEFAULT '',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `time_registered` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);


CREATE TABLE `user_role` (
  `user_id` int(12) unsigned NOT NULL,
  `authority` varchar(256) NOT NULL DEFAULT ''
);
