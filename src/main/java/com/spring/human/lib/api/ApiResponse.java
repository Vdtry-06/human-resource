package com.spring.human.lib.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

/*
    Khi deserialize (JSON → Java object), nếu JSON có những field không tồn tại trong class, Jackson sẽ bỏ qua chúng thay vì báo lỗi.
    ignoreUnknown = true giúp code an toàn khi JSON chứa thêm dữ liệu dư thừa

    Khi serialize (Java object → JSON), chỉ bao gồm những field không null trong JSON.
    Nếu field bằng null thì Jackson sẽ bỏ qua, không đưa vào JSON.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean status;
    private String message;

    @JsonIgnoreProperties
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonIgnoreProperties
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HttpStatus httpStatus;

    @JsonIgnoreProperties
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String moreInformation;

    public ApiResponse(boolean status) {
        this.status = status;
        if (status) {
            this.message = "Success";
        } else {
            this.message = "Failed";
        }
    }

    public ApiResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public ApiResponse(boolean status, T data) {
        this.status = status;
        if (status) {
            this.message = "Success";
        } else {
            this.message = "Failed";
        }
        this.data = data;
    }
}
