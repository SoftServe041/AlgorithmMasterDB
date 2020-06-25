CREATE TABLE cargo_hub
(
    hub_id   integer      NOT NULL AUTO_INCREMENT,
    hub_name varchar(100) NOT NULL,
    PRIMARY KEY (hub_id)
);

CREATE TABLE `relation`
(
    `id`               integer     NOT NULL AUTO_INCREMENT,
    `distance`         double      NOT NULL,
    `connected_hub_id` integer     NOT NULL,
    `owner_hub_id`     integer     NOT NULL,
    `relation_type`    varchar(50) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_relation_connected_hub` FOREIGN KEY (`connected_hub_id`) REFERENCES cargo_hub (hub_id),
    CONSTRAINT `FK_relation_owner_hub` FOREIGN KEY (`owner_hub_id`) REFERENCES cargo_hub (hub_id)
);

CREATE TABLE `transporter`
(
    `id`             integer     NOT NULL AUTO_INCREMENT,
    `current_hub_id` integer     NOT NULL,
    `type`           varchar(50) NOT NULL,
    `status`         varchar(50) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_transporter_current_hub` FOREIGN KEY (`current_hub_id`) REFERENCES cargo_hub (hub_id)
);

CREATE TABLE `dimensions`
(
    `id`     integer NOT NULL AUTO_INCREMENT,
    `width`  double  NOT NULL,
    `height` double  NOT NULL,
    `length` integer NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `carrier_compartment`
(
    `id`             integer NOT NULL AUTO_INCREMENT,
    `maximum_weight` double  NOT NULL,
    `free_space`     double  NOT NULL DEFAULT 100.0,
    `volume_id`      integer NOT NULL,
    `transporter_id` integer,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_carrier_compartment_dimensions` FOREIGN KEY (`volume_id`) REFERENCES `dimensions` (`id`),
    CONSTRAINT `FK_carrier_compartment_transporter` FOREIGN KEY (`transporter_id`) REFERENCES `transporter` (`id`)
);

CREATE TABLE `cargo_position`
(
    `id` integer NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`id`)
);

CREATE TABLE `cargo`
(
    `id`                     integer      NOT NULL AUTO_INCREMENT,
    `weight`                 double       NOT NULL,
    `starting_destination`   varchar(100) NOT NULL,
    `final_destination`      varchar(100) NOT NULL,
    `dimensions_id`          integer      NOT NULL,
    `cargo_position_id`      integer DEFAULT NULL,
    `carrier_compartment_id` integer DEFAULT NULL,
    `delivery_status`        varchar(50)  NOT NULL,
    `order_id`               integer DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_cargo_dimensions` FOREIGN KEY (`dimensions_id`) REFERENCES `dimensions` (`id`),
    CONSTRAINT `FK_cargo_cargo_position` FOREIGN KEY (`cargo_position_id`) REFERENCES `cargo_position` (`id`),
    CONSTRAINT `FK_cargo_carrier_compartment` FOREIGN KEY (`carrier_compartment_id`) REFERENCES `carrier_compartment` (`id`)
);

CREATE TABLE `cargo_order`
(
    `id`                      integer     NOT NULL AUTO_INCREMENT,
    `tracking_id`             varchar(50) NOT NULL UNIQUE,
    `user_id`                 integer     NOT NULL,
    `price`                   double      NOT NULL,
    `estimated_delivery_date` TIMESTAMP   NOT NULL,
    `departure_hub_id`        integer     NOT NULL,
    `arrival_hub_id`          integer     NOT NULL,
    `cargo_id`                integer     NOT NULL,
    `delivery_status`         varchar(50) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_cargo_order_departure_hub` FOREIGN KEY (`departure_hub_id`) REFERENCES cargo_hub (hub_id),
    CONSTRAINT `FK_cargo_order_arrival_hub` FOREIGN KEY (`arrival_hub_id`) REFERENCES cargo_hub (hub_id),
    CONSTRAINT `FK_cargo_order_cargo` FOREIGN KEY (`cargo_id`) REFERENCES `cargo` (`id`)
);

ALTER TABLE `cargo`
    add CONSTRAINT `FK_cargo_cargo_order` FOREIGN KEY (`order_id`) REFERENCES `cargo_order` (`id`);

CREATE TABLE `transport_details`
(
    `id`            integer     NOT NULL AUTO_INCREMENT,
    `average_speed` double      NOT NULL,
    `price_per_km`  double      NOT NULL,
    `type`          varchar(50) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `transporter_route`
(
    `transporter_id` integer NOT NULL,
    `hub_id`         integer NOT NULL,
    CONSTRAINT `FK_transporter_transporter_route` FOREIGN KEY (`transporter_id`) REFERENCES `transporter` (`id`),
    CONSTRAINT `FK_hub_transporter_route` FOREIGN KEY (`hub_id`) REFERENCES cargo_hub (hub_id)
);
INSERT INTO cargo_hub
values (DEFAULT, 'Kharkiv');
INSERT INTO cargo_hub
values (DEFAULT, 'Poltava');
INSERT INTO cargo_hub
values (DEFAULT, 'Myrgorod');
INSERT INTO cargo_hub
values (DEFAULT, 'Kyiv');
INSERT INTO cargo_hub
values (DEFAULT, 'Lviv');
INSERT INTO cargo_hub
values (DEFAULT, 'Odesa');
INSERT INTO cargo_hub
values (DEFAULT, 'Sumy');
INSERT INTO cargo_hub
values (DEFAULT, 'Pavlograd');
