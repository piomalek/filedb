package ch.pmalek.filedb.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result {
    private String statusCode;
    private String message;
}
