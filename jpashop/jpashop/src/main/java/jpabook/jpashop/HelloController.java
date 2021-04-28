package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")    // https://example.com/hello url로 요청이 들어오게끔!
    public String hello(Model model) {
        // 데이터(argument)를 View에 넘기는 것 - key: value 관계
        model.addAttribute("data", "hidoyi!!");
        return "hello"; // hello라고 썼지만 (관례상) hello.html이 리턴되는 거라고 함!
                        //    → ../../resources/templates/hello.html이 렌더링 된다고 함!
    }
}
