package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repositories.AddressRepository;
import com.ecommerce.project.repositories.UserRepository;
import com.ecommerce.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService{
    @Autowired
    private AddressRepository addressRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    UserRepository userRepository;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);
        address.setUser(user);
        List<Address> addressesList = user.getAddresses();
        addressesList.add(address);
        user.setAddresses(addressesList);
        Address savedAddress = addressRepo.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address>addresses=addressRepo.findAll();
       List<AddressDTO>addressDTOS= addresses.stream().map(address -> modelMapper.map(address,AddressDTO.class)).collect(Collectors.toList());
        return addressDTOS;
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address=addressRepo.findById(addressId)
        .orElseThrow(()-> new ResourceNotFoundException("address","addressId",addressId));
        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        List<Address>addresses=user.getAddresses();
        List<AddressDTO>addressDTOS= addresses.stream().map(address -> modelMapper.map(address,AddressDTO.class)).collect(Collectors.toList());
        return addressDTOS;
    }

    @Override
    public AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO) {
        Address addressFromDatabase=addressRepo.findById(addressId)
                .orElseThrow(()->new ResourceNotFoundException("Address","address",addressId));
        addressFromDatabase.setCity(addressDTO.getCity());
        addressFromDatabase.setPincode(addressDTO.getPincode());
        addressFromDatabase.setCountry(addressDTO.getCountry());
        addressFromDatabase.setStreet(addressDTO.getStreet());
        addressFromDatabase.setState(addressDTO.getState());
        addressFromDatabase.setBuildingName(addressDTO.getBuildingName());
        Address updatedAddress=addressRepo.save(addressFromDatabase);
        User user=addressFromDatabase.getUser();
        user.getAddresses().removeIf(address->address.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);
        return modelMapper.map(updatedAddress,AddressDTO.class);
    }

    @Override
    public String deleteAddressById(Long addressId) {
        Address addressFromDatabase=addressRepo.findById(addressId)
                .orElseThrow(()->new ResourceNotFoundException("Address","address",addressId));
        User user=addressFromDatabase.getUser();
        user.getAddresses().removeIf(address->address.getAddressId().equals(addressId));
        userRepository.save(user);
        addressRepo.delete(addressFromDatabase);
        return "Address deleted succcessfully" +addressId;
    }


}
