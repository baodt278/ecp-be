package org.ecp.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.ecp.backend.constant.CommonConstant;
import org.ecp.backend.dto.request.NewsRequest;
import org.ecp.backend.dto.response.NewResponse;
import org.ecp.backend.dto.response.ServerResponseDto;
import org.ecp.backend.entity.News;
import org.ecp.backend.exception.ApplicationRuntimeException;
import org.ecp.backend.repository.ContractRepository;
import org.ecp.backend.repository.NewsRepository;
import org.ecp.backend.service.MinioService;
import org.ecp.backend.service.NewsService;
import org.ecp.backend.utils.GenerateUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepo;
    private final MinioService minioService;
    private final ContractRepository contractRepo;

    @Override
    public ServerResponseDto getSystemNews() {
        List<News> news = newsRepo.findSystemNews();
        List<NewResponse> newsResponses = news.stream()
                .map(this::getNews)
                .toList();
        return new ServerResponseDto(CommonConstant.SUCCESS, newsResponses);
    }

    @Override
    public ServerResponseDto getLocalNewsAll() {
        List<News> news = newsRepo.findAll();
        List<News> news1 = newsRepo.findByAcronym(null);
        news.removeAll(news1);
        List<NewResponse> newsResponses = news.stream()
                .map(this::getNews)
                .toList();
        return new ServerResponseDto(CommonConstant.SUCCESS, newsResponses);
    }

    @Override
    public ServerResponseDto createGlobalNews(NewsRequest dto) {
        try {
            News news = new News();
            news.setCode(GenerateUtils.generatedCode());
            news.setAuthor(dto.getAuthor());
            news.setContent(dto.getContent());
            news.setTitle(dto.getTitle());
            news.setTime(new Date());
            news.setImages(dto.getImages() == null ? null : Arrays.stream(dto.getImages()).map(minioService::uploadFile).toList());
            news.setAcronym(null);
            newsRepo.save(news);
        } catch (Exception e){
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Tạo tin tức thất bại!");
        }
        return new ServerResponseDto(CommonConstant.SUCCESS, "Tạo tin tức thành công!");
    }

    @Override
    public ServerResponseDto deleteNews(String code) {
        News news = newsRepo.findByCode(code)
                .orElseThrow(() -> new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Không tìm thấy tin tức!"));
        newsRepo.delete(news);
        return new ServerResponseDto(CommonConstant.SUCCESS, "Xóa tin tức thành công!");
    }
    @Override
    public ServerResponseDto createLocalNews(String acronym, NewsRequest dto) {
        try {
            News news = new News();
            news.setCode(GenerateUtils.generatedCode());
            news.setAuthor(dto.getAuthor());
            news.setContent(dto.getContent());
            news.setTitle(dto.getTitle());
            news.setTime(new Date());
            news.setImages(dto.getImages() == null ? null : Arrays.stream(dto.getImages()).map(minioService::uploadFile).toList());
            news.setAcronym(acronym);
            newsRepo.save(news);
        } catch (Exception e){
            throw new ApplicationRuntimeException(CommonConstant.INTERNAL_SERVER_ERROR, "Tạo tin tức thất bại!");
        }
        return new ServerResponseDto(CommonConstant.SUCCESS, "Tạo tin tức thành công!");
    }

    @Override
    public ServerResponseDto getLocalNewsForEmployee(String acronym) {
        List<News> news = newsRepo.findByAcronym(acronym);
        List<NewResponse> newsResponses = news.stream()
                .map(this::getNews)
                .toList();
        return new ServerResponseDto(CommonConstant.SUCCESS, newsResponses);
    }

    @Override
    public ServerResponseDto getLocalNewsForUser(String username) {
        List<NewResponse> responses = new ArrayList<>();
        List<String> acronyms = contractRepo.findCompanyAcronymByClient(username);
        for (String acronym : acronyms) {
            List<News> news = newsRepo.findByAcronym(acronym);
            List<NewResponse> newsResponses = news.stream()
                    .map(this::getNews)
                    .toList();
            responses.addAll(newsResponses);
        }
        return new ServerResponseDto(CommonConstant.SUCCESS, responses);
    }

    public NewResponse getNews(News news) {
        List<String> imageUrls = news.getImages() == null ? null :
                news.getImages().stream()
                        .map(minioService::getUrl)
                        .toList();
        return NewResponse.builder()
                .code(news.getCode())
                .author(news.getAuthor())
                .title(news.getTitle())
                .content(news.getContent())
                .time(news.getTime())
                .acronym(news.getAcronym())
                .imageUrl(imageUrls)
                .build();
    }
}
