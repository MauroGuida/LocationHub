#include "./avl.h"

int max(int a, int b)
{
    return (a > b) ? a : b;
}

int height(node_t *node)
{
    if (!node) return 0;

    return node->height;
}

int get_balance(node_t *node)
{
    if (!node) return 0;

    return height(node->left) - height(node->right);
}

node_t *left_rotate(node_t *node)
{
    node_t *child = node->right;
    node_t *tmp = child->left;

    child->left = node;
    node->right = tmp;

    node->height = 1 + max(height(node->left), height(node->right));
    child->height = 1 + max(height(child->left), height(child->right));

    return child;
}

node_t *right_rotate(node_t *node)
{
    node_t *child = node->left;
    node_t *tmp = child->right;

    child->right = node;
    node->left = tmp;

    node->height = 1 + max(height(node->left), height(node->right));
    child->height = 1 + max(height(child->left), height(child->right));

    return child;
}

node_t *node_min_value(node_t *node)
{
    if (!node) return node;

    node_t *curr = node;

    while (curr->left)
    {
        curr = curr->left;
    }
    
    return curr;
}

void tree_free(node_t *root)
{
    if (root)
    {
        tree_free(root->left);
        tree_free(root->right);
        node_destroy(root);
    }
}

node_t *node_create(void)
{
    node_t *node = (node_t *)malloc(sizeof(node_t));

    if (node)
    {
        node->nickname        = NULL;
        node->client_location = NULL;
        node->is_private      = false;
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

node_t *node_insert(node_t *root, node_t *new_node, comparator comp)
{
    if (!root)
    {
        return new_node;
    }

    if (comp(new_node->nickname, root->nickname) < 0)
    {
        root->left = node_insert(root->left, new_node, comp);
    }
    else if (comp(new_node->nickname, root->nickname) > 0)
    {
        root->right = node_insert(root->right, new_node, comp);
    }
    else
    {
        return new_node;
    }

    root->height = 1 + max(height(root->left), height(root->right));
    int balance = get_balance(root);        

    // Left left case
    if (balance > 1 && comp(new_node->nickname, root->left->nickname) < 0)
    {
        return right_rotate(root);
    }

    // Right right case
    if (balance < -1 && comp(new_node->nickname, root->right->nickname) > 0)
    {
        return left_rotate(root);
    }

    // Left right case
    if (balance > 1 && comp(new_node->nickname, root->left->nickname) > 0)
    {
        root->left = left_rotate(root->left);
        return right_rotate(root);
    }

    // Right left case
    if (balance < -1 && comp(new_node->nickname, root->right->nickname) < 0)
    {
        root->right = right_rotate(root->right);
        return left_rotate(root);
    }

    return root;
}

node_t *node_remove(node_t *root, char *key, comparator comp)
{
    if (!root)
    {
        return root;
    }

    if (comp(root->nickname, key) > 0)
    {
        root->left = node_remove(root->left, key, comp);
    }
    else if (comp(root->nickname, key) < 0)
    {
        root->right = node_remove(root->right, key, comp);
    }
    else
    {
        if (!root->left || !root->right)
        {
            node_t *tmp = (root->left) ? root->left : root->right;
            if (!tmp)
            {
                tmp = root;
                root = NULL;
            }
            else
            {
                *root = *tmp;
            }
            node_destroy(tmp);
        }
        else
        {
            node_t *tmp = node_min_value(root->right);
            
            free(root->nickname); 
            root->nickname = NULL;

            client_location_destroy(root->client_location); 
            root->client_location = NULL;

            if (tmp->nickname)
            {
                root->nickname = strdup(tmp->nickname);
            }

            if (tmp->client_location)
            {
                root->client_location = client_location_duplicate(tmp->client_location);
            }

            root->is_private = tmp->is_private;
        
            root->right = node_remove(root->right, tmp->nickname, comp);
        }
    }

    if (!root) return root;

    root->height = 1 + max(height(root->left), height(root->right));
    int balance = get_balance(root);

    // Left Left Case
    if (balance > 1 && get_balance(root->left) >= 0)
    {
        return right_rotate(root);
    }
 
    // Left Right Case
    if (balance > 1 && get_balance(root->left) < 0)
    {
        root->left = left_rotate(root->left);
        return right_rotate(root);
    }
 
    // Right Right Case
    if (balance < -1 && get_balance(root->right) <= 0)
    {
        return left_rotate(root);
    }
 
    // Right Left Case
    if (balance < -1 && get_balance(root->right) > 0)
    {
        root->right = right_rotate(root->right);
        return left_rotate(root);
    }
 
    return root;
}

node_t *node_find(node_t *root, comparator comp, char *key)
{
    if (root && key)
    {
        if (comp(key, root->nickname) == 0)
        {
            return root;
        }
        else if (comp(key, root->nickname) > 0)
        {
            return node_find(root->right, comp, key);
        }
        else
        {
            return node_find(root->left, comp, key);
        }
    }

    return NULL;
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
        tree_free(avl->root);
        pthread_mutex_unlock(&avl->lock);
        
        pthread_mutex_destroy(&avl->lock);
        free(avl);
    }
}

bool avl_insert(avl_t *avl, char *nickname)
{
    bool result = false;

    if (avl && nickname)
    {
        pthread_mutex_lock(&avl->lock);

        node_t *target = node_find(avl->root, avl->comp, nickname);
        if (!target)
        {
            node_t *new_node = node_create();

            if (new_node)
            { 
                new_node->nickname = strdup(nickname);
                new_node->client_location = NULL;
                new_node->is_private = true;

                avl->root = node_insert(avl->root, new_node, avl->comp);
                
                result = true;
            }
        }

        pthread_mutex_unlock(&avl->lock);
    }

    return result;
}

void avl_update_location(avl_t *avl, char *nickname, client_location_t *client_location)
{
    node_t *target = NULL;

    if (avl && nickname)
    {
        pthread_mutex_lock(&avl->lock);

        target = node_find(avl->root, avl->comp, nickname);
        if (target)
        {
            client_location_destroy(target->client_location);
            target->client_location = client_location_duplicate(client_location);
        }

        pthread_mutex_unlock(&avl->lock);
    }
}

void avl_update_privacy(avl_t *avl, char *nickname, bool privacy)
{
    node_t *target = NULL;

    if (avl)
    {
        pthread_mutex_lock(&avl->lock);

        target = node_find(avl->root, avl->comp, nickname);
        if (target)
        {
            target->is_private = privacy;
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