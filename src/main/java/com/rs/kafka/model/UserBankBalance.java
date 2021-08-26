package com.rs.kafka.model;

import lombok.Data;

import java.time.Instant;

@Data
public class UserBankBalance {
    private String userName;
    private String lastUpdateTime;
    private Integer balance=0;
}
