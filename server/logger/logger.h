#ifndef __LOGGER_H__
#define __LOGGER_H__

#include <stdio.h>
#include <pthread.h>
#include <time.h>
#include <stdlib.h>

#define LOGFILE "location_hub.log"

enum log_type_t
{
    LOG_NEW_CONNECTION,
    LOG_DISCONNECTION,
    LOG_SIGN_UP,
    LOG_GET_LOCATIONS,
    LOG_SEND_LOCATION,
    LOG_SET_PRIVACY
};
typedef enum log_type_t log_type_t;

struct logger_t
{
    FILE            *logfile;
    pthread_mutex_t lock;
};
typedef struct logger_t logger_t;


logger_t *logger_create(void);
void logger_destroy(logger_t *);

void log_print(logger_t *, log_type_t, char *, int);

#endif // __LOGGER_H__