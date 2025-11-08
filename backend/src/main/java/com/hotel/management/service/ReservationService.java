package com.hotel.management.service;

import com.hotel.management.dto.request.ReservationRequest;
import com.hotel.management.dto.response.ReservationResponse;
import com.hotel.management.entity.User;

import java.util.List;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request, User user);
    ReservationResponse getReservationById(Long id);
    List<ReservationResponse> getMyReservations(User user);
    ReservationResponse cancelReservation(Long id, User user);
    ReservationResponse checkIn(Long id);
    ReservationResponse checkOut(Long id);
}
