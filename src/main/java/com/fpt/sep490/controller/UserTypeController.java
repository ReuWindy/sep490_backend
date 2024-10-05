package com.fpt.sep490.controller;

import com.fpt.sep490.exceptions.ApiExceptionResponse;
import com.fpt.sep490.model.Category;
import com.fpt.sep490.model.UserType;
import com.fpt.sep490.service.UserTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.processing.Generated;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/usertype")
public class  UserTypeController {
     private final UserTypeService userTypeService;

     public UserTypeController (UserTypeService userTypeService){
         this.userTypeService = userTypeService;
     }

    @GetMapping("/")
    public ResponseEntity<?> getAllUserTypes() {
        List<UserType> userTypes = userTypeService.getAllUserType();
        if (!userTypes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(userTypes);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserTypeById(@PathVariable int id) {
        UserType userType = userTypeService.getUserTypeById(id);
        if (userType != null) {
            return ResponseEntity.status(HttpStatus.OK).body(userType);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Not Found", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/createUserType")
    public ResponseEntity<?> createUserType(@RequestBody UserType userType) {
        UserType createdUserType = userTypeService.createUserType(userType);
        if (createdUserType != null) {

            return ResponseEntity.status(HttpStatus.CREATED).body(createdUserType);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Create Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/updateUserType")
    public ResponseEntity<?> updateUserType(@RequestBody UserType userType) {
        UserType updatedUserType = userTypeService.updateUserType(userType);
        if (updatedUserType != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updatedUserType);
        }
        final ApiExceptionResponse response = new ApiExceptionResponse("Update Failed", HttpStatus.BAD_REQUEST, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
