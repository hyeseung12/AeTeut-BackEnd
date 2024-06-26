package com.appjam.aeteut.service;

import com.appjam.aeteut.domain.Mortuary;
import com.appjam.aeteut.domain.Obituary;
import com.appjam.aeteut.domain.User;
import com.appjam.aeteut.dto.obituary.ObituaryRequestDto;
import com.appjam.aeteut.dto.obituary.ObituaryResponseDto;
import com.appjam.aeteut.dto.userobituarymapping.UserObituaryMappingRequestDto;
import com.appjam.aeteut.exception.ObituaryNotFoundException;
import com.appjam.aeteut.exception.UserNotFoundException;
import com.appjam.aeteut.repository.MortuaryRepository;
import com.appjam.aeteut.repository.ObituaryRepository;
import com.appjam.aeteut.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ObituaryService {

    private final EntityManager entityManager;

    private final ObituaryRepository obituaryRepository;
    private final MortuaryRepository mortuaryRepository;
    private final UserRepository userRepository;

    private final UserObituaryMappingService userObituaryMappingService;

    public List<ObituaryResponseDto> getAllObituaries() {
        return obituaryRepository.findAll()
                .stream()
                .map(ObituaryResponseDto::from)
                .collect(Collectors.toList());
    }

    public ObituaryResponseDto getObituaryById(Long id) {
        Obituary obituary = obituaryRepository.findById(id)
                .orElseThrow(() -> ObituaryNotFoundException.EXCEPTION);

        return ObituaryResponseDto.from(obituary);
    }

    public ObituaryResponseDto createObituary(ObituaryRequestDto obituaryRequestDto) {
        Mortuary mortuary = mortuaryRepository.save(
                Mortuary.builder()
                        .name(obituaryRequestDto.getMortuary().getName())
                        .lat(obituaryRequestDto.getMortuary().getLat())
                        .lng(obituaryRequestDto.getMortuary().getLng())
                        .build()
        );

        // 영속성 컨텍스트 초기화
        entityManager.clear();

        User user = userRepository.findById(obituaryRequestDto.getUserId())
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);

        Obituary obituary = obituaryRepository.save(
                Obituary.builder()
                        .mortuary(mortuary)
                        .user(user)
                        .name(obituaryRequestDto.getName())
                        .phoneNumber(obituaryRequestDto.getPhoneNumber())
                        .date(LocalDateTime.parse(obituaryRequestDto.getDate() + "T00:00:00"))
                        .build()
        );

        userObituaryMappingService.createMapping(
                new UserObituaryMappingRequestDto(user.getId(), obituary.getId())
        );

        return ObituaryResponseDto.from(obituary);
    }
}
