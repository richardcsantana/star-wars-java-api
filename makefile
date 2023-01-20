run:
	./gradlew clean bootRun
test:
	./gradlew clean check
build:
	./gradlew clean build
docker-build:
	docker build . -t star-wars-app:latest
docker-run:
	docker compose up -d --build