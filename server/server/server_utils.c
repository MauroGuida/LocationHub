#include "./server_utils.h"

static const char *client_request_strings[] = 
{
    [SIGN_UP]       = "SIGN_UP",
    [GET_LOCATIONS] = "GET_LOCATIONS",
    [SEND_LOCATION] = "SEND_LOCATION"
};

int get_request(char *string)
{
    for (int i = 0; i < (sizeof(client_request_strings) / sizeof(client_request_strings[0])); i++)
    {
        if (strcmp(client_request_strings[i], string) == 0)
        {
            return i;
        }
    }

    return -1;
}

client_request_t extract_request(char *str)
{
    char *ptr;
    char *delimiter = " ";
    char *request;
    client_request_t req;

    if (str)
    {
        if ((ptr = strstr(str, delimiter)) != NULL)
        {
            request = (char *)malloc(ptr - str + 1);
            memcpy(request, str, ptr - str + 1);
            request[ptr - str] = '\0';

            req = (client_request_t) get_request(request);
            free(request);
            return req;
        }
    }

    return -1;
}