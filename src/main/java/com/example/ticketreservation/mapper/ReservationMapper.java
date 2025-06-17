package com.example.ticketreservation.mapper;

import com.example.ticketreservation.dto.ReservationRecord;
import com.example.ticketreservation.model.Reservation;

/**
 * ReservationエンティティとReservationRecord DTOの相互変換を行うマッパークラス
 */
public class ReservationMapper {
    
    /**
     * ReservationエンティティをReservationRecord DTOに変換
     * 
     * @param entity 変換元のReservationエンティティ
     * @return 変換されたReservationRecord DTO、引数がnullの場合はnull
     */
    public static ReservationRecord toDto(Reservation entity) {
        if (entity == null) {
            return null;
        }
        
        return new ReservationRecord(
            entity.getId(),
            EventMapper.toDto(entity.getEvent()),
            entity.getEmail(),
            entity.getQuantity(),
            entity.getTotalPrice(),
            entity.getConfirmationCode(),
            entity.getReservationTime(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    
    /**
     * ReservationRecord DTOをReservationエンティティに変換
     * 
     * @param dto 変換元のReservationRecord DTO
     * @return 変換されたReservationエンティティ、引数がnullの場合はnull
     */
    public static Reservation toEntity(ReservationRecord dto) {
        if (dto == null) {
            return null;
        }
        
        Reservation entity = new Reservation();
        entity.setId(dto.id());
        entity.setEvent(EventMapper.toEntity(dto.event()));
        entity.setEmail(dto.email());
        entity.setQuantity(dto.quantity());
        entity.setTotalPrice(dto.totalPrice());
        entity.setConfirmationCode(dto.confirmationCode());
        entity.setReservationTime(dto.reservationTime());
        entity.setStatus(dto.status());
        entity.setCreatedAt(dto.createdAt());
        entity.setUpdatedAt(dto.updatedAt());
        
        return entity;
    }
}