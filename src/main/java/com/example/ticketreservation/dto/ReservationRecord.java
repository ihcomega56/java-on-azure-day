package com.example.ticketreservation.dto;

import java.time.LocalDateTime;
import com.example.ticketreservation.model.Reservation.ReservationStatus;

/**
 * 予約情報のDTO（Data Transfer Object）
 * Java Record型を使用して不変オブジェクトとして実装
 */
public record ReservationRecord(
    Long id,
    EventRecord event,
    String email,
    Integer quantity,
    Double totalPrice,
    String confirmationCode,
    LocalDateTime reservationTime,
    ReservationStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}