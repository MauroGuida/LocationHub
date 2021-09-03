#include "./logger.h"

static const char *log_strings[] =
{
    [LOG_NEW_CONNECTION] = "************   NEW CONNECTION   ***********",
    [LOG_DISCONNECTION]  = "************   DISCONNECTED   ***********",
    [LOG_SIGN_UP]        = "Client requested registration",
    [LOG_GET_LOCATIONS]  = "Client requested all locations",
    [LOG_SEND_LOCATION]  = "Client requested to sent his location",
    [LOG_SET_PRIVACY]    = "Client requested to change in privacy settings"
};

logger_t *logger_create(void)
{
    logger_t *logger = (logger_t *)malloc(sizeof(logger_t));
    
    if (logger)
    {
        logger->logfile = NULL;
        pthread_mutex_init(&logger->lock, NULL);    
    }
    
    return logger;
}

void logger_destroy(logger_t *logger)
{
    if (logger)
    {
        pthread_mutex_lock(&logger->lock);
        if(logger->logfile) 
        {
            fclose(logger->logfile);
            logger->logfile = NULL;
        }
        pthread_mutex_unlock(&logger->lock);

        pthread_mutex_destroy(&logger->lock);
        free(logger);
    }
}

void log_print(logger_t *logger, log_type_t log_type, char *ip_addr, int port_num)
{
    time_t rawtime;
    struct tm *timeinfo;
    char log[512] = {'\0'};
    
    if (logger && ip_addr)
    {
        pthread_mutex_lock(&logger->lock);

        if ((logger->logfile = fopen(LOGFILE, "a+")) == NULL)
        {
            pthread_mutex_unlock(&logger->lock);
            return;
        }

        time(&rawtime);
        timeinfo = localtime(&rawtime);

        sprintf(log, "%02d-%02d-%d  %02d:%02d:%02d  %s:%d  %s", timeinfo->tm_mday, 
                                                                timeinfo->tm_mon + 1, 
                                                                timeinfo->tm_year + 1900, 
                                                                timeinfo->tm_hour, 
                                                                timeinfo->tm_min, 
                                                                timeinfo->tm_sec,
                                                                ip_addr, 
                                                                port_num, 
                                                                log_strings[log_type]);

        printf("%s\n", log);                        
        fprintf(logger->logfile, "%s\n", log);      

        fclose(logger->logfile);
        logger->logfile = NULL;

        pthread_mutex_unlock(&logger->lock);
    }
}