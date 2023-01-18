run:
	./gradlew clean bootRun
test:
	./gradlew clean check
docker: build
	docker build . -t star-wars-app:latest