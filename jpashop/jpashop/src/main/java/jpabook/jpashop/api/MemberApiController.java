package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController // @Controller @ResponseBody : 이 두 어노테이션을 동시에 포함
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 회원 등록 API
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        // @RequestBody : json(ex.)으로 온 Body를 Member 변수에 그대로 쫙 넣어주면서 맵핑
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2
                            (@RequestBody @Valid CreateMemberRequest request) {
                            // 별도의 Data Transfer Object(DTO) 사용
        Member member = new Member();
        member.setName(request.getName());
        member.setAddress(request.getAddress());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 회원 수정 API
     */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
                       @PathVariable("id") Long id,
                       @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    /**
     * 회원 조회 API
     */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public memberList membersV2() {
        List<Member> findMembers = memberService.findMembers();

        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new memberList(collect.size(), collect);
    }


    /**
     * V1
     */
    // 회원 등록 res
    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }


    /**
     * V2
     */
    // 회원 등록 req (DTO)
    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
        private Address address;
    }


    // 회원 등록 req
    @Data
    static class UpdateMemberRequest {
        private String name;
    }
    // 회원 수정 res (DTO)
    @Data   @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }


    // 회원 조회
    @Data   @AllArgsConstructor
    static class memberList<T> {
        private int count;
        private T memberData; // memberData 필드의 값으로 List가 주어짐 (∵ Collectors.toList())
    }
    // 회원 조회 DTO
    @Data   @AllArgsConstructor
    static class MemberDto {
        private String name;
    }
}
