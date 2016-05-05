package com.zeyad.cleanarchitecture.presentation.mapper;

import android.test.AndroidTestCase;

import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;
import com.zeyad.cleanarchitecture.presentation.view_models.mapper.UserViewModelDataMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class UserViewModelDataMapperTest extends AndroidTestCase {

    private static final int FAKE_USER_ID = 123;
    private static final String FAKE_FULLNAME = "Tony Stark";

    private UserViewModelDataMapper userViewModelDataMapper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userViewModelDataMapper = new UserViewModelDataMapper();
    }

    public void testTransformUser() {
        User user = createFakeUser();
        UserViewModel userViewModel = userViewModelDataMapper.transform(user);

        assertThat(userViewModel, is(instanceOf(UserViewModel.class)));
        assertThat(userViewModel.getUserId(), is(FAKE_USER_ID));
        assertThat(userViewModel.getFullName(), is(FAKE_FULLNAME));
    }

    public void testTransformUserCollection() {
        User mockUserOne = mock(User.class);
        User mockUserTwo = mock(User.class);

        List<User> userList = new ArrayList<>(5);
        userList.add(mockUserOne);
        userList.add(mockUserTwo);

        Collection<UserViewModel> userViewModelList = userViewModelDataMapper.transform(userList);

        assertThat(userViewModelList.toArray()[0], is(instanceOf(UserViewModel.class)));
        assertThat(userViewModelList.toArray()[1], is(instanceOf(UserViewModel.class)));
        assertThat(userViewModelList.size(), is(2));
    }

    private User createFakeUser() {
        User user = new User(FAKE_USER_ID);
        user.setFullName(FAKE_FULLNAME);
        return user;
    }
}