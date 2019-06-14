package com.spectre.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String password;
}
