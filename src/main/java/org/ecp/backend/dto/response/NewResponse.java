package org.ecp.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class NewResponse {
    private String code;
    private String author;
    private String title;
    private String content;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date time;
    private String acronym;

    private List<String> imageUrl;
}
