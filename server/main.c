#include <stdio.h>
#include <signal.h>
#include <stdlib.h>

#include "./server/server.h"

#define PORT_NUM atoi(argv[1])

server_t *server;

void sig_handler(int sig_num)
{
    server_destroy(server);
    exit(0);
}

int main(int argc, char const *argv[])
{
    int err = 0;

    if (argc != 2)
    {
        fprintf(stderr, "usage: %s <port_num>\n", argv[0]);
        exit(1);
    }

    if (signal(SIGINT, sig_handler) == SIG_ERR || signal(SIGTERM, sig_handler) == SIG_ERR)
    {
        perror("signal");
        fprintf(stderr, "Failed to install termination signal handler.\n");
        exit(errno);
    }

    if ((server = server_create(PORT_NUM)) == NULL)
    {
        fprintf(stderr, "Failed to create server instance.\n");
        exit(errno);
    }

    if ((err = server_init(server)) != 0)
    {
        server_destroy(server);
        exit(err);
    } 

    if ((err = server_accept(server)) != 0)
    {
        server_destroy(server);
        exit(err);
    } 

    return 0;
}
