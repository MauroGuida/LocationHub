#include "./server_utils.h"

static const char *client_request_strings[] = 
{
    [SIGN_UP]       = "SIGN_UP",
    [GET_LOCATIONS] = "GET_LOCATIONS",
    [SEND_LOCATION] = "SEND_LOCATION",
    [SET_PRIVACY]   = "SET_PRIVACY"
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

client_location_t *extract_client_location(char *str)
{
    char *open_delimiter = "[";
    char *close_delimiter = "]";
    char *field_delimiter = ";";
    char *target = NULL;
    char *start = NULL;
    char *end = NULL;
    client_location_t *client_location = NULL;

    if ((start = strstr(str, open_delimiter)) != NULL)
    {
        start += strlen(open_delimiter);
        if ((end = strstr(str, close_delimiter)) != NULL)
        {
            target = (char *)malloc(end - start + 1);
            memcpy(target, start, end - start);
            target[end - start] = '\0';
        }
    }

    client_location = client_location_create();
    for (struct { int i; char *ptr; } s = { 0, strtok(target, field_delimiter) }; s.ptr; s.ptr = strtok(NULL, field_delimiter), ++s.i)
    {
        client_location_set(client_location, s.i, s.ptr);
    }

    free(target);

    return client_location;
}

bool extract_privacy(char *str)
{
    char *copy_str = NULL; 
    char *ptr = NULL;
    char *delimiter = " ";
    bool result = false;

    copy_str = (char *)malloc(sizeof(char) * (strlen(str) + 1));
    strcpy(copy_str, str);

    ptr = strtok(copy_str, delimiter);
    ptr = strtok(NULL, " ");
    ptr = strtok(NULL, " ");

    result = (strcmp(ptr, "1") == 0);
    
    free(copy_str);
    
    return result;
}