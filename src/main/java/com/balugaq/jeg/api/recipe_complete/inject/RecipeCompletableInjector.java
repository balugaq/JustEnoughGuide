package com.balugaq.jeg.api.recipe_complete.inject;

import com.balugaq.jeg.api.recipe_complete.RecipeCompleteSession;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * @author balugaq
 * @since 2.2
 */
@NullMarked
public interface RecipeCompletableInjector {
    SlimefunItem getSlimefunItem();

    /**
     * @return true if call origin recipe completion logic, otherwise false
     */
    boolean inject(RecipeCompleteSession session, @Nullable SlimefunItem toCraft, int scheduleCraftTimes);
}
