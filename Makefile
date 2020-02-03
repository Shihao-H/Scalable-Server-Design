all: compile
	@echo -e '[INFO] Done!'
clean:
	@echo -e '[INFO] Cleaning Up..'	
	@-rm -rf bin/cs455/scaling/**/*.class

compile: 
	@echo -e '[INFO] Compiling the Source..'
	@javac -Xlint -d bin src/cs455/scaling/server/task/*.java src/cs455/scaling/**/*.java
