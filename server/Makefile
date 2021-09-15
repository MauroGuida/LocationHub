CC = gcc
CFLAGS = -Wall -O3 -w
MYSERVER = myserver

$(MYSERVER): main.o server.o server_utils.o logger.o client.o avl.o
	$(CC) $(CFLAGS) -pthread $^ -o $(MYSERVER) -lm
	@echo Server compiled successfully.

main.o: main.c
	$(CC) $(CFLAGS) -c $<

server.o: server/server.c server/server.h
	$(CC) $(CFLAGS) -c $<

server_utils.o: server/server_utils.c server/server_utils.h
	$(CC) $(CFLAGS) -c  $< 

logger.o: logger/logger.c logger/logger.h
	$(CC) $(CFLAGS) -c $<

client.o: client/client.c client/client.h
	$(CC) $(CFLAGS) -c $<

avl.o: avl/avl.c avl/avl.h
	$(CC) $(CFLAGS) -c $<

clean:
	@echo Removing obj files:
	rm -f *.o