package com.andromeda.dreamshops.controller;

import com.andromeda.dreamshops.dto.AddressDto;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.request.AddAddressRequest;
import com.andromeda.dreamshops.request.UpdateAddressRequest;
import com.andromeda.dreamshops.response.ApiResponse;
import com.andromeda.dreamshops.service.address.IAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/addresses")
public class AddressController {
    private final IAddressService addressService;

    // add an address for a user
    @PostMapping("user/{userId}/address/add")
    public ResponseEntity<ApiResponse> addAddress(@RequestBody AddAddressRequest request, @PathVariable Long userId) {
        try {
            AddressDto addressDto = addressService.addAddress(request, userId);
            return ResponseEntity.ok(new ApiResponse("Address added successfully", addressDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to add address: " + e.getMessage(), null));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse("Failed to add address: " + e.getMessage(), null));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to add address: " + e.getMessage(), null));
        }
    }

    // update an address for a user
    @PutMapping("user/{userId}/address/update/{addressId}")
    public ResponseEntity<ApiResponse> updateAddress(@RequestBody UpdateAddressRequest request, @PathVariable Long userId, @PathVariable Long addressId) {
        try {
            AddressDto addressDto = addressService.updateAddress(addressId, request, userId);
            return ResponseEntity.ok(new ApiResponse("Address updated successfully", addressDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to update address: " + e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse("Failed to update address: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to update address: " + e.getMessage(), null));
        }
    }

        // delete an address for a user
    @DeleteMapping("user/{userId}/address/delete/{addressId}")
    public ResponseEntity<ApiResponse> deleteAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        try {
            addressService.deleteAddressById(addressId, userId);
            return ResponseEntity.ok(new ApiResponse("Address deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to delete address: " + e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse("Failed to delete address: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to delete address: " + e.getMessage(), null));
        }
    }

    // get an address by id
    @GetMapping("/address/{addressId}")
    public ResponseEntity<ApiResponse> getAddressById(@PathVariable Long addressId) {
        try {
            AddressDto addressDto = addressService.getAddressById(addressId);
            return ResponseEntity.ok(new ApiResponse("Address retrieved successfully", addressDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to retrieve address: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to retrieve address: " + e.getMessage(), null));
        }
    }

    // get all addresses
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllAddresses(){
        try {
            List<AddressDto> addresses = addressService.getAllAddress();
            return !addresses.isEmpty() ?
                    ResponseEntity.ok(new ApiResponse("Addresses retrieved successfully", addresses)) :
                    ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No addresses found", null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to retrieve addresses: " + e.getMessage(), null));
        }
    }

    @GetMapping("/user/{userId}/all")
    public ResponseEntity<ApiResponse> getAllAddressesByUserId(@PathVariable Long userId) {
        try {
            List<AddressDto> addresses = addressService.getAllAddressByUserId(userId);
            return !addresses.isEmpty() ?
                    ResponseEntity.ok(new ApiResponse("Addresses retrieved successfully", addresses)) :
                    ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No addresses found for user with id: " + userId, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to retrieve addresses: " + e.getMessage(), null));
        }
    }

    @GetMapping("/user/{userId}/address/default")
    public ResponseEntity<ApiResponse> getDefaultAddressByUserId(@PathVariable Long userId) {
        try {
            AddressDto addressDto = addressService.getDefaultAddressByUserId(userId);
            return ResponseEntity.ok(new ApiResponse("Default address retrieved successfully", addressDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to retrieve default address: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to retrieve default address: " + e.getMessage(), null));
        }
    }

    @PutMapping("/user/{userId}/address/{addressId}/set-default")
    public ResponseEntity<ApiResponse> setDefaultAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        try {
            addressService.setDefaultAddress(userId, addressId);
            return ResponseEntity.ok(new ApiResponse("Default address set successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to set default address: " + e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new ApiResponse("Failed to set default address: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to set default address: " + e.getMessage(), null));
        }
    }

    // Additional endpoints for filtering addresses by country, city, state

    @GetMapping("/by-country")
    public ResponseEntity<ApiResponse> getAddressByCountry(@RequestParam String country) {
        try {
            List<AddressDto> addresses = addressService.getAddressByCountry(country);
            return !addresses.isEmpty() ?
                    ResponseEntity.ok(new ApiResponse("Addresses retrieved successfully", addresses)) :
                    ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No addresses found for country: " + country, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to retrieve addresses: " + e.getMessage(), null));
        }
    }

    @GetMapping("/by-city")
    public ResponseEntity<ApiResponse> getAddressByCity(@RequestParam String city) {
        try {
            List<AddressDto> addresses = addressService.getAddressByCity(city);
            return !addresses.isEmpty() ?
                    ResponseEntity.ok(new ApiResponse("Addresses retrieved successfully", addresses)) :
                    ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No addresses found for city: " + city, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to retrieve addresses: " + e.getMessage(), null));
        }
    }

    @GetMapping("/by-state")
    public ResponseEntity<ApiResponse> getAddressByState(@RequestParam String state) {
        try {
            List<AddressDto> addresses = addressService.getAddressByState(state);
            return !addresses.isEmpty() ?
                    ResponseEntity.ok(new ApiResponse("Addresses retrieved successfully", addresses)) :
                    ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No addresses found for state: " + state, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to retrieve addresses: " + e.getMessage(), null));
        }
    }

    @GetMapping("/user/{userId}/address/{addressId}/exists")
    public ResponseEntity<ApiResponse> existsByIdAndUserAccountId(@PathVariable Long userId, @PathVariable Long addressId) {
        try {
            boolean exists = addressService.existsByIdAndUserAccountId(addressId, userId);
            return ResponseEntity.ok(new ApiResponse("Address existence check completed successfully", exists));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to check address existence: " + e.getMessage(), null));
        }
    }
}
