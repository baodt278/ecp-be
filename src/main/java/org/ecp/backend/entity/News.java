package org.ecp.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.List;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String author;
    private String title;
    @Column(columnDefinition = "text")
    private String content;
    private Date time;
    private String companyName;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> images;
}
