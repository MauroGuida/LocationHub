#ifndef __AVL_H__
#define __AVL_H__

#include <stdbool.h>
#include <pthread.h>
#include <string.h>

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

struct avl_t
{
    node_t          *root;
    comparator      comp;
    pthread_mutex_t lock;
};
typedef struct avl_t avl_t;


node_t *node_create(void);
void node_destroy(node_t *);
node_t *node_insert(node_t *, node_t *, comparator);
node_t *node_remove(node_t *, char *, comparator);
node_t *node_find(node_t *, comparator, char *);

avl_t *avl_create(void);
void avl_destroy(avl_t *);
void avl_insert(avl_t *, char *, client_location_t *, bool);
void avl_update(avl_t *, char *, client_location_t *, bool);
void avl_remove(avl_t *, char *);

#endif // __AVL_H__