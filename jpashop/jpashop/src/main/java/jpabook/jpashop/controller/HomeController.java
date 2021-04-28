package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {

    // @Slf4j 어노테이션으로 아래 코드 없어도 됨!
    /*Logger log = LoggerFactory.getLogger(getClass());*/

    @RequestMapping("/")
    public String home() {
        log.info("home controller");
        return "home";  // [template] home.html로 찾아가서 Thymeleaf 파일을 찾음!
    }
}
