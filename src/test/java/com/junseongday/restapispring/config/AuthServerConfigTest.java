package com.junseongday.restapispring.config;

import com.junseongday.restapispring.accounts.Account;
import com.junseongday.restapispring.accounts.AccountRole;
import com.junseongday.restapispring.accounts.AccountService;
import com.junseongday.restapispring.common.BaseControllerTest;
import com.junseongday.restapispring.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception{
        String username = "junseong@gmail.com";
        String password = "junseong";
//        Account junseong = Account.builder()
//                .email(username)
//                .password(password)
//                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
//                .build();
//        accountService.saveAccount(junseong);

        String clentId = "myApp";
        String clientSecret = "pass";
        mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clentId, clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password")
        )
                .andDo(print())
                .andExpect(status().isOk())
        .andExpect(jsonPath("access_token").exists())
        ;

    }
}