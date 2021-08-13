#include "./server.h"

struct thread_args_t
{
    client_addr_t *client_addr;
    server_t      *server;
};
typedef struct thread_args_t thread_args_t;

void *handle_client(void *arg)
{
    printf("New client.\n");
}


server_t *server_create(int port_num)
{
    server_t *server = (server_t *)malloc(sizeof(server_t));

    if (server)
    {
        server->sockfd = 0;
        server->sockaddr.sin_family = AF_INET;
        server->sockaddr.sin_addr.s_addr = htonl(INADDR_ANY);
        server->sockaddr.sin_port = htons(port_num);
        memset(&server->sockaddr.sin_zero, '\0', 8);
        
        server->port_num = port_num;
        server->logger = logger_create();
        server->avl = avl_create();
    }

    return server;
}

void server_destroy(server_t *server)
{
    if (server)
    {
        logger_destroy(server->logger);
        avl_destroy(server->avl);
        
        close(server->sockfd);
        free(server);

        printf("\nServer shutdown.\n");
    }
}

int server_init(server_t *server)
{
    int err = 0;
    int option = 1;

    if ((err = (server->sockfd = socket(AF_INET, SOCK_STREAM, 0))) == -1)
    {
        perror("socket");
        fprintf(stderr, "Failed to create socket.\n");
        return err;
    }

    if ((err = setsockopt(server->sockfd, SOL_SOCKET, SO_REUSEADDR, &option, sizeof(option))) == -1)
    {
        perror("setsockopt");
        fprintf(stderr, "Failed to allow reause of local addresses.\n");
        return err;
    }

    if ((err = bind(server->sockfd, (struct sockaddr *)&server->sockaddr, sizeof(server->sockaddr))) == -1)
    {
        perror("bind");
        fprintf(stderr, "Failed to bind socket to address.\n");
        return err;
    }

    if ((err = listen(server->sockfd, BACKLOG)) == -1)
    {
        perror("listen");
        fprintf(stderr, "Failed to put server to listen mode.\n");
        return err;
    }

    printf("Server running on port %d.\n", server->port_num);

    return 0;
}

int server_accept(server_t *server)
{
    int err;
    int client_sockfd;
    struct sockaddr_in client_sockaddr;
    socklen_t client_socklen = sizeof(struct sockaddr_in);
    pthread_t tid;
    thread_args_t *targs;

    for (;;)
    {
        err = (client_sockfd = accept(server->sockfd, (struct sockaddr *)&client_sockaddr, &client_socklen));
        
        if (err == -1)
        {
            perror("accept");
            fprintf(stderr, "Failed to accept new client.\n");
            return err;
        }

        targs = (thread_args_t *)malloc(sizeof(thread_args_t));
        targs->server = server;
        targs->client_addr = client_addr_create();
        targs->client_addr->sockfd = client_sockfd;
        memcpy(&targs->client_addr->sockaddr, &client_sockaddr, sizeof(struct sockaddr_in));

        if ((errno = pthread_create(&tid, NULL, handle_client, targs)) != 0)
        {
            perror("pthread_create");
            fprintf(stderr, "Failed to create new thread to handle client.\n");
            return errno;
        }

        if ((errno = pthread_detach(tid)) != 0)
        {
            perror("pthread_detach");
            fprintf(stderr, "Failed to set thread to detach state.\n");
            return errno;
        }
    }

    return 0;
}