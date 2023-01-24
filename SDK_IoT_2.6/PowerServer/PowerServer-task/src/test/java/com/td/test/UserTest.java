//package com.td.test;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.td._sys.IUserService;
//import com.td._sys.SysApplication;
//import com.td._sys.TestUser;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = SysApplication.class)
//public class UserTest {
//
//	@Autowired
//	private IUserService us;
//
//	@Test
//	public void testAdd() {
//		TestUser user = new TestUser();
//		user.setName("name");
//		System.out.println(user.getId());
//		System.out.println(us.add1(user));
//		System.out.println(user.getId());
//	}
//
//}
