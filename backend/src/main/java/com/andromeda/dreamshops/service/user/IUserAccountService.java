package com.andromeda.dreamshops.service.user;

import com.andromeda.dreamshops.dto.UserAccountDto;
import com.andromeda.dreamshops.model.User;
import com.andromeda.dreamshops.model.UserAccount;
import com.andromeda.dreamshops.request.UpdateUserAccountRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUserAccountService {
    UserAccount createUserAccount(User user, String firstName, String lastName);
    UserAccountDto updateUserAccount(Long userId, UpdateUserAccountRequest request);
    UserAccountDto getUserAccountById(Long id);
    UserAccountDto getUserAccountByUserId(Long userId);
    String updateProfilePicture(Long userId, MultipartFile profilePicture);
    void removeProfilePicture(Long userId);
    UserAccountDto convertToDto(UserAccount userAccount);
    List<UserAccountDto> getAllUserAccounts();
    UserAccountDto getUserAccountByUsername(String username);
}
