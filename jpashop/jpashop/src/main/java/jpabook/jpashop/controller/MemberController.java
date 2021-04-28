package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 등록(가입)
     */
    @GetMapping("/members/new") // 해당 경로를 열어봄
    public String createForm(Model model) {
        // Controller → View로 넘어갈 때, 맵핑한 데이터를 실어 넘겨줌
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new") // 해당 경로에 등록해줌. 이 때 "post"해준 memberForm이 넘어옴
    public String create(@Valid MemberForm form, BindingResult result) {
        // @Valid : MemberForm에 있는 @NotEmpty와 같은 어노테이션을 보고 Validation 자동 진행 (Ex. @NotNull, Negative, NotBlank, Size, ... 등등이 있음)

        if (result.hasErrors()) {
            return "members/createMemberForm";
            // 와~~ 어떤 에러가 있는지 createMemberForm 화면에 뿌려줌
            // 이는 thymeleaf-spring 둘이 Integration이 잘 되어 있기 때문이라고 함!!
            // 서버 사이드에서 Validation을 진행하고, 그 결과(에러 message)를 Client에 뿌려줌 ㄷㄷ
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";    // /(root)로 리턴함 (== home.html)
    }

    /**
     * 회원 조회
     */
    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
