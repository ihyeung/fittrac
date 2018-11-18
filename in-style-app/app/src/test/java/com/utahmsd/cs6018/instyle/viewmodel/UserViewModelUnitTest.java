package com.utahmsd.cs6018.instyle.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.utahmsd.cs6018.instyle.db.InStyleDatabase;
import com.utahmsd.cs6018.instyle.db.dao.UserDao;
import com.utahmsd.cs6018.instyle.db.entity.User;
import com.utahmsd.cs6018.instyle.db.repo.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Date;

import javax.inject.Inject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserViewModelUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private InStyleDatabase inStyleDatabase;

    @Mock
    private MediatorLiveData<User> m_observableUser = new MediatorLiveData<>();



    @Mock
    private UserDao mUserDao;

    @Inject
    private UserViewModel userViewModel;

    private User user = createNewUser();

    @Before
    public void setUp() {

        User userResult = createNewUser();
        m_observableUser.setValue(userResult);

        given(userRepository.find(anyString())).willReturn(m_observableUser);
    }

    @Test
    public void authenticateUser_success() {
        boolean authenticateResult = userViewModel.authenticateUser(user);

        verify(userRepository).find(anyString());
        assertTrue(authenticateResult);

    }

    @Test
    public void authenticateUser_failure() {
        user.setPassword("DIFFERENTPASSWORD");
        boolean authenticateResult = userViewModel.authenticateUser(user);

        verify(userRepository).find(anyString());
        assertFalse(authenticateResult);

    }

    @Test
    public void createUser() {
    }

    @Test
    public void updateUser() {
    }

    @Test
    public void findUser() {
        LiveData<User> findResult = userViewModel.findUser(user.getEmail());

        verify(userRepository).find(anyString());

        assertNotNull(findResult);
        assertNotNull(findResult.getValue());
    }

    @Test
    public void deleteUser() {
    }

    private User createNewUser() {
        User user = new User();
        user.setFirstName("HELLO");
        user.setLastName("KITTY");
        user.setEmail("hello@kitty.com");
        user.setPassword("password");
        user.setJoinDate(Date.from(Instant.now()));

        return user;
    }
}