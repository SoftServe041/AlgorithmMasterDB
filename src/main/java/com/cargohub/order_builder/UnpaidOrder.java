package com.cargohub.order_builder;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UnpaidOrder {

    private static int counter = 0;

    @NonNull
    private String trackingId = generateNextTrackingId();

    @NonNull
    private Integer price;

    @NonNull
    private LocalDate estimatedDeliveryDate;

    private List<String> hubs;

    private static String generateNextTrackingId() {
        int randomNumber = (int) (Math.random() * 10000);
        return "ch" + randomNumber + (++counter);
    }
}
