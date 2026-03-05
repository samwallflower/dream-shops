package com.andromeda.dreamshops.service.user;

import com.andromeda.dreamshops.dto.UserAccountDto;
import com.andromeda.dreamshops.enums.Gender;
import com.andromeda.dreamshops.enums.Theme;
import com.andromeda.dreamshops.exceptions.AlreadyExistsException;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.exceptions.ResourceProcessingException;
import com.andromeda.dreamshops.model.User;
import com.andromeda.dreamshops.model.UserAccount;
import com.andromeda.dreamshops.repository.UserAccountRepository;
import com.andromeda.dreamshops.request.UpdateUserAccountRequest;
import com.andromeda.dreamshops.service.cloudprovider.ICloudProviderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAccountService implements IUserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final ModelMapper modelMapper;
    private final ICloudProviderService cloudProviderService;

    @Override
    @Transactional
    public UserAccount createUserAccount(User user, String firstName, String lastName) {
        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(generateUniqueUsername(firstName,lastName));
        userAccount.setUser(user);
        return userAccountRepository.save(userAccount);
    }

    @Override
    @Transactional
    public UserAccountDto updateUserAccount(Long userId, UpdateUserAccountRequest request) {

        UserAccount userAccount = userAccountRepository.findUserAccountByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User account not found for user id: " + userId));
        if(request.getUsername() != null && !request.getUsername().equals(userAccount.getUsername())) {
            validateUsername(request.getUsername());
            userAccount.setUsername(request.getUsername());
        }
        Optional.ofNullable(request.getPhoneNumber()).ifPresent(userAccount::setPhoneNumber);
        Optional.ofNullable(request.getDateOfBirth()).ifPresent(userAccount::setDateOfBirth);
        Optional.ofNullable(request.getGender()).ifPresent(gender -> userAccount.setGender(Gender.valueOf(gender)));
        Optional.ofNullable(request.getDashboardColor()).ifPresent(userAccount::setDashboardColor);
        Optional.ofNullable(request.getPreferredTheme()).ifPresent(theme -> userAccount.setPreferredTheme(Theme.valueOf(theme)));
        Optional.ofNullable(request.getPreferredLanguage()).ifPresent(userAccount::setPreferredLanguage);

        return convertToDto(userAccountRepository.save(userAccount));
    }

    private void validateUsername(String username) {
        if (username.trim().isEmpty()) {
            throw new ResourceNotFoundException("Username cannot be empty");
        }
        if (userAccountRepository.existsByUsername(username)) {
            throw new AlreadyExistsException("Username already exists: " + username);
        }

        if(!username.matches("^[a-zA-Z0-9._]{3,20}$")) {
            throw new ResourceProcessingException("Username must be at least 3 characters long and can only contain letters, numbers, dots and underscores");
        }
    }

    @Override
    public UserAccountDto getUserAccountById(Long id) {
        return userAccountRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User account not found with id: " + id));
    }

    @Override
    public UserAccountDto getUserAccountByUserId(Long userId) {
        return userAccountRepository.findUserAccountByUserId(userId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User account not found for user id: " + userId));
    }

    @Override
    @Transactional
    public String updateProfilePicture(Long userId, MultipartFile profilePicture) {
        UserAccount userAccount = userAccountRepository.findUserAccountByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User account not found for user id: " + userId));
        String folderPath = "dreamshops/profiles";
        String publicId = "user_" + userId;
        String transformation = "c_fill,g_face,h_200,w_200"; // Crop to 200x200 focusing on the face
        try {
            Map uploadResult = cloudProviderService.updateImageByPublicId(
                    publicId,
                    profilePicture,
                    folderPath,
                    transformation
            );
            String imageUrl = (String) uploadResult.get("secure_url");
            userAccount.setProfilePictureUrl(imageUrl);
            return userAccountRepository.save(userAccount).getProfilePictureUrl();
        } catch (IOException e) {
            throw new ResourceProcessingException("Error while uploading profile picture: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removeProfilePicture(Long userId) {
        UserAccount userAccount = userAccountRepository.findUserAccountByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User account not found for user id: " + userId));
        if(userAccount.getProfilePictureUrl() == null || userAccount.getProfilePictureUrl().isEmpty()) {
            return;
        }

        try {
            cloudProviderService.deleteImageByImageURl(userAccount.getProfilePictureUrl());
            userAccount.setProfilePictureUrl(null);
            userAccountRepository.save(userAccount);
        } catch (IOException e) {
            throw new ResourceProcessingException("Error while removing profile picture: " + e.getMessage());
        }

    }

    @Override
    public UserAccountDto convertToDto(UserAccount userAccount) {
        return modelMapper.map(userAccount, UserAccountDto.class);
    }

    @Override
    public List<UserAccountDto> getAllUserAccounts() {
        return userAccountRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public UserAccountDto getUserAccountByUsername(String username) {
        return userAccountRepository.findByUsername(username)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User account not found with username: " + username));
    }

    private String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = (firstName + lastName)
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "");

        String uniqueUsername = baseUsername;
        int counter = 1;

        while (usernameExists(uniqueUsername)) {
            uniqueUsername = baseUsername + counter;
            counter++;
        }

        return uniqueUsername;
    }

    private boolean usernameExists(String uniqueUsername) {
        return userAccountRepository.existsByUsername(uniqueUsername);
    }
}
