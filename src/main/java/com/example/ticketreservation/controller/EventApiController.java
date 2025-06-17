package com.example.ticketreservation.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.ticketreservation.dto.EventDto;
import com.example.ticketreservation.service.EventService;

/**
 * イベント情報API用のREST コントローラー
 * JSON形式で全てのイベント情報を提供
 */
@RestController
@RequestMapping("/api/all-events")
public class EventApiController {

    @Autowired
    private EventService eventService;
    
    /**
     * 全てのイベント一覧を取得
     * 
     * @return イベント一覧（JSON形式）
     */
    @GetMapping
    public List<EventDto> getAllEvents() {
        return eventService.getAllEvents()
                .stream()
                .map(EventDto::fromEntity)
                .collect(Collectors.toList());
    }
}
