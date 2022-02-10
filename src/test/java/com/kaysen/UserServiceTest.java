package com.kaysen;

import com.kaysen.domain.User;
import com.kaysen.domain.enums.RoleEnum;
import com.kaysen.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;
//    @Test
    public void testUserInsert(){
        User user = new User();
        user.setPhone("15612341234");
        user.setPassword("123456");
        user.setUsername("zhangsan");
        user.setRole(RoleEnum.USER);
        userService.save(user);
    }

//    @Test
    public void testUserGetyId(){
        System.out.println(userService.getById(1));
    }
}
