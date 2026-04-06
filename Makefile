.PHONY: up down run test verify

up:
	docker compose up -d

down:
	docker compose down

run:
	./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

test:
	./mvnw test

verify:
	./mvnw verify
