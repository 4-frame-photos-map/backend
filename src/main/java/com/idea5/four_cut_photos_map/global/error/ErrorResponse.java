package com.idea5.four_cut_photos_map.global.error;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;

import javax.validation.ConstraintViolationException;
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


    // 에러 정보들을 BindingResult을 통해 처리
    public static ErrorResponse of(String errorCode, BindingResult bindingResult){
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(createErrorMessage(bindingResult))
                .build();

    }

    // ConstraintViolationException 정보들을 [field] : msg 로 구조화
    public static ErrorResponse of(String errorCode, ConstraintViolationException e){
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(createErrorMessage(e))
                .build();
    }

    // MissingServletRequestParameterException 정보들을 [field] : msg 로 구조화
    public static ErrorResponse of(String errorCode, MissingServletRequestParameterException e){
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(createErrorMessage(e))
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

    private static String createErrorMessage(ConstraintViolationException e) {
        String[] errorMessages = e.getMessage().split(",");

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
            sb.append(errorMessage.split(":")[1].trim()); // error Message
        }
        return sb.toString();
    }

    private static String createErrorMessage(MissingServletRequestParameterException e) {
        return "[" + e.getParameterName() + "] " + ErrorCode.MISSING_PARAMETER.getMessage();
    }

}