package com.cargohub.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Order {
    @Id
    Integer id;
    
    @Column
    Integer userId;

    @Column
    Double totalPrice;

    @Column
    Date estimatedDeliveryDate;

    //ToDo first - departure, last - arrival
    @OneToMany
    List<OrderItem> orderItems;
}
