package ru.practicum.shareit_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShareItGatewayTests {

	@Test
	void contextLoads() {
	}

	@Test
	void runTest() {
		String[] args = {};
		ShareItGateway.main(args);
	}

}
