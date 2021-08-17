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
    char *ptr = NULL;
    char *delimiter = " ";
    char *request = NULL;
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

char *extract_nickname(char *str)
{
    char *copy_str = NULL; 
    char *ptr = NULL;
    char *nickname = NULL;
    char *delimiter = " ";

    copy_str = (char *)malloc(sizeof(char) * (strlen(str) + 1));
    strcpy(copy_str, str);

    ptr = strtok(copy_str, delimiter);
    ptr = strtok(NULL, " ");

    nickname = (char *)malloc(sizeof(char) * (strlen(ptr) + 1));
    strcpy(nickname, ptr);

    free(copy_str);

    return nickname;
}