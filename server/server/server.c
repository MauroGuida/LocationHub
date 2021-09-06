#include "./server.h"

#define BUF_SIZE 512

struct thread_args_t
{
    client_addr_t *client_addr;
    server_t      *server;
};
typedef struct thread_args_t thread_args_t;

thread_args_t *thread_args_create(server_t *server, int client_sockfd, struct sockaddr_in *client_sockaddr)
{
    thread_args_t *targs = (thread_args_t *)malloc(sizeof(thread_args_t));

    if (targs)
    {
        targs->server = server;
        targs->client_addr = client_addr_create();
        targs->client_addr->sockfd = client_sockfd;
        memcpy(&targs->client_addr->sockaddr, client_sockaddr, sizeof(struct sockaddr_in));
    }

    return targs;
}

void thread_args_destroy(thread_args_t *targs)
{
    if (targs)
    {
        targs->server = NULL;
        client_addr_destroy(targs->client_addr);
        free(targs);
    }
}

void sign_up(server_t *server, char **nickname, char *str, int client_sockfd)
{
    *nickname = extract_nickname(str);
    bool success = avl_insert(server->avl, *nickname);
    if (success)
    {
        char msg[] = "OK\n";
        write(client_sockfd, msg, strlen(msg));
    }
    else
    {
        free(*nickname);
        *nickname = NULL;
        char msg[] = "ERR\n";
        write(client_sockfd, msg, strlen(msg));
    }
}

void send_locations_to_client(server_t *server, char *nickname, int client_sockfd)
{
    char *buf = avl_serialize(server->avl, nickname);
    if (buf)
    {
        write(client_sockfd, buf, strlen(buf));
    }
    else
    {
        char msg[] = "ERR\n";
        write(client_sockfd, msg, strlen(msg));
    }
    free(buf);
}

void set_client_location(server_t *server, char *nickname, char *str, int client_sockfd)
{
    client_location_t *client_location = extract_client_location(str);
    avl_update_location(server->avl, nickname, client_location);
    client_location_destroy(client_location);
    
    char msg[] = "OK\n";
    write(client_sockfd, msg, strlen(msg));
}

void set_client_privacy(server_t *server, char *nickname, char *str, int client_sockfd)
{
    bool privacy = extract_privacy(str);
    avl_update_privacy(server->avl, nickname, privacy);
    
    char msg[] = "OK\n";
    write(client_sockfd, msg, strlen(msg));
}

void *handle_client(void *arg)
{
    thread_args_t *targs = NULL;
    server_t *server = NULL;
    char *client_ip_addr = NULL;
    int client_port_num = 0;
    int client_sockfd = 0;
    char buf[BUF_SIZE] = {'\0'};
    int n = 0;
    client_request_t req;
    char *nickname = NULL;
    struct timeval timeout;
    fd_set read_fds;

    targs = (thread_args_t *)arg;
    server = targs->server;
    client_ip_addr = inet_ntoa(targs->client_addr->sockaddr.sin_addr);
    client_port_num = ntohs(targs->client_addr->sockaddr.sin_port);
    client_sockfd = targs->client_addr->sockfd;


    log_print(server->logger, LOG_NEW_CONNECTION, client_ip_addr, client_port_num);

    for (;;)
    {
        timeout.tv_sec = 15;
        timeout.tv_usec = 0;
        
        FD_ZERO(&read_fds);
        FD_SET(client_sockfd, &read_fds);

        // n = read(client_sockfd, buf, BUF_SIZE);
        if (select(client_sockfd + 1, &read_fds, NULL, NULL, &timeout) > 0)
        {
            n = read(client_sockfd, buf, BUF_SIZE);
            if (n != -1)
            {
                buf[n] = '\0';
                
                req = extract_request(buf);

                switch (req)
                {
                case SIGN_UP:
                    log_print(server->logger, LOG_SIGN_UP, client_ip_addr, client_port_num);
                    sign_up(server, &nickname, buf, client_sockfd);
                    break;
                
                case GET_LOCATIONS:
                    log_print(server->logger, LOG_GET_LOCATIONS, client_ip_addr, client_port_num);
                    send_locations_to_client(server, nickname, client_sockfd);
                    break;

                case SEND_LOCATION:
                    log_print(server->logger, LOG_SEND_LOCATION, client_ip_addr, client_port_num);
                    set_client_location(server, nickname, buf, client_sockfd);
                    break;
                
                case SET_PRIVACY:
                    log_print(server->logger, LOG_SET_PRIVACY, client_ip_addr, client_port_num);
                    set_client_privacy(server, nickname, buf, client_sockfd);
                    break;

                default:
                    break;
                }
            }
        }
        else 
        {
            log_print(server->logger, LOG_DISCONNECTION, client_ip_addr, client_port_num);
            avl_remove(server->avl, nickname);
            free(nickname);
            break;
        }
    }

    thread_args_destroy(targs);
    pthread_exit((char *)0);
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
    int option = 1;
    int maxpkt = 10;

    for (;;)
    {
        if ((err = (client_sockfd = accept(server->sockfd, (struct sockaddr *)&client_sockaddr, &client_socklen))) == -1)
        {
            perror("accept");
            fprintf(stderr, "Failed to accept new client.\n");
            return err;
        }

        targs = thread_args_create(server, client_sockfd, &client_sockaddr);

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