package kr.hhplus.be.server;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
class TestcontainersConfiguration {

	public static final MySQLContainer<?> MYSQL_CONTAINER;
	public static final GenericContainer<?> REDIS_CONTAINER;

	private static final int REDIS_PORT = 6379;

	static {
		MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
			.withDatabaseName("hhplus")
			.withUsername("test")
			.withPassword("test");
		MYSQL_CONTAINER.start();

		System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
		System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
		System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());

		REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:latest"))
				.withExposedPorts(REDIS_PORT);
		REDIS_CONTAINER.start();

		String redisHost = REDIS_CONTAINER.getHost();
		int redisPort = REDIS_CONTAINER.getMappedPort(6379);

		System.setProperty("spring.data.redis.host", redisHost);
		System.setProperty("spring.data.redis.port", String.valueOf(redisPort));
		System.setProperty("spring.redis.redisson.config",
				String.format("singleServerConfig:\n" +
						"  address: \"redis://%s:%d\"\n" +
						"  connectionMinimumIdleSize: 1\n" +
						"  connectionPoolSize: 10\n" +
						"  connectTimeout: 10000\n" +
						"  timeout: 3000", redisHost, redisPort));
	}

	@PreDestroy
	public void preDestroy() {
		if (MYSQL_CONTAINER.isRunning()) {
			MYSQL_CONTAINER.stop();
		}
	}
}
