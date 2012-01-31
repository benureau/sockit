
clean:
	rm -f *.class */*.class */*/*.class */*/*/*.class */*/*/*/*.class *.pyc */*.pyc */*/*.pyc */*/*/*.pyc
	
jar: src/playground/Server.java src/playground/Message.java src/playground/Client.java src/playground/utils/Utils.java
	

FibServer: