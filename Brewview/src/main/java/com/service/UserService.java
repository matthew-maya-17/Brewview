package com.service;

import com.dto.CreateUserRequest;
import com.dto.ResponseUser;
import com.exception.ResourceNotFoundException;
import com.model.User;
import com.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    //CREATE
    public ResponseUser addUser(CreateUserRequest userRequest){
        User newUser = userRepository.save(user);
        return Optional.of(newUser);
    }

    //READ
    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    public Optional<User> findUserById(String id){
        return userRepository.findById(id);
    }

    /*
    I need to come back to implement since JPA Repository doesn't include this method.
        - I need to create the JPQL method and create happy and unhappy test paths
     */
    public Optional<User> findUserByUsername(String username){
        return null;
    }

    /*
    I need to come back to implement since JPA Repository doesn't include this method.
        - I need to create the JPQL method and create happy and unhappy test paths
     */
    public Optional<User> findUserByEmail(String email){
        return null;
    }

    //UPDATE
    public Optional<User> updateUserById(String id){
        Optional<User> existingUser = userRepository.findById(id);

        if(existingUser.isEmpty()){
            throw new ResourceNotFoundException("User with ID: " + id + " does not exist.");
        }

        if(!userRepository.existsById(id)){
            throw new ResourceNotFoundException("User with ID: " + id + " does not exist." );
        }

        Optional<User> userToBeUpdated = userRepository.findById(id);

        userRepository.save(userToBeUpdated.get());
    }

    //DELETE
    public void deleteUserById(String id){
        if(!userRepository.existsById(id)){
            throw new ResourceNotFoundException("User with ID: " + id + " does not exist.");
        }
        userRepository.deleteById(id);
    }

    private User convertToEntity(CreateUserRequest userRequestInfo){
        User user = new User();
        user.setUsername(userRequestInfo.getUsername());
        user.setEmail(userRequestInfo.getEmail());

        return user;
    }
}
