package com.springboot.domain.place.service;

import com.springboot.domain.place.dto.PlaceResponseDto;
import com.springboot.domain.place.entity.Places;
import com.springboot.domain.place.entity.PlacesRepository;
import com.springboot.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Console;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import static com.springboot.global.error.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class PlaceService {

    private final PlacesRepository placesRepository;

    @Transactional(readOnly = true)
    public PlaceResponseDto getPlace(Long placeId) {
        Places entity = placesRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException(PLACE_NOT_FOUND, "해당 id의 장소가 없습니다 : " + placeId));
        return new PlaceResponseDto(entity);
    }

    @Transactional(readOnly = true)
    public List<PlaceResponseDto> getAllPlace() {
        List<Places> placesList = placesRepository.findAll();
        List<PlaceResponseDto> list = placesList.stream()
                .map(entity -> PlaceResponseDto.builder()
                        .entity(entity)
                        .build())
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            throw new EntityNotFoundException(PLACE_NOT_FOUND, "장소 리스트가 없습니다." );
        }
        return list;
    }

    @Transactional(readOnly = true)
    public List<PlaceResponseDto> getPlaceByRegion(String name) {
        List<Places> placesList = placesRepository.findByRegion(name);
        System.out.println(placesList.size());
        List<PlaceResponseDto> list = placesList.stream()
                .map(entity -> PlaceResponseDto.builder()
                        .entity(entity)
                        .build())
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            throw new EntityNotFoundException(REGION_NOT_FOUND, "입력한 지역이 없습니다 : " + name);
        }
        return list;
    }
    @Transactional(readOnly = true)
    public List<PlaceResponseDto> getPlaceByRegionAndName(String name) {

        name = name.trim();
        List<Places> placesList = placesRepository.findByNameOrRegion(name);

        String finalName = name;
        List<PlaceResponseDto> list = placesList.stream()

                .map(entity -> PlaceResponseDto.builder()
                        .entity(entity)
                        .build())

                .collect(Collectors.toList());
        placesList.stream()
            .forEach(entity -> System.out.println(entity.getName()));
        if (list.isEmpty()) {
            throw new EntityNotFoundException(PLACE_NOT_FOUND, "입력한 장소나 지역이 없습니다 : " + name);
        }
        return list;
    }
}
