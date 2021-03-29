package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {

//		// for test : 잘 돌아가는가!?!?
//		Hello hello = new Hello();
//		hello.setStr("hi");
//		String str = hello.getStr();
//		System.out.println("str = " + str);

		SpringApplication.run(JpashopApplication.class, args);
	}

}
