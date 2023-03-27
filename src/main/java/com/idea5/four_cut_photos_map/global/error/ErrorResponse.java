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

    // 에러 정보들을 Error Field 통해 처리
    public static ErrorResponse of(String errorCode, String errorMessage, List<String> errorFieldList){
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(createErrorMessage(errorMessage, errorFieldList))
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

    private static String createErrorMessage(String errorMessage, List<String> errorFieldList) {
        StringBuilder sb = new StringBuilder();
        String[] msg = errorMessage.split(",");
        for (int i=0; i<errorFieldList.size(); i++) {
            if(i != 0){
                sb.append(", ");
            }
            sb.append("[");
            sb.append((errorFieldList.get(i)));
            sb.append("] ");
            sb.append(msg[i].split(":")[1]);
        }
        return sb.toString();
    }

}
