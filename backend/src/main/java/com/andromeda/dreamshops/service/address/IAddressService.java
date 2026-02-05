package com.andromeda.dreamshops.service.address;

import com.andromeda.dreamshops.model.Address;
import com.andromeda.dreamshops.request.AddAddressRequest;
import com.andromeda.dreamshops.request.UpdateAddressRequest;

import java.util.List;

public interface IAddressService {
    Address addAddress(AddAddressRequest request);
    Address updateAddress(Long addressId, UpdateAddressRequest request);
    void deleteAddressById(Long addressId);
    Address getAddressById(Long addressId);
    List<Address> getAllAddress();
    List<Address> getAddressByCountry(String country);
    List<Address> getAddressByCity(String city);
    List<Address> getAddressByState(String state);
    List<Address> getAllAddressByUserId(Long  userId);
}
