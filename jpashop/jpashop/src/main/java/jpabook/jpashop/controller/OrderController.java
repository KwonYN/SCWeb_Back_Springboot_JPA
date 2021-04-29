package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    // Member, Item을 선택할 수 있어야 하기에 Dependency 많이 필요;
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    /**
     * 상품 주문
     */
    @GetMapping("/order")
    public String createForm(Model model) {

        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count) {
        Long orderId = orderService.order(memberId, itemId, count);

        return "redirect:/orders";
    }

    /**
     * 주문 내역
     */
    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch,
                            Model model) {
        List<Order> orders = orderService.findOrders(orderSearch);
        /*
            orderRepository.findAllByString(orderSearch);
            물론 단순히 조회하는 것은 바로 Repository에 접근해서 해도 무방함.
            굳이 Service에서 단순 위잏마기 위한 findOrders 메서드를 이용할 필요가 있을까? 라는 생각은 해볼 수 있음.
            어떻게 아키텍처를 구성할 것인지의 문제
         */

        model.addAttribute("orders", orders);
        /*
            // JPA orderSearch는 orders에 포함되어 있다고 봄
            model.addAttribute("orderSearch", orderSearch);
            코드가 생략되어 있다고 생각하면 됨.
         */
        return "order/orderList";
    }

    /**
     * 주문 취소
     */
    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
