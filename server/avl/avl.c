#include "./avl.h"

int max(int a, int b)
{
    return (a > b) ? a : b;
}

int height(node_t *node)
{
    return node ? node->height : 0;
}

void recalculate_height(node_t *node)
{
    node->height = 1 + max(height(node->left), height(node->right));
}

node_t *rotate_left(node_t *node)
{
    node_t *child = node->right;
    
    node->right = child->left;
    child->left = node;

    recalculate_height(node);
    recalculate_height(child);

    return child;
}

node_t *rotate_right(node_t *node)
{
    node_t *child = node->left;
    
    node->left = child->right;
    child->right = node;

    recalculate_height(node);
    recalculate_height(child);

    return child;
}

node_t *balance(node_t *node)
{
    recalculate_height(node);

    if (height(node->left) - height(node->right) == 2)
    {
        if (height(node->left->right) > height(node->left->left))
        {
            node->left = rotate_left(node->left);
        }
        return rotate_right(node);
    }
    else if (height(node->right) - height(node->left) == 2)
    {
        if (height(node->right->left) > height(node->right->right))
        {
            node->right = rotate_right(node->right);
        }
        return rotate_left(node);
    }

    return node;
}

node_t *find_min(node_t *node)
{
    while (node->left)
    {
        node = node->left;
    }
    
    return node;
}

node_t *remove_min(node_t *node)
{
    if (!node->left) return node->right;

    node->left = remove_min(node->left);
    return balance(node);
}

node_t *search(node_t *root, char *key, comparator comp)
{
    if (!root) return NULL;

    if (comp(key, root->nickname) == 0)
    {
        return root;
    }
    else if (comp(key, root->nickname) > 0)
    {
        return search(root->right, key, comp);
    }
    else
    {
        return search(root->left, key, comp);
    }
}

void dealloc_tree(node_t *root)
{
    if (root)
    {
        dealloc_tree(root->left);
        dealloc_tree(root->right);
        node_destroy(root);
    }
}

node_t *node_create(char *key)
{
    node_t *node = (node_t *)malloc(sizeof(node_t));

    if (node)
    {
        node->nickname        = strdup(key);
        node->client_location = NULL;
        node->is_private      = true;

        node->height          = 1;
        node->left            = NULL;
        node->right           = NULL;
    }

    return node;
}

void node_destroy(node_t *node)
{
    if (node)
    {
        free(node->nickname);
        client_location_destroy(node->client_location);
        free(node);
    }
}

node_t *node_insert(node_t *root, char *key, comparator comp)
{
    if (!root) return node_create(key);

    if (comp(key, root->nickname) < 0)
    {
        root->left = node_insert(root->left, key, comp);
    }
    else if (comp(key, root->nickname) > 0)
    {
        root->right = node_insert(root->right, key, comp);
    }

    return balance(root);
}

node_t *node_remove(node_t *root, char *key, comparator comp)
{
    if (!root) return NULL;

    if (comp(key, root->nickname) < 0)
    {
        root->left = node_remove(root->left, key, comp);
    }
    else if (comp(key, root->nickname) > 0)
    {
        root->right = node_remove(root->right, key, comp);
    }
    else
    {
        node_t *left = root->left;
        node_t *right = root->right;

        node_destroy(root);

        if (!right) return left;

        node_t *min = find_min(right);
        min->right = remove_min(right);
        min->left = left;

        return balance(min);
    }

    return balance(root);
}

avl_t *avl_create(void)
{
    avl_t *avl = (avl_t *)malloc(sizeof(avl_t));

    if (avl)
    {
        avl->root = NULL;
        avl->comp = strcmp;
        pthread_mutex_init(&avl->lock, NULL);
    }

    return avl;
}

void avl_destroy(avl_t *avl)
{
    if (avl)
    {
        pthread_mutex_lock(&avl->lock);
        dealloc_tree(avl->root);
        pthread_mutex_unlock(&avl->lock);
        
        pthread_mutex_destroy(&avl->lock);
        free(avl);
    }
}

bool avl_insert(avl_t *avl, char *key)
{
    bool result = false;

    if (avl)
    {
        pthread_mutex_lock(&avl->lock);

        node_t *target = search(avl->root, key, avl->comp);
        if (!target)
        {
            avl->root = node_insert(avl->root, key, avl->comp);
            result = true;
        }

        pthread_mutex_unlock(&avl->lock);
    }

    return result;
}

void avl_update_location(avl_t *avl, char *key, client_location_t *client_location)
{
    node_t *target = NULL;

    if (avl)
    {
        pthread_mutex_lock(&avl->lock);

        target = search(avl->root, key, avl->comp);
        if (target)
        {
            client_location_destroy(target->client_location);
            target->client_location = client_location_duplicate(client_location);
        }

        pthread_mutex_unlock(&avl->lock);
    }
}

void avl_update_privacy(avl_t *avl, char *key, bool is_private)
{
    node_t *target = NULL;

    if (avl)
    {
        pthread_mutex_lock(&avl->lock);

        target = search(avl->root, key, avl->comp);
        if (target)
        {
            target->is_private = is_private;
        }

        pthread_mutex_unlock(&avl->lock);
    }
}

void avl_remove(avl_t *avl, char * key)
{
    if (avl && key)
    {
        pthread_mutex_lock(&avl->lock);
        avl->root = node_remove(avl->root, key, avl->comp);
        pthread_mutex_unlock(&avl->lock);
    }
}