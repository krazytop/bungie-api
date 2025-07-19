package com.krazytop.bungie.exception;

import com.krazytop.bungie.model.generated.ApiErrorDTO;
import com.krazytop.bungie.model.generated.ApiErrorDTODetailsInner;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class CustomApiError extends ApiErrorDTO {

    public static ResponseEntity<ApiErrorDTO> buildApiResponseError(ApiErrorEnum errorEnum) {
        return new ResponseEntity<>(new ApiErrorDTO().setCode(errorEnum.getCode()).setMessage(errorEnum.getMessage()), errorEnum.getStatus());
    }

    public static ResponseEntity<ApiErrorDTO> buildApiResponseError(ApiErrorEnum errorEnum, List<ApiErrorDTODetailsInner> details) {
        return new ResponseEntity<>(new ApiErrorDTO().setCode(errorEnum.getCode()).setMessage(errorEnum.getMessage()).setDetails(details), errorEnum.getStatus());
    }
}
