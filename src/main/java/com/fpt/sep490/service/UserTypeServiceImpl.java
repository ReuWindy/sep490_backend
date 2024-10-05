package com.fpt.sep490.service;

import com.fpt.sep490.model.UserType;
import com.fpt.sep490.repository.UserTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserTypeServiceImpl implements UserTypeService {

    private final UserTypeRepository userTypeRepository;

    public UserTypeServiceImpl(UserTypeRepository userTypeRepository){
        this.userTypeRepository = userTypeRepository;
    }

    @Override
    public List<UserType> getAllUserType() {
        return userTypeRepository.findAll();
    }

    @Override
    public UserType getUserTypeById(int id) {
        Optional<UserType> userType = userTypeRepository.findById((long) id);
        return userType.orElse(null);
    }

    @Override
    public UserType createUserType(UserType userType) {
        UserType newUserType = new UserType();
        newUserType.setName(userType.getName());
        userTypeRepository.save(newUserType);
        return newUserType;
    }

    @Override
    public UserType updateUserType(UserType userType) {
        UserType existingUserType = userTypeRepository.findById((long)userType.getId()).orElse(null);
        if(existingUserType != null){
            existingUserType.setName(userType.getName());
            userTypeRepository.save(existingUserType);
            return existingUserType;
        }
        return null;
    }

    @Override
    public UserType deleteUserType(int id) {
        return null;
    }
}
