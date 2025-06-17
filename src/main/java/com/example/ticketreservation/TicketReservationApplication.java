package com.example.ticketreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * チケット予約システムのメインアプリケーションクラス
 * Spring Boot 3.2 + Java 21 対応
 */
@SpringBootApplication
public class TicketReservationApplication extends SpringBootServletInitializer {

    /**
     * アプリケーションのエントリーポイント
     * 
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(TicketReservationApplication.class, args);
    }

    /**
     * WARデプロイメント用設定
     * 従来のWARファイルとしてもデプロイ可能にするため
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TicketReservationApplication.class);
    }
}
