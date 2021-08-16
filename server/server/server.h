#ifndef __SERVER_H__
#define __SERVER_H__

#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <pthread.h>

#include "../logger/logger.h"
#include "../avl/avl.h"
#include "./server_utils.h"

#define BACKLOG 25

struct server_t
{
    int                sockfd;
    struct sockaddr_in sockaddr;
    int                port_num;
    
    logger_t           *logger;
    avl_t              *avl;
};
typedef struct server_t server_t;


server_t *server_create(int);
void server_destroy(server_t *);
int server_init(server_t *);
int server_accept(server_t *);

#endif // __SERVER_H__