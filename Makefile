run: App
	java -ea App

App: App.java Panel.java Tree.java
	javac $^

Tree: Tree.java
	javac $^

.PHONY: clean
clean:
	rm ./*.class


