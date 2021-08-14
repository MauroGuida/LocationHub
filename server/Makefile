CC = gcc
CFLAGS = -Wall -O3
MYSERVER = myserver

$(MYSERVER): main.o server.o logger.o client.o avl.o
	$(CC) $(CFLAGS) -pthread $^ -o $(MYSERVER)
	@echo Server compiled successfully.

main.o: main.c
	$(CC) $(CFLAGS) -c $<

server.o: server/server.c server/server.h
	$(CC) $(CFLAGS) -c $<

logger.o: logger/logger.c logger/logger.h
	$(CC) $(CFLAGS) -c $<

client.o: client/client.c client/client.h
	$(CC) $(CFLAGS) -c $<

avl.o: avl/avl.c avl/avl.h
	$(CC) $(CFLAGS) -c $<

clean:
	@echo Removing obj files:
	rm -f *.o