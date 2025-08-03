package com.ingemark.productmanager.model.user;

public record JwtResponse (
         String token,
         String username,
         String email
){
}
