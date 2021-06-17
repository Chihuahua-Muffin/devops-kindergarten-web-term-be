package com.devopskindergarten.xtermssh.domain;

import lombok.Data;

@Data
public class SshDto {

    private String	type;
    private String 	host;
    private int	port;
    private String	username;
    private String	password;

}