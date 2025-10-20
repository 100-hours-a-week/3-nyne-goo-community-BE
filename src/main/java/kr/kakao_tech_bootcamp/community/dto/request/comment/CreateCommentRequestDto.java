package kr.kakao_tech_bootcamp.community.dto.request.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentRequestDto {
    private String content;
}
