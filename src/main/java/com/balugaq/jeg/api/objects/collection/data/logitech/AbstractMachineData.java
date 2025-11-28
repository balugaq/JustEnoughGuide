package com.balugaq.jeg.api.objects.collection.data.logitech;

import com.balugaq.jeg.api.groups.CERRecipeGroup;
import com.balugaq.jeg.api.objects.collection.data.MachineData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * @author balugaq
 * @since 1.9
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NullMarked
public class AbstractMachineData extends MachineData {
    private final List<MachineRecipe> machineRecipes;
    private final int energyConsumption;

    @Override
    public List<CERRecipeGroup.RecipeWrapper> wrap() {
        return machineRecipes.stream()
                .map(recipe -> new CERRecipeGroup.RecipeWrapper(
                        recipe.getInput(),
                        recipe.getOutput(),
                        recipe.getTicks(),
                        (long) energyConsumption * recipe.getTicks()
                ))
                .toList();
    }
}
