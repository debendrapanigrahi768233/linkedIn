package com.debpro.linkedin.user_service.service;

import com.debpro.linkedin.user_service.dto.LoginRequestDto;
import com.debpro.linkedin.user_service.dto.SignupRequestDto;
import com.debpro.linkedin.user_service.dto.UserDto;
import com.debpro.linkedin.user_service.entity.User;
import com.debpro.linkedin.user_service.exception.BadRequestException;
import com.debpro.linkedin.user_service.exception.ResourceNotFoundException;
import com.debpro.linkedin.user_service.repository.UserRepository;
import com.debpro.linkedin.user_service.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JWTService jwtService;

    public UserDto signup(SignupRequestDto signupRequestDto) {
        boolean isExists = userRepository.existsByEmail(signupRequestDto.getEmail());
        if(isExists){
            throw new BadRequestException("User already exists");
        }
        User user = modelMapper.map(signupRequestDto,User.class);
        user.setPassword(PasswordUtil.hashPassword(signupRequestDto.getPassword()));

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    public String login(LoginRequestDto loginRequestDto) {
        User user = userRepository
                .findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with this email not exists"));
        boolean isPasswordMatch = PasswordUtil.matchPassword(loginRequestDto.getPassword(),user.getPassword());
        if(!isPasswordMatch) throw new BadRequestException("Incorrect details filled");
        return jwtService.generateAccessToken(user);
    }
}
