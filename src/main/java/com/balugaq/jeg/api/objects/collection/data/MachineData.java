/*
 * Copyright (c) 2024-2025 balugaq
 *
 * This file is part of JustEnoughGuide, available under MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The author's name (balugaq or 大香蕉) and project name (JustEnoughGuide or JEG) shall not be
 *   removed or altered from any source distribution or documentation.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.balugaq.jeg.api.objects.collection.data;

import com.balugaq.jeg.api.cost.CERCalculator;
import com.balugaq.jeg.api.groups.CERRecipeGroup;
import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.api.objects.collection.data.dynatech.AbstractElectricMachineData;
import com.balugaq.jeg.api.objects.collection.data.infinityexpansion.GrowingMachineData;
import com.balugaq.jeg.api.objects.collection.data.infinityexpansion.MaterialGeneratorData;
import com.balugaq.jeg.api.objects.collection.data.infinityexpansion.MobDataCardData;
import com.balugaq.jeg.api.objects.collection.data.infinityexpansion.QuarryData;
import com.balugaq.jeg.api.objects.collection.data.infinityexpansion.ResourceSynthesizerData;
import com.balugaq.jeg.api.objects.collection.data.infinityexpansion.SingularityConstructorData;
import com.balugaq.jeg.api.objects.collection.data.infinityexpansion.StoneworksFactoryData;
import com.balugaq.jeg.api.objects.collection.data.infinityexpansion.StrainerBaseData;
import com.balugaq.jeg.api.objects.collection.data.infinityexpansion.VoidHarvesterData;
import com.balugaq.jeg.api.objects.collection.data.infinitylib.MachineBlockData;
import com.balugaq.jeg.api.objects.collection.data.infinitylib.MachineBlockRecipe;
import com.balugaq.jeg.api.objects.collection.data.logitech.AbstractMachineData;
import com.balugaq.jeg.api.objects.collection.data.rsc.RSCCustomLinkedMachineRecipe;
import com.balugaq.jeg.api.objects.collection.data.rsc.RSCCustomMachineRecipe;
import com.balugaq.jeg.api.objects.collection.data.rsc.RSCCustomMaterialGeneratorData;
import com.balugaq.jeg.api.objects.collection.data.rsc.RSCCustomRecipeMachineData;
import com.balugaq.jeg.api.objects.collection.data.rsc.RSCCustomTemplateMachineData;
import com.balugaq.jeg.api.objects.collection.data.rsc.RSCLinkedOutput;
import com.balugaq.jeg.api.objects.collection.data.rsc.RSCMachineTemplate;
import com.balugaq.jeg.api.objects.collection.data.sc.SCCustomMaterialGeneratorData;
import com.balugaq.jeg.api.objects.collection.data.sf.AContainerData;
import com.balugaq.jeg.api.objects.collection.data.sf.AbstractEnergyProviderData;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.ReflectionUtil;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.AbstractEnergyProvider;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.RandomizedSet;
import it.unimi.dsi.fastutil.ints.IntList;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"DataFlowIssue", "unchecked", "MismatchedQueryAndUpdateOfCollection", "rawtypes", "CommentedOutCode", "IfCanBeSwitch"})
public abstract class MachineData {
    @CallTimeSensitive(CallTimeSensitive.AfterIntegrationsLoaded)
    public static MachineData get(SlimefunItem sf) {
        String className = sf.getClass().getName();
        if (sf instanceof AContainer ac) {
            Debug.debug(ItemStackHelper.getDisplayName(sf.getItem()) + " - " + className);
            if (className.equals("org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomRecipeMachine")) {
                // RykenSlimefunCustomizer - CustomRecipeMachine
                // vals:
                // List<CustomMachineRecipe> recipes
                //
                // CustomMachineRecipe vals:
                // List<Integer> chances;
                // IntList noConsume; // probably not exist, empty list by default
                //
                // boolean chooseOneIfHas;
                // boolean forDisplay;
                // boolean hide;
                //
                List<RSCCustomMachineRecipe> r = new ArrayList<>();
                for (var recipe : ac.getMachineRecipes()) {
                    boolean forDisplay = ReflectionUtil.getValue(recipe, "forDisplay", boolean.class);
                    boolean chooseOneIfHas = ReflectionUtil.getValue(recipe, "chooseOneIfHas", boolean.class);
                    boolean hide = ReflectionUtil.getValue(recipe, "hide", boolean.class);
                    List<Integer> chances = ReflectionUtil.getValue(recipe, "chances", List.class);
                    IntList noConsume = ReflectionUtil.getValue(recipe, "noConsume", IntList.class);

                    r.add(new RSCCustomMachineRecipe(recipe.getTicks() / 2, recipe.getInput(), recipe.getOutput(), chances, chooseOneIfHas, forDisplay, hide, noConsume));
                }
                return new RSCCustomRecipeMachineData(r, ac.getEnergyConsumption(), ac.getSpeed());
            } else if (className.equals("org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomLinkedRecipeMachine")) {
                // RykenSlimefunCustomizer - CustomLinkedRecipeMachine
                // vals:
                // List<CustomLinkedMachineRecipe> recipes;
                //
                // CustomLinkedMachineRecipe vals:
                // Set<Integer> noConsumes;
                // Map<Integer, ItemStack> linkedInput;
                // LinkedOutput linkedOutput;
                //
                // boolean chooseOneIfHas;
                // boolean forDisplay;
                // boolean hide;
                //
                // record LinkedOutput(
                //     ItemStack[] freeOutput,
                //     Map<Integer, ItemStack> linkedOutput,
                //     int[] freeChances,
                //     Map<Integer, Integer> linkedChances
                // )
                //
                List<RSCCustomLinkedMachineRecipe> r = new ArrayList<>();
                for (var recipe : ac.getMachineRecipes()) {
                    boolean forDisplay = ReflectionUtil.getValue(recipe, "forDisplay", boolean.class);
                    boolean chooseOneIfHas = ReflectionUtil.getValue(recipe, "chooseOneIfHas", boolean.class);
                    boolean hide = ReflectionUtil.getValue(recipe, "hide", boolean.class);
                    Set<Integer> noConsumes = ReflectionUtil.getValue(recipe, "noConsumes", Set.class);
                    Map<Integer, ItemStack> linkedInput = ReflectionUtil.getValue(recipe, "linkedInput", Map.class);
                    Object linkedOutputr = ReflectionUtil.getValue(recipe, "linkedOutput", Object.class);
                    RSCLinkedOutput linkedOutput = new RSCLinkedOutput(
                            (ItemStack[]) ReflectionUtil.invokeMethod(linkedOutputr, "freeOutput"),
                            (Map<Integer, ItemStack>) ReflectionUtil.invokeMethod(linkedOutputr, "linkedOutput"),
                            (int[]) ReflectionUtil.invokeMethod(linkedOutputr, "freeChances"),
                            (Map<Integer, Integer>) ReflectionUtil.invokeMethod(linkedOutputr, "linkedChances")
                    );
                    r.add(new RSCCustomLinkedMachineRecipe(recipe.getTicks(), noConsumes, linkedInput, linkedOutput, forDisplay, chooseOneIfHas, hide));
                }
            } else {
                return new AContainerData(ac.getMachineRecipes(), ac.getEnergyConsumption(), ac.getSpeed());
            }
        }

        // RykenSlimefunCustomizer - CustomTemplateMachine
        // vals:
        // boolean fasterIfMoreTemplates
        // boolean moreOutputIfMoreTemplates
        // int consumption
        // List<MachineTemplate> templates
        //
        // record MachineTemplate(ItemStack template, List<CustomMachineRecipe> recipes)
        //
        else if (className.equals("org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomTemplateMachine")) {
            boolean fasterIfMoreTemplates = ReflectionUtil.getValue(sf, "fasterIfMoreTemplates", boolean.class);
            boolean moreOutputIfMoreTemplates = ReflectionUtil.getValue(sf, "moreOutputIfMoreTemplates", boolean.class);
            int consumption = ReflectionUtil.getValue(sf, "consumption", int.class);
            List/*<MachineTemplate>*/ templates = ReflectionUtil.getValue(sf, "templates", List.class);
            List<RSCMachineTemplate> ts = new ArrayList<>();
            for (var template /* record MachineTemplate */: templates) {
                ItemStack t = (ItemStack) ReflectionUtil.invokeMethod(template, "template");
                List<RSCCustomMachineRecipe> r = new ArrayList<>();
                List/*<CustomMachineRecipe>*/<MachineRecipe> recipes = (List<MachineRecipe>) ReflectionUtil.invokeMethod(sf, "recipes");
                for (var recipe : recipes) {
                    boolean forDisplay = ReflectionUtil.getValue(recipe, "forDisplay", boolean.class);
                    boolean chooseOneIfHas = ReflectionUtil.getValue(recipe, "chooseOneIfHas", boolean.class);
                    boolean hide = ReflectionUtil.getValue(recipe, "hide", boolean.class);
                    List<Integer> chances = ReflectionUtil.getValue(recipe, "chances", List.class);
                    IntList noConsume = ReflectionUtil.getValue(recipe, "noConsume", IntList.class);
                    r.add(new RSCCustomMachineRecipe(recipe.getTicks() / 2, recipe.getInput(), recipe.getOutput(), chances, chooseOneIfHas, forDisplay, hide, noConsume));
                }

                ts.add(new RSCMachineTemplate(t, r));
            }

            return new RSCCustomTemplateMachineData(fasterIfMoreTemplates, moreOutputIfMoreTemplates, consumption, ts);
        }

        if (sf instanceof AbstractEnergyProvider aep) {
            return new AbstractEnergyProviderData(aep.getFuelTypes(), aep.getEnergyProduction());
        }

        if (className.equals("org.lins.mmmjjkx.rykenslimefuncustomizer.objects.customs.machine.CustomMaterialGenerator")) {
            int tickRate = ReflectionUtil.getValue(sf, "tickRate", int.class);
            int per = ReflectionUtil.getValue(sf, "per", int.class);
            List/*<ItemStack>*/<ItemStack> generation = ReflectionUtil.getValue(sf, "generation", List.class);
            List/*<Integer>*/<Integer> chances = ReflectionUtil.getValue(sf, "chances", List.class);
            boolean chooseOne = ReflectionUtil.getValue(sf, "chooseOne", boolean.class);

            return new RSCCustomMaterialGeneratorData(tickRate, per, generation, chances, chooseOne);
        }

        if (className.equals("io.ncbpfluffybear.slimecustomizer.objects.CustomMaterialGenerator")) {
            int tickRate = ReflectionUtil.getValue(sf, "tickRate", int.class);
            ItemStack output = ReflectionUtil.getValue(sf, "output", ItemStack.class);
            return new SCCustomMaterialGeneratorData(tickRate, output);
        }

        if (className.equals("io.github.mooy1.infinityexpansion.items.blocks.StrainerBase")) {
            return new StrainerBaseData(ReflectionUtil.getStaticValue(sf.getClass(), "POTATO", ItemStack.class), ReflectionUtil.getStaticValue(sf.getClass(), "OUTPUTS", ItemStack[].class));
        }

        if (className.equals("io.github.mooy1.infinityexpansion.items.quarries.Quarry")) {
            int INTERVAL = ReflectionUtil.getStaticValue(sf.getClass(), "INTERVAL", int.class);
            Material[] outputs = ReflectionUtil.getValue(sf, "OUTPUTS", Material[].class);
            int speed = ReflectionUtil.getValue(sf, "speed", int.class);
            int energyPerTick = ReflectionUtil.getValue(sf, "energyPerTick", int.class);
            return new QuarryData(INTERVAL, outputs, speed, energyPerTick);
        }

        if (className.equals("io.github.mooy1.infinityexpansion.items.machines.SingularityConstructor")) {
            List/*<Recipe>*/ RECIPE_LIST = ReflectionUtil.getStaticValue(sf.getClass(), "RECIPE_LIST", List.class);
            int energyPerTick = ReflectionUtil.getValue(sf, "energyPerTick", int.class);
            int speed = sf.getId().equals("SINGULARITY_CONSTRUCTOR") ? 1 : sf.getId().equals("INFINITY_CONSTRUCTOR") ? 64 : 1;
            List<SingularityConstructorData.Recipe> recipes = new ArrayList<>();
            for (var recipe : RECIPE_LIST) {
                SlimefunItemStack output = ReflectionUtil.getValue(recipe, "output", SlimefunItemStack.class);
                ItemStack input = ReflectionUtil.getValue(recipe, "input", ItemStack.class);
                int amount = ReflectionUtil.getValue(recipe, "amount", int.class);
                String id = ReflectionUtil.getValue(recipe, "id", String.class);
                recipes.add(new SingularityConstructorData.Recipe(output, input, id, amount));
            }
            return new SingularityConstructorData(recipes, energyPerTick, speed);
        }

        if (className.equals("io.github.mooy1.infinityexpansion.items.machines.MaterialGenerator")) {
            Material material = ReflectionUtil.getValue(sf, "material", Material.class);
            int speed = ReflectionUtil.getValue(sf, "speed", int.class);
            int energyPerTick = ReflectionUtil.getValue(sf, "energyPerTick", int.class);
            return new MaterialGeneratorData(material, speed, energyPerTick);
        }

        if (className.equals("io.github.mooy1.infinityexpansion.items.machines.ResourceSynthesizer")) {
            SlimefunItemStack[] recipes = ReflectionUtil.getValue(sf, "recipes", SlimefunItemStack[].class);
            int energyPerTick = ReflectionUtil.getValue(sf, "energyPerTick", int.class);
            List<ResourceSynthesizerData.Recipe> rs = new ArrayList<>();
            for (int i = 0; i < recipes.length; i += 3) {
                SlimefunItemStack i1 = recipes[i];
                SlimefunItemStack i2 = recipes[i + 1];
                SlimefunItemStack output = recipes[i + 2];
                rs.add(new ResourceSynthesizerData.Recipe(i1, i2, output));
            }

            return new ResourceSynthesizerData(rs, energyPerTick);
        }

        if (className.equals("io.github.mooy1.infinityexpansion.items.machines.GrowingMachine")) {
            EnumMap/*<Material, ItemStack[]>*/<Material, ItemStack[]> recipes = ReflectionUtil.getValue(sf, "recipes", EnumMap.class);
            int ticksPerOutput = ReflectionUtil.getValue(sf, "ticksPerOutput", int.class);
            int energyPerTick = ReflectionUtil.getValue(sf, "energyPerTick", int.class);

            return new GrowingMachineData(recipes, energyPerTick, ticksPerOutput);
        }

        if (CERCalculator.isInstanceSimple(sf, "MachineBlock")) {
            List/*MachineBlockRecipe*/ recipes = ReflectionUtil.getValue(sf, "recipes", List.class);
            int ticksPerOutput = ReflectionUtil.getValue(sf, "ticksPerOutput", int.class);
            int energyPerTick = ReflectionUtil.getValue(sf, "energyPerTick", int.class);

            if (recipes != null) {
                List<MachineBlockRecipe> rs = new ArrayList<>();
                for (var recipe : recipes) {
                    String[] strings = ReflectionUtil.getValue(recipe, "strings", String[].class);
                    int[] amounts = ReflectionUtil.getValue(recipe, "amounts", int[].class);
                    ItemStack output = ReflectionUtil.getValue(recipe, "output", ItemStack.class);
                    rs.add(new MachineBlockRecipe(CERCalculator.translateIntoItemStackArray(strings, amounts), output));
                }

                return new MachineBlockData(rs, ticksPerOutput, energyPerTick);
            } else {
                return null;
            }
        }

        if (CERCalculator.isInstanceSimple(sf, "AbstractElectricMachine") && isAddon(sf, "DynaTech")) {
            List/*<MachineRecipe>*/<MachineRecipe> recipes = ReflectionUtil.getValue(sf, "recipes", List.class);
            int energyConsumedPerTick = ReflectionUtil.getValue(sf, "energyConsumedPerTick", int.class);
            int processingSpeed = ReflectionUtil.getValue(sf, "processingSpeed", int.class);
            return new AbstractElectricMachineData(recipes, energyConsumedPerTick, processingSpeed);
        }

        if (className.equals("io.github.mooy1.infinityexpansion.items.machines.StoneworksFactory")) {
            int energyPerTick = ReflectionUtil.getValue(sf, "energyPerTick", int.class);
            Set<Material> materials = new HashSet<>();
            materials.add(Material.COBBLESTONE);
            materials.add(Material.STONE);
            materials.add(Material.SAND);
            materials.add(Material.STONE_BRICKS);
            materials.add(Material.SMOOTH_STONE);
            materials.add(Material.GLASS);
            materials.add(Material.CRACKED_STONE_BRICKS);
            materials.add(Material.GRAVEL);
            materials.add(Material.GRANITE);
            materials.add(Material.DIORITE);
            materials.add(Material.ANDESITE);
            materials.add(Material.POLISHED_GRANITE);
            materials.add(Material.POLISHED_DIORITE);
            materials.add(Material.POLISHED_ANDESITE);
            materials.add(Material.SANDSTONE);

            return new StoneworksFactoryData(materials, energyPerTick);
        }

        if (className.equals("io.github.mooy1.infinityexpansion.items.machines.VoidHarvester")) {
            int TIME = ReflectionUtil.getStaticValue(sf.getClass(), "TIME", int.class);
            int speed = ReflectionUtil.getValue(sf, "speed", int.class);
            int energyPerTick = ReflectionUtil.getValue(sf, "energyPerTick", int.class);
            ItemStack output;
            if (sf.getAddon().getName().equals("InfinityExpansion-Changed")) {
                output = SlimefunItem.getById("VOID_DUST").getItem();
            } else {
                output = SlimefunItem.getById("VOID_BIT").getItem();
            }

            return new VoidHarvesterData(TIME, speed, energyPerTick, output);
        }

        if (className.equals("io.github.mooy1.infinityexpansion.items.mobdata.MobDataCard")) {
            RandomizedSet<ItemStack> drops = ReflectionUtil.getValue(sf, "drops", RandomizedSet.class);
            SlimefunItem chamber = SlimefunItem.getById("MOB_SIMULATION_CHAMBER");
            int energy = ReflectionUtil.getValue(chamber, "energy", int.class);
            int interval = ReflectionUtil.getValue(chamber, "interval", int.class);
            return new MobDataCardData(chamber, energy, interval, drops.toMap());
        }

        if (sf.getAddon().getName().equals("Logitech") && CERCalculator.isInstanceSimple(sf, "AbstractMachine")) {
            List<MachineRecipe> machineRecipes = ReflectionUtil.getValue(sf, "machineRecipes", List.class);
            Supplier<List<MachineRecipe>> machineRecipeSupplier = ReflectionUtil.getValue(sf, "machineRecipeSupplier", Supplier.class);
            List<MachineRecipe> clone = new ArrayList<>();
            if (machineRecipes != null) {
                clone.addAll(machineRecipes);
            }
            if (machineRecipeSupplier != null) {
                List<MachineRecipe> get = machineRecipeSupplier.get();
                if (get != null) {
                    clone.addAll(get);
                }
            }
            int energyConsumption = ReflectionUtil.getValue(sf, "energyConsumption", int.class);
            return new AbstractMachineData(clone.stream().distinct().toList(), energyConsumption);
        }

        return null;
    }

    public static boolean isAddon(@NotNull SlimefunItem sf, @NotNull String addonName) {
        return sf.getAddon().getName().equals(addonName);
    }

    public abstract @NotNull List<CERRecipeGroup.RecipeWrapper> wrap();
}
