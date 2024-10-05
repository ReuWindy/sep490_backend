package com.fpt.sep490.service;


import com.fpt.sep490.model.UserType;

import java.util.List;

public interface UserTypeService {

    List<UserType> getAllUserType();
    UserType getUserTypeById(int id);
    UserType createUserType(UserType userType);
    UserType updateUserType(UserType userType);
    UserType deleteUserType(int id);

}
