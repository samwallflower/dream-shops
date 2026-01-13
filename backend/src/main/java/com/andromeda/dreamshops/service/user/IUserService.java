package com.andromeda.dreamshops.service.user;

import com.andromeda.dreamshops.dto.UserDto;
import com.andromeda.dreamshops.model.User;
import com.andromeda.dreamshops.request.CreateUserRequest;
import com.andromeda.dreamshops.request.UpdateUserRequest;

public interface IUserService {
    User getUserById(Long userId);
    User createUser(CreateUserRequest request);
    User updateUser(UpdateUserRequest request, Long userId);
    void deleteUser(Long userId);

    UserDto convertToDto(User user);

    User getAuthenticatedUser();
}
