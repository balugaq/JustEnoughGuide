package com.balugaq.jeg.api.managers;

/**
 * This is the abstract class for all JEG managers.
 *
 * @author balugaq
 * @since 1.1
 */
public abstract class AbstractManager {
    /**
     * Load the manager.
     */
    public void load() {
    }

    /**
     * Unload the manager.
     */
    public void unload() {
    }

    /**
     * Load the manager.
     */
    @Deprecated
    public void onLoad() {
        load();
    }

    /**
     * Unload the manager.
     */
    @Deprecated
    public void onUnload() {
        unload();
    }
}
