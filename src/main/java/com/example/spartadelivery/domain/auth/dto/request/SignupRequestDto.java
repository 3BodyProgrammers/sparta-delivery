package com.example.spartadelivery.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank(message = "이메일은 필수값입니다.")
    @Email
    private String email;

    @NotBlank(message = "비밀번호는 필수값입니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "비밀번호는 대소문자 영문, 숫자, 특수문자를 최소 1글자씩 포함하며, 8자 이상 20자 이하이어야 합니다."
    )
    private String password;

    @NotBlank(message = "이름은 필수값입니다.")
    @Size(max = 20, message = "이름은 최대 20자 입니다.")
    private String name;

    @NotBlank(message = "유저 권한은 필수값입니다.")
    @Size(max = 10, message = "권한은 최대 10자 입니다.")
    private String userRole;
}
