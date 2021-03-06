#ifndef __SERVER_UTILS_H__
#define __SERVER_UTILS_H__

#include <string.h>
#include <stdlib.h>
#include <stdbool.h>
#include <stdio.h>
#include <math.h>

#include "../client/client.h"
#include "../avl/avl.h"

enum client_request_t
{
    SIGN_UP,
    GET_LOCATIONS,
    SEND_LOCATION,
    SET_PRIVACY
};
typedef enum client_request_t client_request_t;


client_request_t extract_request(char *);
char *extract_nickname(char *);
client_location_t *extract_client_location(char *);
bool extract_privacy(char *);
char *avl_serialize(avl_t *, char *);

#endif // __SERVER_UTILS_H__