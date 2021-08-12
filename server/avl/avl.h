#ifndef __AVL_H__
#define __AVL_H__

#include <stdbool.h>

#include "../client/client.h"

typedef int (*comparator)(const char *, const char *);

struct node_t
{
    char              *nickname;
    client_location_t *client_location;
    bool              is_private;

    int               height;
    struct node_t     *left;
    struct node_t     *right;
};
typedef struct node_t node_t;


node_t *node_create(void);
void node_destroy(node_t *);
node_t *node_insert(node_t *, node_t *, comparator);
node_t *node_remove(node_t *, char *, comparator);
node_t *node_find(node_t *, comparator, char *);

#endif // __AVL_H__