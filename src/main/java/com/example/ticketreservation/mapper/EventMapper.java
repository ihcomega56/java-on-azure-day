package com.example.ticketreservation.mapper;

import com.example.ticketreservation.dto.EventRecord;
import com.example.ticketreservation.model.Event;

/**
 * EventエンティティとEventRecord DTOの相互変換を行うマッパークラス
 */
public class EventMapper {
    
    /**
     * EventエンティティをEventRecord DTOに変換
     * 
     * @param entity 変換元のEventエンティティ
     * @return 変換されたEventRecord DTO、引数がnullの場合はnull
     */
    public static EventRecord toDto(Event entity) {
        if (entity == null) {
            return null;
        }
        
        return new EventRecord(
            entity.getId(),
            entity.getEventName(),
            entity.getDescription(),
            entity.getEventDate(),
            entity.getVenue(),
            entity.getCategory(),
            entity.getTotalSeats(),
            entity.getAvailableSeats(),
            entity.getPrice(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    
    /**
     * EventRecord DTOをEventエンティティに変換
     * 
     * @param dto 変換元のEventRecord DTO
     * @return 変換されたEventエンティティ、引数がnullの場合はnull
     */
    public static Event toEntity(EventRecord dto) {
        if (dto == null) {
            return null;
        }
        
        Event entity = new Event();
        entity.setId(dto.id());
        entity.setEventName(dto.eventName());
        entity.setDescription(dto.description());
        entity.setEventDate(dto.eventDate());
        entity.setVenue(dto.venue());
        entity.setCategory(dto.category());
        entity.setTotalSeats(dto.totalSeats());
        entity.setAvailableSeats(dto.availableSeats());
        entity.setPrice(dto.price());
        entity.setCreatedAt(dto.createdAt());
        entity.setUpdatedAt(dto.updatedAt());
        
        return entity;
    }
}