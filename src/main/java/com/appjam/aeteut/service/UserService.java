package com.appjam.aeteut.service;

import com.appjam.aeteut.domain.User;
import com.appjam.aeteut.dto.user.UserRequestDto;
import com.appjam.aeteut.dto.user.UserResponseDto;
import com.appjam.aeteut.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponseDto::from)
                .collect(Collectors.toList());
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        return UserResponseDto.from(user);
    }

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = userRepository.save(
                User.builder()
                        .name(userRequestDto.getName())
                        .email(userRequestDto.getEmail())
                        .build()
        );

        return UserResponseDto.from(user);
    }

}
