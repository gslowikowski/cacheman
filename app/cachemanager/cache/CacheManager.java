package cachemanager.cache;

import java.io.Serializable;

import play.Logger;
import play.cache.Cache;
import play.mvc.Scope;
import cachemanager.cache.CacheKey.CacheScope;

public final class CacheManager {

    private static final String KEY_SEPERATOR = ":";

    /**
     * Do not instantiate this class. Only use the static methods provided.
     */
    private CacheManager() {

    }

    /**
     * Constructs the key for the given CacheKey. If id is null, constructed key
     * is going to be simply the CacheKey.key(). If an id is provided then the
     * key is going to be like "<key>:<id>". If the cache scope is session then
     * session id is going to be appended to the key so that the key is going to
     * be like "<key>:<id>:<session_id>".
     * 
     * @param cacheKey
     * @param id
     * @return
     */
    private static String constructKey(final CacheKey cacheKey, final Object id) {

        String key = cacheKey.key();
        if (id != null) {
            key += KEY_SEPERATOR + id.toString();
        }
        if (CacheScope.SESSION.equals(cacheKey.scope())) {
            key += KEY_SEPERATOR + Scope.Session.current().getId();
        }
        return key;
    }

    /**
     * Retreives the value associated with the given key from the cache. If
     * there is no such key present in the cache then null is returned.
     * 
     * @param <P>
     *            Type of id
     * @param <R>
     *            Return type
     * @param cacheKey
     * @param id
     * @return
     */
    public static <P extends Object, R extends Serializable> R get(final CacheKey cacheKey, final P id) {

        final String key = constructKey(cacheKey, id);
        R value = (R) Cache.get(key);
        if (value != null) {
            Logger.debug("Cache hit: %s", key);
            return value;
        }
        Logger.debug("Cache miss: %s", key);
        final CacheAdapter adapter = cacheKey.adapter();
        value = adapter.get(id);
        Cache.set(key, value, adapter.expire);
        return value;
    }

    /**
     * Retreives the value associated with the given key from the cache. If
     * there is no such key present in the cache then null is returned. This
     * method is a shortcut for CacheManager.get(cacheKey, null)
     * 
     * @param <R>
     *            Return type
     * @param cacheKey
     * @return
     */
    public static <R extends Serializable> R get(final CacheKey cacheKey) {

        return get(cacheKey, null);
    }

    /**
     * Deletes the value associated with the given key from the cache.
     * 
     * @param cacheKey
     * @param id
     */
    public static void delete(final CacheKey cacheKey, final Object id) {

        final String key = constructKey(cacheKey, id);
        Cache.delete(key);
        Logger.debug("%s removed from cache", key);
    }
}
