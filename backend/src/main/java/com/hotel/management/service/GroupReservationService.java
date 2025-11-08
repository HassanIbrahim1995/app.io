package com.hotel.management.service;

import com.hotel.management.dto.request.GroupReservationRequest;
import com.hotel.management.dto.response.ReservationResponse;
import com.hotel.management.entity.User;

import java.util.List;
import java.util.Map;

public interface GroupReservationService {
    Map<String, Object> createGroupReservation(GroupReservationRequest request, User user);
    Map<String, Object> getGroupReservation(Long id);
    List<Map<String, Object>> getAllGroupReservations();
    Map<String, Object> cancelGroupReservation(Long id, User user);
}
