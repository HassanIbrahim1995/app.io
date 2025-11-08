package com.hotel.management.service.impl;

import com.hotel.management.dto.request.RoomSearchRequest;
import com.hotel.management.dto.response.AmenityResponse;
import com.hotel.management.dto.response.RoomResponse;
import com.hotel.management.entity.Amenity;
import com.hotel.management.entity.Room;
import com.hotel.management.entity.enums.RoomStatus;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.ReviewRepository;
import com.hotel.management.repository.RoomRepository;
import com.hotel.management.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::mapToRoomResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoomResponse getRoomById(Long id) {
        Room room = getRoomEntityById(id);
        return mapToRoomResponse(room);
    }

    @Override
    public List<RoomResponse> searchAvailableRooms(RoomSearchRequest searchRequest) {
        List<Room> rooms;
        
        if (searchRequest.getCheckInDate() != null && searchRequest.getCheckOutDate() != null) {
            rooms = roomRepository.findAvailableRooms(
                    searchRequest.getCheckInDate(),
                    searchRequest.getCheckOutDate()
            );
        } else {
            rooms = roomRepository.findByStatus(RoomStatus.AVAILABLE);
        }

        return rooms.stream()
                .filter(room -> matchesSearchCriteria(room, searchRequest))
                .map(this::mapToRoomResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Room getRoomEntityById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
    }

    private boolean matchesSearchCriteria(Room room, RoomSearchRequest search) {
        if (search.getRoomTypeId() != null && !room.getRoomType().getId().equals(search.getRoomTypeId())) {
            return false;
        }
        if (search.getGuests() != null && room.getMaxOccupancy() < search.getGuests()) {
            return false;
        }
        if (search.getMinPrice() != null && room.getBasePrice().compareTo(search.getMinPrice()) < 0) {
            return false;
        }
        if (search.getMaxPrice() != null && room.getBasePrice().compareTo(search.getMaxPrice()) > 0) {
            return false;
        }
        if (search.getIsAccessible() != null && !room.getIsAccessible().equals(search.getIsAccessible())) {
            return false;
        }
        if (search.getIsSmokingAllowed() != null && !room.getIsSmokingAllowed().equals(search.getIsSmokingAllowed())) {
            return false;
        }
        return true;
    }

    private RoomResponse mapToRoomResponse(Room room) {
        Double averageRating = reviewRepository.findAverageRatingByRoomId(room.getId());
        
        List<String> imageUrls = room.getImageUrls() != null ?
                Arrays.asList(room.getImageUrls().split(",")) : List.of();

        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .roomTypeId(room.getRoomType().getId())
                .roomTypeName(room.getRoomType().getName())
                .floor(room.getFloor())
                .basePrice(room.getBasePrice())
                .status(room.getStatus())
                .maxOccupancy(room.getMaxOccupancy())
                .isAccessible(room.getIsAccessible())
                .isSmokingAllowed(room.getIsSmokingAllowed())
                .description(room.getDescription())
                .imageUrls(imageUrls)
                .amenities(room.getAmenities().stream()
                        .map(this::mapToAmenityResponse)
                        .collect(Collectors.toList()))
                .averageRating(averageRating)
                .build();
    }

    private AmenityResponse mapToAmenityResponse(Amenity amenity) {
        return AmenityResponse.builder()
                .id(amenity.getId())
                .name(amenity.getName())
                .description(amenity.getDescription())
                .iconName(amenity.getIconName())
                .isPremium(amenity.getIsPremium())
                .build();
    }
}
