package cache;

import models.User;
import cachemanager.cache.CacheAdapter;
import cachemanager.cache.CacheKey;
import cachemanager.common.Function;

public enum GlobalCacheKey implements CacheKey {

    USER(new CacheAdapter(new Function<String, User>() {

        @Override
        public User apply(String username) {

            return User.findByUsername(username);
        }
    }, "8h")), USER_COUNT(new CacheAdapter(User.class, "count", "5mn"));

    private CacheAdapter adapter;

    private GlobalCacheKey(CacheAdapter adapter) {

        this.adapter = adapter;
    }

    @Override
    public CacheScope scope() {

        return CacheScope.GLOBAL;
    }

    @Override
    public String key() {

        return this.name();
    }

    @Override
    public CacheAdapter adapter() {

        return adapter;
    }
}