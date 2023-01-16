run:
	./gradlew clean bootRun
check:
	./gradlew clean check
docker: build
	docker build . -t star-wars-app:latest