package ru.practicum.shareit_server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShareItTests {

	@Test
	void contextLoads() {
	}

	@Test
	void runTest() {
		String[] args = {};
		ShareItServer.main(args);
	}

}
