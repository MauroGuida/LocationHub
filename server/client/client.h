#ifndef __CLIENT_H__
#define __CLIENT_H__

#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct client_addr_t
{
    int                sockfd;
    struct sockaddr_in sockaddr;
};
typedef struct client_addr_t client_addr_t;

struct client_location_t
{
    double latitude;
    double longitude;
    char   *address_line;
    char   *locality;
    char   *postal_code;
    char   *country_name;
    char   *country_code;
};
typedef struct client_location_t client_location_t;


client_addr_t *client_addr_create(void);
void client_addr_destroy(client_addr_t *);

client_location_t *client_location_create(void);
void client_location_destroy(client_location_t *);
void client_location_set(client_location_t *, int, char *);
client_location_t *client_location_duplicate(client_location_t *);

#endif // __CLIENT_H__