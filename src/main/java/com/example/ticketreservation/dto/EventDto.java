package com.example.ticketreservation.dto;

/**
 * イベント情報のデータ転送オブジェクト（DTO）
 * APIレスポンスやリクエストでのデータ交換に使用
 */
public record EventDto(
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
) {
    
    /**
     * EventエンティティからEventDtoを作成するファクトリメソッド
     * 
     * @param event Eventエンティティ
     * @return EventDto
     */
    public static EventDto fromEntity(Event event) {
        return new EventDto(
            event.getId(),
            event.getEventName(),
            event.getDescription(),
            event.getEventDate(),
            event.getVenue(),
            event.getCategory(),
            event.getTotalSeats(),
            event.getAvailableSeats(),
            event.getPrice(),
            event.getCreatedAt(),
            event.getUpdatedAt()
        );
    }
    
    /**
     * EventDtoからEventエンティティを作成するメソッド
     * 新規作成時に使用（IDなし）
     * 
     * @return Event エンティティ
     */
    public Event toEntity() {
        return new Event(
            this.eventName,
            this.description,
            this.eventDate,
            this.venue,
            this.category,
            this.totalSeats,
            this.price
        );
    }
    
    /**
     * 既存のEventエンティティに値を設定するメソッド
     * 更新時に使用
     * 
     * @param event 更新対象のEventエンティティ
     */
    public void updateEntity(Event event) {
        event.setEventName(this.eventName);
        event.setDescription(this.description);
        event.setEventDate(this.eventDate);
        event.setVenue(this.venue);
        event.setCategory(this.category);
        event.setTotalSeats(this.totalSeats);
        event.setAvailableSeats(this.availableSeats);
        event.setPrice(this.price);
    }
}