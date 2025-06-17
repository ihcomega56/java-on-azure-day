package com.example.ticketreservation.dto;

/**
 * 予約情報のデータ転送オブジェクト（DTO）
 * APIレスポンスやリクエストでのデータ交換に使用
 */
public record ReservationDto(
    Long id,
    Long eventId,
    String eventName,
    String email,
    Integer quantity,
    String confirmationCode,
    LocalDateTime reservationTime,
    LocalDateTime createdAt
) {
    
    /**
     * ReservationエンティティからReservationDtoを作成するファクトリメソッド
     * 
     * @param reservation Reservationエンティティ
     * @return ReservationDto
     */
    public static ReservationDto fromEntity(Reservation reservation) {
        return new ReservationDto(
            reservation.getId(),
            reservation.getEvent().getId(),
            reservation.getEvent().getEventName(),
            reservation.getEmail(),
            reservation.getQuantity(),
            reservation.getConfirmationCode(),
            reservation.getReservationTime(),
            reservation.getCreatedAt()
        );
    }
}