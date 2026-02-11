package com.andromeda.dreamshops.service.address;

import com.andromeda.dreamshops.dto.AddressDto;
import com.andromeda.dreamshops.enums.AddressType;
import com.andromeda.dreamshops.exceptions.GeneralException;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.exceptions.ResourceProcessingException;
import com.andromeda.dreamshops.model.Address;
import com.andromeda.dreamshops.model.UserAccount;
import com.andromeda.dreamshops.repository.AddressRepository;
import com.andromeda.dreamshops.repository.UserAccountRepository;
import com.andromeda.dreamshops.request.AddAddressRequest;
import com.andromeda.dreamshops.request.UpdateAddressRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService implements IAddressService{
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    private final UserAccountRepository userAccountRepository;

    @Override
    @Transactional
    public AddressDto addAddress(AddAddressRequest request, Long userId) {
         UserAccount userAccount = userAccountRepository.findUserAccountByUserId(userId)
                .orElseThrow(()->new ResourceNotFoundException("User account not found for user id: " + userId));

        AddressType addressType = resolveAddressType(request.getAddressType());

        Address address = createAddress(request, userAccount, addressType);

         // if the new address is default, we need to unset default for all other addresses of the user
         if (address.isDefault()) {
             // Unset default for all other addresses of the user
                unsetDefaultAddress(userAccount.getId(), addressType);
         } else {
             // If the new address is not default and there are no other default addresses, set this one as default
             boolean hasDefault = hasDefaultAddress(userAccount.getId(), addressType);
             if (!hasDefault) {
                 address.setDefault(true);
             }
         }

        return convertToDto(addressRepository.save(address));
    }

    private Address createAddress(AddAddressRequest request, UserAccount userAccount, AddressType addressType) {
        Address address = new Address();
        address.setStreet(request.getStreet());
        address.setHouseNumber(request.getHouseNumber());
        address.setFloor(request.getFloor());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZip(request.getZip());
        address.setCountry(request.getCountry());
        address.setUserAccount(userAccount);
        address.setAddressType(addressType);
        address.setDefault(request.isDefault());
        return address;
    }


    @Override
    @Transactional
    public AddressDto updateAddress(Long addressId, UpdateAddressRequest request, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()->new ResourceNotFoundException("Address not found with id: " + addressId));
        UserAccount userAccount = userAccountRepository.findUserAccountByUserId(userId)
                .orElseThrow(()->new ResourceNotFoundException("User account not found for user id: " + userId));
        validateAddressOwnership(address, userAccount);
       // Basically if the request contains a new address type we resolve it and use it,
        // otherwise we keep the existing address type of the address
        AddressType addressType = request.getAddressType() != null ?
                resolveAddressType(request.getAddressType()) : address.getAddressType();
        // if the updated address is default, we need to unset default for all other addresses of the user for the same address type

        Address updatedAddress =updateExistingAddress(address, request);

        if (updatedAddress.isDefault()) {
            unsetDefaultAddress(address.getUserAccount().getId(), addressType);
        }else {
            // If the updated address is not default and there are no other default addresses, set this one as default
            boolean hasDefault = hasDefaultAddress(userAccount.getId(), addressType);
            if (!hasDefault) {
                address.setDefault(true);
            }
        }

        return convertToDto(addressRepository.save(updatedAddress));
    }

    private Address updateExistingAddress(Address address, UpdateAddressRequest request) {
        Optional.ofNullable(request.getStreet()).ifPresent(address::setStreet);
        Optional.ofNullable(request.getHouseNumber()).ifPresent(address::setHouseNumber);
        Optional.ofNullable(request.getFloor()).ifPresent(address::setFloor);
        Optional.ofNullable(request.getCity()).ifPresent(address::setCity);
        Optional.ofNullable(request.getState()).ifPresent(address::setState);
        Optional.ofNullable(request.getZip()).ifPresent(address::setZip);
        Optional.ofNullable(request.getCountry()).ifPresent(address::setCountry);
        Optional.ofNullable(request.getAddressType()).ifPresent(type -> address.setAddressType(resolveAddressType(type)));
        Optional.of(request.isDefault()).ifPresent(address::setDefault);
        return address;
    }

    @Override
    @Transactional
    public void deleteAddressById(Long addressId, Long userId) {
        UserAccount userAccount = userAccountRepository.findUserAccountByUserId(userId)
                .orElseThrow(()->new ResourceNotFoundException("User account not found for user id: " + userId));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()->new ResourceNotFoundException("Address not found with id: " + addressId));
        validateAddressOwnership(address, userAccount);
        // if the deleted address is default, we need to set another address of the user as default if there are any other addresses
        if (address.isDefault()) {
            List<Address> otherAddresses = addressRepository.findAllByUserAccountIdAndAddressType(userAccount.getId(), address.getAddressType())
                    .stream()
                    .filter(addr -> !addr.getId().equals(address.getId())) // Exclude the current address
                    .toList();
            if (!otherAddresses.isEmpty()) {
                Address newDefault = otherAddresses.get(0);
                newDefault.setDefault(true);
                addressRepository.save(newDefault);
            }
        }
        addressRepository.delete(address); 

    }

    @Override
    public AddressDto getAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .map(this::convertToDto)
                .orElseThrow(()->new ResourceNotFoundException("Address not found with id: " + addressId));
    }

    @Override
    public List<AddressDto> getAllAddress() {
        return convertToDtoList(addressRepository.findAll());
    }

    @Override
    public List<AddressDto> getAddressByCountry(String country) {
        return addressRepository.findByCountry(country)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<AddressDto> getAddressByCity(String city) {
        return addressRepository.findByCity(city)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<AddressDto> getAddressByState(String state) {
        return addressRepository.findByState(state)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<AddressDto> getAllAddressByUserId(Long userId) {
        UserAccount userAccount = userAccountRepository.findUserAccountByUserId(userId)
                .orElseThrow(()->new ResourceNotFoundException("User account not found for user id: " + userId));
        return addressRepository.findAllByUserAccountId(userAccount.getId())
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public AddressDto getDefaultAddressByUserId(Long userId) {
        UserAccount userAccount = userAccountRepository.findUserAccountByUserId(userId)
                .orElseThrow(()->new ResourceNotFoundException("User account not found for user id: " + userId));
        return addressRepository.findDefaultAddressByUserAccountId(userAccount.getId())
                .map(this::convertToDto)
                .orElseThrow(()->new ResourceNotFoundException("Default address not found for user id: " + userId));
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        UserAccount userAccount = userAccountRepository.findUserAccountByUserId(userId)
                .orElseThrow(()->new ResourceNotFoundException("User account not found for user id: " + userId));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()->new ResourceNotFoundException("Address not found with id: " + addressId));
        validateAddressOwnership(address, userAccount);
        // Unset all other addresses
        // basically we get all addresses of the user 
        // and set default to false for the ones that are currently default
        // then we set default to true for the selected address
        unsetDefaultAddress(userAccount.getId(), address.getAddressType());
        // Set new default address
        address.setDefault(true);
        addressRepository.save(address);
    }

    @Override
    public AddressDto convertToDto(Address address) {
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public List<AddressDto> convertToDtoList(List<Address> addresses) {
        return addresses.stream()
                .map(this::convertToDto)
                .toList();
    }

    private void validateAddressOwnership(Address address, UserAccount userAccount) {
        if (!address.getUserAccount().getId().equals(userAccount.getId())) {
            throw new GeneralException("Address with id: " + address.getId() + " does not belong to user with id: " + userAccount.getUser().getId());
        }
    }

    private AddressType resolveAddressType(String type) {
        if (type==null || type.trim().isEmpty()) {
            throw new ResourceNotFoundException("Address type is required and cannot be empty");
        }
        try {
            return AddressType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResourceProcessingException("Invalid address type: " + type +
                    ". Valid types are: " + Arrays.toString(AddressType.values()));
        }
    }

    // basically we are fetching all addresses of the user for the given address type
    // and then we are checking if any of the addresses is default,
    // if it is then we set default to false and save the address
    private void unsetDefaultAddress(Long userAccountId, AddressType addressType) {
        List<Address> defaultAddresses = addressRepository.findAllByUserAccountIdAndAddressType(userAccountId, addressType)
                .stream().filter(Address::isDefault)
                .peek(address -> address.setDefault(false))
                .toList();
        addressRepository.saveAll(defaultAddresses);
    }

    private boolean hasDefaultAddress(Long userAccountId, AddressType addressType) {
        return addressRepository.findAllByUserAccountIdAndAddressType(userAccountId, addressType)
                .stream()
                .anyMatch(Address::isDefault);
    }

    @Override
    public boolean existsByIdAndUserAccountId(Long addressId, Long userAccountId) {
        return addressRepository.existsByIdAndUserAccountId(addressId, userAccountId);
    }
}
