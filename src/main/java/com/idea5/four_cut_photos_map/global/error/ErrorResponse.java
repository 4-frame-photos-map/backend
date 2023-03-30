package com.idea5.four_cut_photos_map.global.error;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
@Builder
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ErrorResponse {
    private String errorCode;
    private String errorMessage;

    public static ErrorResponse of(String errorCode, String errorMessage) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }

    // 에러 정보들을 [field] : msg 로 구조화
    public static ErrorResponse of(String errorCode, String[] errorMessages){
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(createErrorMessage(errorMessages))
                .build();
    }


    // 에러 정보들을 BindingResult을 통해 처리
    public static ErrorResponse of(String errorCode, BindingResult bindingResult){
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(createErrorMessage(bindingResult))
                .build();

    }

    private static String createErrorMessage(BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            if(!isFirst){
                sb.append(", ");
            }
            else {
                isFirst = false;
            }
            sb.append("[");
            sb.append((fieldError.getField()));
            sb.append("] ");
            sb.append(fieldError.getDefaultMessage());
        }

        return sb.toString();

    }

    private static String createErrorMessage(String[] errorMessages) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;

        for (String errorMessage : errorMessages) {
            errorMessage = errorMessage.split("\\.")[1];
            if(!isFirst){
                sb.append(", ");
            }
            else {
                isFirst = false;
            }
            sb.append("[");
            sb.append((errorMessage.split(":")[0])); // error Field
            sb.append("] ");
            sb.append(errorMessage.split(":")[1]); // error Message
        }
        return sb.toString();
    }

}
