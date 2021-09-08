#include "./client.h"

client_addr_t *client_addr_create(void)
{
    client_addr_t *client_addr = (client_addr_t *)malloc(sizeof(client_addr_t));
    
    if (client_addr)
    {
        client_addr->sockfd = 0;
        client_addr->sockaddr = (struct sockaddr_in) {0};
    }

    return client_addr;
}

void client_addr_destroy(client_addr_t *client_addr)
{
    if (client_addr)
    {
        free(client_addr);
    }
}

client_location_t *client_location_create(double latitude, double longitude)
{
    client_location_t *client_location = (client_location_t *)malloc(sizeof(client_location_t));

    if (client_location)
    {
        client_location->latitude  = latitude;
        client_location->longitude = longitude;
    }

    return client_location;
}

void client_location_destroy(client_location_t *client_location)
{
    if (client_location)
    {
        free(client_location);
    }
}

client_location_t *client_location_duplicate(client_location_t *client_location)
{
    client_location_t *copy = NULL;

    if (client_location)
    {
        copy = (client_location_t *)malloc(sizeof(client_location_t));

        if (copy)
        {
            copy->latitude  = client_location->latitude;
            copy->longitude = client_location->longitude;
        }
    }

    return copy;
}
