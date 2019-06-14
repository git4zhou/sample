package com.spectre.service.impl;


import com.spectre.entity.User;
import com.spectre.service.UserService;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class UserTest {

    @Autowired
    private UserService userService;

    @Test
    public void codecTest(){

        Base64 base64 = new Base64();

        String val1 = "111111111111111111111";

        String val2 = "dasssssssssssssssssssssssssssssssssssssssacxczxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxsadfsafdasfdfxcasdddddddddafssssssssssssssssssssssssssss";

        String[] array = new String[]{"weq","3213","321312"};

        String enCode1 = base64.encodeAsString(val1.getBytes());
        String enCode2 = base64.encodeAsString(val2.getBytes());

        System.out.println(val1.hashCode());
        System.out.println(val2.hashCode());
        System.out.println(Arrays.deepHashCode(array));

    }

    @Test
    public void cacheTest() {
        User user = userService.getById(9529);
        System.out.println(user.getId());
        //String username = userService.getUserName("qwer");
        //System.out.println(user.getUsername());
        //userService.deleteById(9527);
    }

}
