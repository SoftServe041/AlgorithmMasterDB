ALTER TABLE `cargo_position`
    Add column `width_pos` integer NOT NULL ,
    Add column `height_pos` integer NOT NULL ,
    Add column `length_pos` integer NOT NULL ;

ALTER TABLE `transport_details`
    Add column `cell_size` double NOT NULL ;

ALTER TABLE `cargo_order`
    Add column `created` TIMESTAMP DEFAULT CURRENT_TIMESTAMP();