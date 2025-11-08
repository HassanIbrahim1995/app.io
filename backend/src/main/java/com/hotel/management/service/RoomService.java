package com.hotel.management.service;

import com.hotel.management.dto.request.RoomSearchRequest;
import com.hotel.management.dto.response.RoomResponse;
import com.hotel.management.entity.Room;

import java.util.List;

public interface RoomService {
    List<RoomResponse> getAllRooms();
    RoomResponse getRoomById(Long id);
    List<RoomResponse> searchAvailableRooms(RoomSearchRequest searchRequest);
    Room getRoomEntityById(Long id);
}
