package com.example.teamcity.api.models;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String username;
    private String password;
}
