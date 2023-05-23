package ch.unisg.ems.actuator.model;

import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsumerShutdownRequest implements Serializable {


    @Getter
    @Setter
    private String customerId;
    @Getter
    @Setter
    private String message;
}

