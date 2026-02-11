package com.andromeda.dreamshops.service.address;

import com.andromeda.dreamshops.dto.AddressDto;
import com.andromeda.dreamshops.model.Address;
import com.andromeda.dreamshops.request.AddAddressRequest;
import com.andromeda.dreamshops.request.UpdateAddressRequest;

import java.util.List;
import java.util.Optional;


public interface IAddressService {
    AddressDto addAddress(AddAddressRequest request, Long userId);
    AddressDto updateAddress(Long addressId, UpdateAddressRequest request, Long userId);
    void deleteAddressById(Long addressId, Long userId);
    AddressDto getAddressById(Long addressId);

    List<AddressDto> getAllAddress();
    List<AddressDto> getAddressByCountry(String country);
    List<AddressDto> getAddressByCity(String city);
    List<AddressDto> getAddressByState(String state);
    List<AddressDto> getAllAddressByUserId(Long  userId);
    AddressDto getDefaultAddressByUserId(Long userId);
    void setDefaultAddress(Long userId, Long addressId);

    boolean existsByIdAndUserAccountId(Long addressId, Long userId);

    AddressDto convertToDto(Address address);
    List<AddressDto> convertToDtoList(List<Address> addresses);
}
