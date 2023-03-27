package com.idea5.four_cut_photos_map.domain.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateReq {
    @NotBlank(message = "닉네임은 필수입력입니다.")
    private String nickname;
}
