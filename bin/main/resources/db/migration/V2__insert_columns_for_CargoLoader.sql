ALTER TABLE `cargo_position`
    Add column `width_pos`  integer NOT NULL,
    Add column `height_pos` integer NOT NULL,
    Add column `length_pos` integer NOT NULL;

ALTER TABLE `transport_details`
    Add column `cell_size` double NOT NULL;

ALTER TABLE `cargo_order`
    Add column `created` TIMESTAMP DEFAULT CURRENT_TIMESTAMP();

CREATE TABLE `route`
(
    `id` integer NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = latin1;

CREATE TABLE `route_hub`
(
    `route_id` integer NOT NULL,
    `hub_id`   integer NOT NULL,
    KEY `FK_route_route_hub` (`route_id`),
    KEY `FK_hub_route_hub` (`hub_id`),
    CONSTRAINT `FK_route_route_hub` FOREIGN KEY (`route_id`) REFERENCES `route` (`id`),
    CONSTRAINT `FK_hub_route_hub` FOREIGN KEY (`hub_id`) REFERENCES `hub` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;