package com.andromeda.dreamshops.controller;

import com.andromeda.dreamshops.dto.UserAccountDto;
import com.andromeda.dreamshops.exceptions.AlreadyExistsException;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.exceptions.ResourceProcessingException;
import com.andromeda.dreamshops.request.UpdateUserAccountRequest;
import com.andromeda.dreamshops.response.ApiResponse;
import com.andromeda.dreamshops.service.user.IUserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/user-accounts")
public class UserAccountController {
    private final IUserAccountService userAccountService;

    //get all user accounts
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllUserAccounts() {
        List<UserAccountDto> userAccounts = userAccountService.getAllUserAccounts();
        return !userAccounts.isEmpty()?
                ResponseEntity.ok(new ApiResponse("User accounts retrieved successfully", userAccounts)):
                ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No user accounts found", null));

    }

    // get user account by user id
    @GetMapping("/account/user/{userId}")
    public ResponseEntity<ApiResponse> getUserAccountByUserId(@PathVariable Long userId) {
        try {
            UserAccountDto userAccount = userAccountService.getUserAccountByUserId(userId);
            return ResponseEntity.ok(new ApiResponse("User account retrieved successfully", userAccount));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }

    }
    // get user account by id
    @GetMapping("/account/{id}")
    public ResponseEntity<ApiResponse> getUserAccountById(@PathVariable Long id) {
        try {
            UserAccountDto userAccount = userAccountService.getUserAccountById(id);
            return ResponseEntity.ok(new ApiResponse("User account retrieved successfully", userAccount));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }
    // get user account by username
    @GetMapping("/account/by-username")
    public ResponseEntity<ApiResponse> getUserAccountByUsername(@RequestParam String username) {
        try {
            UserAccountDto userAccount = userAccountService.getUserAccountByUsername(username);
            return ResponseEntity.ok(new ApiResponse("User account retrieved successfully", userAccount));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    // update user account
    @PutMapping("/account/{userId}")
    public ResponseEntity<ApiResponse> updateUserAccount(@PathVariable Long userId, @RequestBody UpdateUserAccountRequest request) {
        try {
            UserAccountDto updatedAccount = userAccountService.updateUserAccount(userId, request);
            return ResponseEntity.ok(new ApiResponse("User account updated successfully", updatedAccount));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (ResourceProcessingException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    // update profile picture
    @PostMapping("/account/{userId}/profile-picture")
    public ResponseEntity<ApiResponse> updateProfilePicture(@PathVariable Long userId, @RequestParam("profilePicture") MultipartFile profilePicture) {
        try {
            String imageUrl = userAccountService.updateProfilePicture(userId, profilePicture);
            return ResponseEntity.ok(new ApiResponse("Profile picture updated successfully", imageUrl));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (ResourceProcessingException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    // remove profile picture
    @DeleteMapping("/account/{userId}/profile-picture")
    public ResponseEntity<ApiResponse> removeProfilePicture(@PathVariable Long userId) {
        try {
            userAccountService.removeProfilePicture(userId);
            return ResponseEntity.ok(new ApiResponse("Profile picture removed successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (ResourceProcessingException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

}
