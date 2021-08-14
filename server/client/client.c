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

client_location_t *client_location_create(void)
{
    client_location_t *client_location = (client_location_t *)malloc(sizeof(client_location_t));

    if (client_location)
    {
        client_location->latitude     = 0.0;
        client_location->longitude    = 0.0;
        client_location->address_line = NULL;
        client_location->locality     = NULL;
        client_location->postal_code  = NULL;
        client_location->country_name = NULL;
        client_location->country_code = NULL;
    }

    return client_location;
}

void client_location_destroy(client_location_t *client_location)
{
    if (client_location)
    {
        free(client_location->address_line);
        free(client_location->locality);
        free(client_location->postal_code);
        free(client_location->country_name);
        free(client_location->country_code);
        free(client_location);
    }
}

void client_location_set(client_location_t *client_location, int index, char *value)
{
    if (client_location)
    {
        switch (index)
        {
        case 0: client_location->latitude     = strtod(value, NULL); return;
        case 1: client_location->longitude    = strtod(value, NULL); return;
        case 2: client_location->address_line = strdup(value);       return;
        case 3: client_location->locality     = strdup(value);       return;
        case 4: client_location->postal_code  = strdup(value);       return;
        case 5: client_location->country_name = strdup(value);       return;
        case 6: client_location->country_code = strdup(value);       return;
        default: return;
        }
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
            copy->latitude     = client_location->latitude;
            copy->longitude    = client_location->longitude;
            copy->address_line = strdup(client_location->address_line);
            copy->locality     = strdup(client_location->locality);
            copy->postal_code  = strdup(client_location->postal_code);
            copy->country_name = strdup(client_location->country_name);
            copy->country_code = strdup(client_location->country_code);
        }
    }

    return copy;
}
