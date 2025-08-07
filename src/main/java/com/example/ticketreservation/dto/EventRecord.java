package com.example.ticketreservation.dto;

import java.time.LocalDateTime;

/**
 * イベント情報のDTO（Data Transfer Object）
 * Java Record型を使用して不変オブジェクトとして実装
 */
public record EventRecord(
    Long id,
    String eventName,
    String description,
    LocalDateTime eventDate,
    String venue,
    String category,
    Integer totalSeats,
    Integer availableSeats,
    Double price,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}