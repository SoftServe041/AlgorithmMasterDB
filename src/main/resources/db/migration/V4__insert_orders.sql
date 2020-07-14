INSERT INTO route
values (default),
       (default);
INSERT INTO route_hub
values (1, 1),
       (1, 2),
       (1, 3);
INSERT INTO route_hub
values (2, 1),
       (2, 2);
INSERT INTO cargo_order
VALUES (default, '123123123', '1', '200', '2020-11-13 13:30:55', '1', '3', 'PROCESSING', '2020-10-07 13:30:55', 1);
INSERT INTO dimensions
VALUES (default, '1.2', '1.2', '2.4');
INSERT INTO cargo
VALUES (default, '100', 'Kharkiv', 'Myrhorod', '1', default, default, '1', 'PROCESSING');

INSERT INTO cargo_order
VALUES (default, '123123234234', '1', '400', '2020-9-13 13:30:55', '1', '3', 'PROCESSING', '2020--08-07 13:30:55', 1);
INSERT INTO dimensions
VALUES (default, '1.2', '1.2', '1.2'),
       (default, '1.2', '1.2', '1.2');
INSERT INTO cargo
VALUES (default, '100', 'Kharkiv', 'Myrhorod', '2', default, default, '2', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Myrhorod', '3', default, default, '2', 'PROCESSING');

INSERT INTO cargo_order
VALUES (default, '1231555666', '1', '1000', '2020-07-13 13:30:55', '1', '3', 'PROCESSING', '2020-06-07 13:30:55', 1);
INSERT INTO dimensions
VALUES (default, '1.2', '1.2', '1.2'),
       (default, '1.2', '1.2', '1.2'),
       (default, '1.2', '1.2', '1.2'),
       (default, '1.2', '1.2', '1.2'),
       (default, '1.2', '1.2', '1.2'),
       (default, '1.2', '1.2', '1.2'),
       (default, '1.2', '1.2', '1.2'),
       (default, '1.2', '1.2', '1.2'),
       (default, '1.2', '1.2', '1.2'),
       (default, '1.2', '1.2', '1.2'),
       (default, '1.2', '1.2', '1.2'),
       (default, '1.2', '1.2', '1.2');
INSERT INTO cargo
VALUES (default, '100', 'Kharkiv', 'Myrhorod', '4', default, default, '3', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Myrhorod', '5', default, default, '3', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Myrhorod', '6', default, default, '3', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Myrhorod', '7', default, default, '3', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Myrhorod', '8', default, default, '3', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Myrhorod', '9', default, default, '3', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Myrhorod', '10', default, default, '3', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Myrhorod', '11', default, default, '3', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Myrhorod', '12', default, default, '3', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Myrhorod', '13', default, default, '3', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Myrhorod', '14', default, default, '3', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Myrhorod', '15', default, default, '3', 'PROCESSING');

INSERT INTO cargo_order
VALUES (default, '222222222', '1', '200', '2020-11-13 03:30:55', '1', '2', 'PROCESSING', '2020-10-07 03:30:55', 2);
INSERT INTO dimensions
VALUES (default, '1.2', '2.4', '2.4'),
       (default, '1.2', '2.4', '2.4'),
       (default, '1.2', '2.4', '2.4'),
       (default, '1.2', '2.4', '2.4'),
       (default, '1.2', '2.4', '2.4'),
       (default, '1.2', '2.4', '2.4'),
       (default, '1.2', '2.4', '2.4');
INSERT INTO cargo
VALUES (default, '100', 'Kharkiv', 'Poltava', '16', default, default, '4', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Poltava', '17', default, default, '4', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Poltava', '18', default, default, '4', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Poltava', '19', default, default, '4', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Poltava', '20', default, default, '4', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Poltava', '21', default, default, '4', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Poltava', '22', default, default, '4', 'PROCESSING');

INSERT INTO dimensions
VALUES (default, '2.4', '2.4', '12');
INSERT INTO transporter
VALUES (default, '1', 'TRUCK', 'WAITING');
INSERT INTO carrier_compartment
VALUES (default, '22', '100', '23', '1');

INSERT INTO transport_details
VALUES (default, 100, 100, 'TRUCK', 0.3);

INSERT INTO dimensions
VALUES (default, '1.2', '2.4', '2.4'),
       (default, '0.6', '1.2', '1.2'),
       (default, '1.2', '1.2', '2.4'),
       (default, '2.4', '2.4', '2.4');
INSERT INTO cargo
VALUES (default, '100', 'Kharkiv', 'Myrhorod', '24', default, default, '3', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Myrhorod', '25', default, default, '3', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Myrhorod', '26', default, default, '3', 'PROCESSING'),
       (default, '100', 'Kharkiv', 'Myrhorod', '27', default, default, '3', 'PROCESSING');