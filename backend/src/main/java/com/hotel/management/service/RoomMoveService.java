package com.hotel.management.service;

import com.hotel.management.dto.request.RoomMoveRequest;
import com.hotel.management.dto.response.ReservationResponse;
import com.hotel.management.entity.User;

import java.util.List;

public interface RoomMoveService {
    ReservationResponse moveRoom(RoomMoveRequest request, User user);
    List<Object> getRoomMoveHistory(Long reservationId);
}
