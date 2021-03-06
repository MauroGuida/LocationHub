#include "./server_utils.h"

static const char *client_request_strings[] = 
{
    [SIGN_UP]       = "SIGN_UP",
    [GET_LOCATIONS] = "GET_LOCATIONS",
    [SEND_LOCATION] = "SEND_LOCATION",
    [SET_PRIVACY]   = "SET_PRIVACY"
};

double distance(double lat1, double lon1, double lat2, double lon2)
{
    double pi = 3.141592653589793;
    int R = 6371; // Radius of the Earth in KM

    double lat1_rad = lat1 * (pi / 180);
    double lat2_rad = lat2 * (pi / 180);
    double diff_lat = (lat2 - lat1) * (pi / 180);
    double diff_lon = (lon2 - lon1) * (pi / 180);

    double a = sin(diff_lat / 2) * sin(diff_lat / 2) + 
               cos(lat1_rad) * cos(lat2_rad) * 
               sin(diff_lon / 2) * sin(diff_lon / 2);
    double c = 2 * atan2(sqrt(a), sqrt(1 - a));
    double d = R * c;

    return d;
}

int get_request(char *str)
{
    for (int i = 0; i < (sizeof(client_request_strings) / sizeof(client_request_strings[0])); i++)
    {
        if (strcmp(client_request_strings[i], str) == 0)
        {
            return i;
        }
    }

    return -1;
}

client_request_t extract_request(char *str)
{
    char *copy_str = NULL;
    char *delimiter = " ";
    char *token = NULL;
    client_request_t req;

    copy_str = strdup(str);
    token = strtok(copy_str, delimiter);
    req = (client_request_t)get_request(token);
    free(copy_str);

    return req;
}

char *extract_nickname(char *str)
{
    char *copy_str = NULL;
    char *delimiter = " ";
    char *token = NULL;
    char *nickname = NULL;

    copy_str = strdup(str);
    token = strtok(copy_str, delimiter);
    token = strtok(NULL, delimiter);
    
    nickname = strdup(token);
    free(copy_str);
    
    return nickname;
}

client_location_t *extract_client_location(char *str)
{
    char *copy_str = NULL;
    char *delimiter = " ";
    char *token = NULL;
    double latitude, longitude;

    copy_str = strdup(str);
    token = strtok(copy_str, delimiter);

    token = strtok(NULL, delimiter);
    latitude = strtod(token, NULL);

    token = strtok(NULL, delimiter);
    longitude = strtod(token, NULL);

    free(copy_str);

    return client_location_create(latitude, longitude);
}

bool extract_privacy(char *str)
{
    char *copy_str = NULL;
    char *delimiter = " ";
    char *token = NULL;
    bool is_private = false;

    copy_str = strdup(str);
    token = strtok(copy_str, delimiter);
    token = strtok(NULL, delimiter);
    
    is_private = (strcmp(token, "1") == 0);
    
    free(copy_str);

    return is_private;
}

void tree_func_apply(node_t *root, node_t *target, char *buf, void (*func)(node_t *, node_t *, char *))
{
    if (root)
    {
        tree_func_apply(root->left, target, buf, func);
        func(root, target, buf);
        tree_func_apply(root->right, target, buf, func);
    }
}

void add_position(node_t *node, node_t *target, char *buf)
{
    if (node == target) return;

    char *curr_position = NULL;

    if (node && target && buf)
    {
        curr_position = (char *)malloc(sizeof(char) * 1024);
        if (curr_position)
        {
            curr_position[0] = '\0';
            sprintf(curr_position, "%s %f %d [%f;%f]@", node->nickname,
                                                        ((node->client_location && target->client_location) ? distance(target->client_location->latitude,
                                                                                                                       target->client_location->longitude,
                                                                                                                       node->client_location->latitude,
                                                                                                                       node->client_location->longitude) : 0.0),
                                                        (int)node->is_private,
                                                        ((node->client_location) ? node->client_location->latitude : 0.0),
                                                        ((node->client_location) ? node->client_location->longitude : 0.0)); 
            strcat(buf, curr_position);
        }
        free(curr_position);
    }
}

char *avl_serialize(avl_t *avl, char *key)
{
    char *buf = NULL;
    node_t *target = NULL;

    if (avl)
    {
        pthread_mutex_lock(&avl->lock);

        buf = (char *)malloc(sizeof(char) * 8192);
        if (buf)
        {
            buf[0] = '\0';
            strcat(buf, "OK - {");
            
            target = search(avl->root, key, avl->comp);
            if (target)
            {
                tree_func_apply(avl->root, target, buf, add_position);
            }

            strcat(buf, "}\n");
        }
        
        pthread_mutex_unlock(&avl->lock);
    }

    return buf;
}