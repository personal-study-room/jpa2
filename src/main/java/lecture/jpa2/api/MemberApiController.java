package lecture.jpa2.api;

import jakarta.validation.Valid;
import lecture.jpa2.domain.Member;
import lecture.jpa2.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
  private final MemberService memberService;

  /**
   * @등록 V1: 요청 값으로 Member 엔티티를 직접 받는다.
   * @문제점
   * @- 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
   * @- 엔티티에 API 검증을 위한 로직이 들어간다. (@NotEmpty 등등)
   * @- 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 모든 요청 요구사항을 담기는 어렵다.
   * @- 엔티티가 변경되면 API 스펙이 변한다.
   *
   * @결론
   *
   * - API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받는다.
   */
  @PostMapping("/api/v1/members")
  public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
    Long id = memberService.join(member);
    return new CreateMemberResponse(id);
  }


  @PostMapping("/api/v2/members")
  public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
    Member member = new Member();
    member.setName(request.getName());

    Long id = memberService.join(member);
    return new CreateMemberResponse(id);
  }

  @Data
  @AllArgsConstructor
  public static class CreateMemberResponse {
    private Long id;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class CreateMemberRequest {
    private String name;
  }
}