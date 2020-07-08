SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE `cargoEntity` (
  `id` integer NOT NULL AUTO_INCREMENT,
  `weight` double NOT NULL,
  `starting_destination` varchar(100) NOT NULL,
  `final_destination` varchar(100) NOT NULL,
  `dimensions_id`integer NOT NULL,
  `cargo_position_id` integer DEFAULT NULL,
  `carrier_compartment_id` integer DEFAULT NULL,
  `delivery_status` varchar(50) NOT NULL,

  PRIMARY KEY (`id`),
  KEY `FK_cargo_dimensions` (`dimensions_id`),
  KEY `FK_cargo_cargo_position` (`cargo_position_id`),
  KEY `FK_cargo_carrier_compartment` (`carrier_compartment_id`),

  CONSTRAINT `FK_cargo_dimensions` FOREIGN KEY (`dimensions_id`) REFERENCES `dimensions` (`id`),
  CONSTRAINT `FK_cargo_cargo_position` FOREIGN KEY (`cargo_position_id`) REFERENCES `cargo_position` (`id`),
  CONSTRAINT `FK_cargo_carrier_compartment` FOREIGN KEY (`carrier_compartment_id`) REFERENCES `carrier_compartment` (`id`)

) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `cargo_order` (
  `id` integer NOT NULL AUTO_INCREMENT,
  `tracking_id` varchar(50) NOT NULL UNIQUE,
  `user_id` integer NOT NULL,
  `price` double NOT NULL,
  `estimated_delivery_date` TIMESTAMP NOT NULL,
  `departure_hub_id` integer NOT NULL,
  `arrival_hub_id` integer NOT NULL,
  `cargo_id` integer NOT NULL,
  `delivery_status` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_cargo_order_departure_hub` (`departure_hub_id`),
  KEY `FK_cargo_order_arrival_hub` (`arrival_hub_id`),
  KEY `FK_cargo_order_cargo` (`cargo_id`),
  CONSTRAINT `FK_cargo_order_departure_hub` FOREIGN KEY (`departure_hub_id`) REFERENCES `hub` (`id`),
  CONSTRAINT `FK_cargo_order_arrival_hub` FOREIGN KEY (`arrival_hub_id`) REFERENCES `hub` (`id`),
  CONSTRAINT `FK_cargo_order_cargo` FOREIGN KEY (`cargo_id`) REFERENCES `cargoEntity` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `cargo_position` (
  `id` integer NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `carrier_compartment` (
  `id` integer NOT NULL AUTO_INCREMENT,
  `maximum_weight` double NOT NULL,
  `free_space` double NOT NULL DEFAULT 100.0,
  `volume_id` integer NOT NULL,
  `transporter_id` integer,
  PRIMARY KEY (`id`),
  KEY `FK_carrier_compartment_dimensions` (`volume_id`),
  KEY `FK_carrier_compartment_transporter` (`transporter_id`),
  CONSTRAINT `FK_carrier_compartment_dimensions` FOREIGN KEY (`volume_id`) REFERENCES `dimensions` (`id`),
  CONSTRAINT `FK_carrier_compartment_transporter` FOREIGN KEY (`transporter_id`) REFERENCES `transporter` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `dimensions` (
  `id` integer NOT NULL AUTO_INCREMENT,
  `width` double NOT NULL,
  `height` double NOT NULL,
  `length` integer NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `hub` (
  `id` integer NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `relation` (
  `id` integer NOT NULL AUTO_INCREMENT,
  `distance` double NOT NULL,
  `connected_hub_id` integer NOT NULL,
  `owner_hub_id` integer NOT NULL,
  `relation_type` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_relation_connected_hub` (`connected_hub_id`),
  KEY `FK_relation_owner_hub` (`owner_hub_id`),
  CONSTRAINT `FK_relation_connected_hub` FOREIGN KEY (`connected_hub_id`) REFERENCES `hub` (`id`),
  CONSTRAINT `FK_relation_owner_hub` FOREIGN KEY (`owner_hub_id`) REFERENCES `hub` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `transport_details` (
  `id` integer NOT NULL AUTO_INCREMENT,
  `average_speed` double NOT NULL,
  `price_per_km` double NOT NULL,
  `type` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `transporter` (
  `id` integer NOT NULL AUTO_INCREMENT,
  `current_hub_id` integer NOT NULL,
  `type` varchar(50) NOT NULL,
  `status` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_transporter_current_hub` (`current_hub_id`),
  CONSTRAINT `FK_transporter_current_hub` FOREIGN KEY (`current_hub_id`) REFERENCES `hub` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `transporter_route` (
  `transporter_id` integer NOT NULL,
  `hub_id` integer NOT NULL,
  KEY `FK_transporter_transporter_route` (`transporter_id`),
  KEY `FK_hub_transporter_route` (`hub_id`),
  CONSTRAINT `FK_transporter_transporter_route` FOREIGN KEY (`transporter_id`) REFERENCES `transporter` (`id`),
  CONSTRAINT `FK_hub_transporter_route` FOREIGN KEY (`hub_id`) REFERENCES `hub` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO hub
values
    (default, 'Kharkiv'),
    (default, 'Poltava'),
    (default, 'Myrgorod'),
    (default, 'Kyiv'),
    (default, 'Lviv'),
    (default, 'Odesa'),
    (default, 'Sumy'),
    (default, 'Pavlograd');
