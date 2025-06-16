package com.example.ticketreservation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * Spring Boot Web MVC設定クラス
 * JSPビューリゾルバーの設定
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * JSP用ビューリゾルバーの設定
     * レガシーJSPファイルとの互換性を保つため
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        resolver.setOrder(1);
        registry.viewResolver(resolver);
    }
}
