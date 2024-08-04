package lecture.jpa2.api;

import jakarta.validation.Valid;
import lecture.jpa2.domain.Address;
import lecture.jpa2.domain.Member;
import lecture.jpa2.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
  private final MemberService memberService;

  /**
   * @Entity리턴의 문제점
   * @- 기본적으로 무시하고 싶은 필드를 @JsonIgnore해주면 되지만, api 스펙에 따라서 entity가 자주 변경이 되어야 한다.
   * @- 하나의 Entity클래스가 모든 api 스펙에 맞출 수 없다.
   */
  @GetMapping("api/v1/members")
  public List<Member> membersV1() {
    return memberService.findMembers();
  }


  @GetMapping("/api/v2/members")
  public Result membersV2() {
    List<Member> members = memberService.findMembers();

    List<MemberDTO> collect = members.stream()
            .map(member -> new MemberDTO(member.getName()))
            .toList();

    return new Result(collect, collect.size());
  }

  @Data
  @AllArgsConstructor
  static class Result<T> {
    private T data;
    private int count;
  }


  @Data
  @AllArgsConstructor
  static class MemberDTO {
    private String name;
  }

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

  /**
   * @별도의 DTO를 세팅하는 장점
   * @- 데이터 계층과 프레젠테이션 계층을 분리할 수 있다.
   */
  @PostMapping("/api/v2/members")
  public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
    Member member = new Member();
    member.setName(request.getName());
    member.setAddress(request.getAddress());

    Long id = memberService.join(member);
    return new CreateMemberResponse(id);
  }

  @PutMapping("api/v2/members/{id}")
  public UpdateMemberResponse updateMemberV2(
          @PathVariable("id") Long id,
          @RequestBody @Valid UpdateMemberRequest request
  ) {
    memberService.update(id, request.getName());
    Member findMember = memberService.findOne(id);

    return new UpdateMemberResponse(findMember.getId(), findMember.getName());
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
    private Address address;
  }

  @Data
  static class UpdateMemberRequest {
    private String name;
  }

  @Data
  @AllArgsConstructor
  static class UpdateMemberResponse {
    private Long id;
    private String name;
  }


}