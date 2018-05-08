package kr.co.redbrush.webapp.service;

import kr.co.redbrush.webapp.domain.*;
import kr.co.redbrush.webapp.repository.AccountRepository;
import kr.co.redbrush.webapp.repository.RandomDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
public class AccountServiceTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks
    public AccountService accountService = new AccountService();

    @Mock
    private AccountRepository accountRepository;

    private Account account;
    private Long id = 1L;
    private String userId = "userId";
    private String name = "name";
    private String password = "password";
    private String email = "test@test.com";

    private AccountRole accountRole;
    private String roleName = "ROLE_ADMIN";

    @Before
    public void before() {
        account = new Account();
        account.setAcid(id);
        account.setUserId(userId);
        account.setName(name);
        account.setPassword(password);
        account.setEmail(email);

        accountRole = new AccountRole();
        accountRole.setArid(id);
        accountRole.setRoleName(roleName);

        List<AccountRole> roles = new ArrayList<>();
        roles.add(accountRole);

        account.setRoles(roles);
    }

    @Test
    public void testLoadUserByUsername() {
        when(accountRepository.findAccountByUserId(userId)).thenReturn(account);

        UserDetails userDetails = accountService.loadUserByUsername(userId);

        LOGGER.debug("userDetails : {}", userDetails);

        assertThat("Unexpected value.", userDetails, instanceOf(SecureAccount.class));
        assertThat("Unexpected value.", userDetails.getUsername(), is(userId));
        assertThat("Unexpected value.", userDetails.getPassword(), is(password));
        assertThat("Unexpected value.", userDetails.getAuthorities(), notNullValue());
        assertThat("Unexpected value.", userDetails.getAuthorities().size(), is(account.getRoles().size()));
        assertThat("Unexpected value.", userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")), is(true));
    }
}