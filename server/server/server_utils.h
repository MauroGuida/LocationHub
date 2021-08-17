#ifndef __SERVER_UTILS_H__
#define __SERVER_UTILS_H__

#include <string.h>
#include <stdlib.h>

enum client_request_t
{
    SIGN_UP,
    SEND_LOCATION,
    GET_LOCATIONS
};
typedef enum client_request_t client_request_t;


client_request_t extract_request(char *);
char *extract_nickname(char *);

#endif // __SERVER_UTILS_H__