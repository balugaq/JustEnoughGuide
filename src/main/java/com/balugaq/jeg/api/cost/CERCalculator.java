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

package com.balugaq.jeg.api.cost;

import com.balugaq.jeg.api.objects.annotations.CallTimeSensitive;
import com.balugaq.jeg.api.objects.collection.data.MachineData;
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
import com.balugaq.jeg.api.objects.collection.data.rsc.RSCCustomLinkedRecipeMachineData;
import com.balugaq.jeg.api.objects.collection.data.rsc.RSCCustomMachineRecipe;
import com.balugaq.jeg.api.objects.collection.data.rsc.RSCCustomMaterialGeneratorData;
import com.balugaq.jeg.api.objects.collection.data.rsc.RSCCustomRecipeMachineData;
import com.balugaq.jeg.api.objects.collection.data.rsc.RSCCustomTemplateMachineData;
import com.balugaq.jeg.api.objects.collection.data.rsc.RSCMachineTemplate;
import com.balugaq.jeg.api.objects.collection.data.sc.SCCustomMaterialGeneratorData;
import com.balugaq.jeg.api.objects.collection.data.sf.AContainerData;
import com.balugaq.jeg.api.objects.collection.data.sf.AbstractEnergyProviderData;
import com.balugaq.jeg.core.managers.IntegrationManager;
import com.balugaq.jeg.implementation.JustEnoughGuide;
import com.balugaq.jeg.utils.Debug;
import com.balugaq.jeg.utils.compatibility.Converter;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.SneakyThrows;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author balugaq
 * @since 1.9
 */
@SuppressWarnings({"unused", "deprecation", "ConstantValue"})
public class CERCalculator {
    public static final Map<SlimefunItem, MachineData> machines = new HashMap<>();
    public static final Pattern trimp = Pattern.compile("[&§]k[&§]");

    public static void load() {
        IntegrationManager.scheduleRun(CERCalculator::loadInternal);
        ValueTable.load();
    }

    @CallTimeSensitive(CallTimeSensitive.AfterIntegrationsLoaded)
    @ApiStatus.Internal
    private static void loadInternal() {
        for (SlimefunItem sf : Slimefun.getRegistry().getEnabledSlimefunItems()) {
            String className = sf.getClass().getName();
            MachineData wrapper = MachineData.get(sf);
            if (wrapper != null) {
                machines.put(sf, wrapper);
            }
        }

        if (JustEnoughGuide.getConfigManager().isDebug()) {
            Debug.debug("[CER]");
            Debug.debug("[CER] size: " + machines.size());
            for (var entry : machines.entrySet()) {
                Debug.debug("[CER]" + entry.getKey());
            }
        }
    }

    public static boolean cerable(SlimefunItem sf) {
        return machines.containsKey(sf);
    }

    public static double getCER(@NotNull SlimefunItem sf, @NotNull String searchTerm) {
        if (sf == null || sf.isDisabled() || searchTerm == null || !machines.containsKey(sf)) {
            return -1.0D;
        }

        return calc0(sf, item -> similar(searchTerm, item));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @SneakyThrows
    @ParametersAreNonnullByDefault
    private static double calc0(SlimefunItem sf, Predicate<ItemStack> predicate) {
        String className = sf.getClass().getName();
        double cost = ValueTable.getValue(sf);
        MachineData data = machines.get(sf);

        if (data instanceof AContainerData acd) {
            for (var recipe : acd.getRecipes()) {
                for (var item : recipe.getOutput()) {
                    if (predicate.test(item)) {
                        return calc(cost + ValueTable.getValue(recipe.getInput()), item.getAmount(), recipe.getTicks(), (double) acd.getEnergyConsumption() / acd.getSpeed() * recipe.getTicks());
                    }
                }
            }
        }

        if (data instanceof RSCCustomRecipeMachineData rcrmd) {
            for (var recipe : rcrmd.getRecipes()) {
                if (recipe.isForDisplay()) continue;

                int j = 0;
                for (var item : recipe.getOutput()) {
                    if (predicate.test(item)) {
                        boolean forDisplay = recipe.isForDisplay();
                        if (forDisplay) {
                            continue;
                        }

                        boolean chooseOneIfHas = recipe.isChooseOneIfHas();
                        List<Integer> chances = recipe.getChances();
                        IntList noConsume = recipe.getNoConsume();
                        double v = 0.0D;
                        var ii = recipe.getInput();
                        for (int i = 0; i < ii.length; i++) {
                            var iii = ii[i];
                            if (noConsume != null && i < noConsume.size()) {
                                v += ValueTable.getTemplateValue(iii);
                            } else {
                                v += ValueTable.getValue(iii);
                            }
                        }

                        double v2 = j >= chances.size() ? item.getAmount() : item.getAmount() * chances.get(j);
                        if (chooseOneIfHas) {
                            v2 /= recipe.getOutput().length;
                        }

                        return calc(cost + v, v2, recipe.getTicks(), (double) rcrmd.getEnergyConsumption() / rcrmd.getSpeed() * recipe.getTicks());
                    }
                    j++;
                }
            }
        }

        if (data instanceof RSCCustomLinkedRecipeMachineData rclrmd) {
            for (var recipe : rclrmd.getRecipes()) {
                if (recipe.isForDisplay()) continue;

                double iv = 0.0D;
                var li = recipe.getLinkedInput();
                for (var e : li.entrySet()) {
                    if (recipe.getNoConsumes().contains(e.getKey())) iv += ValueTable.getTemplateValue(e.getValue());
                    else iv += ValueTable.getValue(e.getValue());
                }

                var lo = recipe.getLinkedOutput();

                for (var entry : lo.linkedOutput().entrySet()) {
                    int slot = entry.getKey();
                    ItemStack output = entry.getValue();
                    if (predicate.test(output)) {
                        Integer chance = lo.linkedChances().get(slot);
                        double amt = chance == null ? output.getAmount() : output.getAmount() * chance / 100.0D;
                        if (recipe.isChooseOneIfHas()) amt /= lo.linkedOutput().size();
                        return calc(cost + iv, amt, (double) recipe.getTicks() / rclrmd.getSpeed(), (double) (recipe.getTicks() * rclrmd.getEnergyConsumption()) / rclrmd.getSpeed());
                    }
                }

                ItemStack[] fo = lo.freeOutput();
                for (int slot = 0; slot < fo.length; slot++) {
                    var output = fo[slot];
                    if (predicate.test(output)) {
                        Integer chance = lo.freeChances()[slot];
                        double amt = chance == null ? output.getAmount() : output.getAmount() * chance / 100.0D;
                        if (recipe.isChooseOneIfHas()) amt /= lo.freeChances().length;
                        return calc(cost + iv, amt, (double) recipe.getTicks() / rclrmd.getSpeed(), (double) (recipe.getTicks() * rclrmd.getEnergyConsumption()) / rclrmd.getSpeed());
                    }
                }
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
        else if (data instanceof RSCCustomTemplateMachineData rctmd) {
            boolean fasterIfMoreTemplates = rctmd.isFasterIfMoreTemplates();
            boolean moreOutputIfMoreTemplates = rctmd.isMoreOutputIfMoreTemplates();
            int consumption = rctmd.getConsumption();
            List<RSCMachineTemplate> templates = rctmd.getTemplates();
            for (var template /* record MachineTemplate */: templates) {
                ItemStack t = template.template();
                int mavg = t.getMaxStackSize() * 2;
                List<RSCCustomMachineRecipe> recipes = template.recipes();
                for (var recipe : recipes) {
                    if (recipe.isForDisplay()) continue;

                    int j = 0;
                    for (var item : recipe.getOutput()) {
                        if (predicate.test(item)) {
                            boolean forDisplay = recipe.isForDisplay();
                            if (forDisplay) {
                                continue;
                            }

                            boolean chooseOneIfHas = recipe.isChooseOneIfHas();
                            List<Integer> chances = recipe.getChances();
                            IntList noConsume = recipe.getNoConsume();
                            double v = 0.0D;
                            var ii = recipe.getInput();
                            for (int i = 0; i < ii.length; i++) {
                                var iii = ii[i];
                                if (noConsume != null && i < noConsume.size()) {
                                    v += ValueTable.getTemplateValue(iii);
                                } else {
                                    v += ValueTable.getValue(iii);
                                }
                            }

                            double v2 = j >= chances.size() ? item.getAmount() : item.getAmount() * chances.get(j);
                            if (chooseOneIfHas) {
                                v2 /= recipe.getOutput().length;
                            }

                            if (moreOutputIfMoreTemplates) {
                                v2 *= mavg;
                            }

                            double btv = ValueTable.getTemplateValue(t);
                            return calc(cost + (fasterIfMoreTemplates ? btv / mavg : btv) + v, v2, fasterIfMoreTemplates ? (double) recipe.getTicks() / mavg : recipe.getTicks(), (double) consumption * recipe.getTicks());
                        }
                        j++;
                    }
                }
            }
        }

        // Slimefun - AGenerator
        // SlimeCustomizer - SCAGenerator
        // RykenSlimefunCustomizer - CustomGenerator
        else if (data instanceof AbstractEnergyProviderData aep) {
            for (var ft : aep.getFuelTypes()) {
                if (predicate.test(ft.getOutput())) {
                    return calc(cost + ValueTable.getValue(ft.getInput()), ft.getOutput().getAmount(), ft.getTicks(), -aep.getEnergyProduction() * ft.getTicks());
                }
            }
        }

        // RykenSlimefunCustomizer - CustomMaterialGenerator
        // vals:
        // int tickRate
        // int per
        // List<ItemStack> generation
        // List<Integer> chances
        // boolean chooseOne
        //
        else if (data instanceof RSCCustomMaterialGeneratorData rcmgd) {
            int tickRate = rcmgd.getTickRate();
            int per = rcmgd.getPer();
            List/*<ItemStack>*/<ItemStack> generation = rcmgd.getGeneration();
            List/*<Integer>*/<Integer> chances = rcmgd.getChances();
            boolean chooseOne = rcmgd.isChooseOne();
            for (int a = 0; a < generation.size(); a++) {
                var i = generation.get(a);
                if (predicate.test(i)) {
                    if (chances != null && !chances.isEmpty()) {
                        double bv = i.getAmount() * chances.get(a);
                        return calc(cost, chooseOne ? bv / chances.size() : bv, tickRate, per * tickRate);
                    } else {
                        return calc(cost, i.getAmount(), tickRate, per * tickRate);
                    }
                }
            }
        }

        // SlimeCustomizer - CustomMaterialGenerator
        // vals:
        // int tickRate
        // ItemStack output
        //
        else if (data instanceof SCCustomMaterialGeneratorData scmgd) {
            int tickRate = scmgd.getTickRate();
            ItemStack output = scmgd.getOutput();
            if (predicate.test(output)) {
                return calc(cost, output.getAmount(), tickRate, 0);
            }
        }

        // InfinityExpansion - StrainerBase
        // vals:
        // static ItemStack POTATO
        // static ItemStack[] OUTPUTS
        //
        else if (data instanceof StrainerBaseData sbd) {
            ItemStack POTATO = sbd.getPOTATO();
            if (predicate.test(POTATO)) {
                return calc(cost, POTATO.getAmount(), 40, 0);
            }

            ItemStack[] OUTPUTS = sbd.getOUTPUTS();
            for (var o : OUTPUTS) {
                if (predicate.test(o)) {
                    return calc(cost, o.getAmount(), 40, 0);
                }
            }
        }

        // InfinityExpansion - Quarry
        // vals:
        // static int INTERVAL
        // Material[] outputs
        // int speed
        // int energyPerTick
        //
        else if (data instanceof QuarryData qd) {
            int INTERVAL = qd.getINTERVAL();
            Material[] outputs = qd.getOutputs();
            int speed = qd.getSpeed();
            int energyPerTick = qd.getEnergyPerTick();
            if (outputs != null) {
                for (var m : outputs) {
                    if (predicate.test(new ItemStack(m))) {
                        return calc(cost, speed, INTERVAL, energyPerTick * INTERVAL);
                    }
                }
            }
        }

        // InfinityExpansion - SingularityConstructor
        // vals:
        // static List<Recipe> RECIPE_LIST
        // int energyPerTick
        //
        // Recipe vals:
        // SlimefunItemStack output
        // ItemStack input
        // String id
        // int amount
        //
        else if (data instanceof SingularityConstructorData scd) {
            List<SingularityConstructorData.Recipe> RECIPE_LIST = scd.getRECIPE_LIST();
            int energyPerTick = scd.getEnergyPerTick();
            for (var recipe : RECIPE_LIST) {
                SlimefunItemStack output = recipe.getOutput();
                ItemStack input = recipe.getInput();
                int amount = recipe.getAmount();
                if (predicate.test(Converter.getItem(output))) {
                    int speed = scd.getSpeed();
                    return calc(cost + ValueTable.getValue(input, amount), 1, speed, (double) (energyPerTick * amount) / speed);
                }
            }
        }

        // InfinityExpansion MaterialGenerator
        // vals:
        // Material material
        // int speed
        // int energyPerTick
        else if (data instanceof MaterialGeneratorData mgd) {
            Material material = mgd.getMaterial();
            int speed = mgd.getSpeed();
            int energyPerTick = mgd.getEnergyPerTick();
            if (predicate.test(new ItemStack(material))) {
                return calc(cost, speed, 1, (double) energyPerTick / speed);
            }
        }

        // InfinityExpansion - ResourceSynthesizer
        // vals:
        // SlimefunItemStack[] recipes
        // int energyPerTick
        //
        else if (data instanceof ResourceSynthesizerData rsd) {
            int energyPerTick = rsd.getEnergyPerTick();
            for (var recipe : rsd.getRecipes()) {
                ItemStack i1 = Converter.getItem(recipe.getInput1());
                ItemStack i2 = Converter.getItem(recipe.getInput2());
                ItemStack output = Converter.getItem(recipe.getOutput());
                if (predicate.test(output)) {
                    return calc(cost + ValueTable.getValue(i1) + ValueTable.getValue(i2), output.getAmount(), 1, energyPerTick);
                }
            }
        }

        // InfinityExpansion - GrowingMachine
        // vals:
        // EnumMap<Material, ItemStack[]> recipes
        // int ticksPerOutput
        // int energyPerTick
        //
        else if (data instanceof GrowingMachineData gmd) {
            EnumMap/*<Material, ItemStack[]>*/<Material, ItemStack[]> recipes = gmd.getRecipes();
            int ticksPerOutput = gmd.getTicksPerOutput();
            int energyPerTick = gmd.getEnergyPerTick();
            for (var entry : recipes.entrySet()) {
                Material material = entry.getKey();
                ItemStack[] os = entry.getValue();
                for (ItemStack o : os) {
                    if (predicate.test(o)) {
                        return calc(cost + ValueTable.getValue(new ItemStack(material)), o.getAmount(), ticksPerOutput, energyPerTick * ticksPerOutput);
                    }
                }
            }
        }

        // InfinityLib - MachineBlock
        // vals:
        // List<MachineBlockRecipe> recipes
        // int ticksPerOutput
        // int energyPerTick
        //
        // MachineBlockRecipe vals:
        // String[] strings
        // int[] amounts
        // ItemStack output
        //
        else if (data instanceof MachineBlockData mbd) {
            List<MachineBlockRecipe> recipes = mbd.getRecipes();
            int ticksPerOutput = mbd.getTicksPerOutput();
            int energyPerTick = mbd.getEnergyPerTick();

            if (recipes != null) {
                for (var recipe : recipes) {
                    ItemStack output = recipe.getOutput();
                    if (predicate.test(output)) {
                        return calc(cost + ValueTable.getValue(recipe.getInputs()), output.getAmount(), ticksPerOutput, energyPerTick * ticksPerOutput);
                    }
                }
            } else {
                // ?
            }
        }

        // DynaTech - AbstractElectricMachine
        // vals:
        // List<MachineRecipe> recipes
        // int energyConsumedPerTick
        // int processingSpeed
        //
        else if (data instanceof AbstractElectricMachineData aemd) {
            List/*<MachineRecipe>*/<MachineRecipe> recipes = aemd.getRecipes();
            int energyConsumedPerTick = aemd.getEnergyConsumedPerTick();
            int processingSpeed = aemd.getProcessingSpeed();
            for (var recipe : recipes) {
                for (var o : recipe.getOutput()) {
                    if (predicate.test(o)) {
                        return calc(cost + ValueTable.getValue(recipe.getInput()), o.getAmount(), (double) recipe.getTicks() / processingSpeed, (double) (energyConsumedPerTick * recipe.getTicks()) / processingSpeed);
                    }
                }
            }
        }

        // InfinityExpansion StoneworksFactory
        // vals:
        // int energyPerTick
        //
        else if (data instanceof StoneworksFactoryData sfd) {
            int energyPerTick = sfd.getEnergyPerTick();
            for (var material : sfd.getMaterials()) {
                if (predicate.test(new ItemStack(material))) {
                    return calc(cost + ValueTable.getValue(material), 1, 1, energyPerTick);
                }
            }
        }

        // InfinityExpansion - VoidHarvester
        // vals:
        // static int TIME
        // int speed
        // int energyPerTick
        //
        else if (data instanceof VoidHarvesterData vhd) {
            int TIME = vhd.getTIME();
            int speed = vhd.getSpeed();
            int energyPerTick = vhd.getEnergyPerTick();
            ItemStack output = vhd.getOutput();

            if (predicate.test(output)) {
                return calc(cost, output.getAmount(), (double) TIME / speed, energyPerTick);
            }
        }

        // InfinityExpansion - MobDataCard
        // vals:
        // RandomizedSet<ItemStack> drops
        //
        if (data instanceof MobDataCardData mdcd) {
            SlimefunItem chamber = mdcd.getChamber();
            int energy = mdcd.getChamberEnergy();
            int interval = mdcd.getChamberInterval();
            for (var entry : mdcd.getItemStackDoubleMap().entrySet()) {
                if (predicate.test(entry.getKey())) {
                    return calc(cost + ValueTable.getValue(chamber), entry.getValue() * entry.getKey().getAmount(), interval, energy * interval);
                }
            }
        }

        // Logitech - AbstractMachine
        // vals:
        // List<MachineRecipe> machineRecipes
        // int energyConsumption
        //
        if (data instanceof AbstractMachineData amd) {
            for (var recipe : amd.getMachineRecipes()) {
                for (var output : recipe.getOutput()) {
                    if (predicate.test(output)) {
                        return calc(cost + ValueTable.getValue(recipe.getInput()), output.getAmount(), recipe.getTicks(), recipe.getTicks() * amd.getEnergyConsumption());
                    }
                }
            }
        }

        return -2.0D;
    }

    public static double calc(double cost, double outputAmount, double processTicks, double energyCost) {
        if (Double.isInfinite(cost) || Double.isNaN(cost) || Double.isInfinite(outputAmount) || Double.isNaN(outputAmount) || Double.isInfinite(processTicks) || Double.isNaN(processTicks) || Double.isInfinite(energyCost) || Double.isNaN(energyCost)) {
            return -3.0D;
        }

        if (cost < 0.0D || outputAmount <= 0.0D || processTicks < 0.0D) {
            return -4.0D;
        }

        if (energyCost >= 0.0D) {
            return Math.log10(128.0D * outputAmount / (1.0D + processTicks * ((1.0D + cost) / 10.0D) * ((1 + energyCost) / 1.28e5D)));
        } else {
            return Math.log10(128.0D * outputAmount / (1.0D + processTicks * ((1.0D + cost) / 10.0D) - energyCost / 1.28e4D));
        }
    }

    public static String supertrim(String i1) {
        String result = ChatColor.stripColor(i1);
        result = trimp.matcher(result).replaceAll("");
        return result.replace(" ", "");
    }

    public static boolean similar(String i1, ItemStack i2) {
        if (i2 == null || i2.getType() == Material.AIR) {
            return false;
        }
        String name = ItemStackHelper.getDisplayName(i2);
        return supertrim(i1).equalsIgnoreCase(supertrim(name));
    }
}
