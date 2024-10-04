package com.example.myprojectbackend.entity.vo.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AccountPrivacyVO {
    @Pattern(regexp = "phone|email|wechat|qq|gender")
    String type;
    boolean value;
}
