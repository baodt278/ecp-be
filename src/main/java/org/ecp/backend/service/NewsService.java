package org.ecp.backend.service;

import org.ecp.backend.dto.request.NewsRequest;
import org.ecp.backend.dto.response.ServerResponseDto;

public interface NewsService {
    ServerResponseDto getSystemNews();

    ServerResponseDto getLocalNewsAll();

    ServerResponseDto createGlobalNews(NewsRequest dto);

    ServerResponseDto deleteNews(String code);

    ServerResponseDto createLocalNews(String acronym, NewsRequest dto);

    ServerResponseDto getLocalNewsForEmployee(String acronym);

    ServerResponseDto getLocalNewsForUser(String username);
}
