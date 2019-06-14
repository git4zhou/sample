package com.spectre.service;

import com.spectre.entity.User;

import java.util.List;

public interface UserService {

    User getById(long id);

    List<User> getByIds(List<Long> ids);
    
    void deleteById(long id);

    String getUserName(String username);

}
