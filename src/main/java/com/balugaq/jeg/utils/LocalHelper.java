package com.balugaq.jeg.utils;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LocalHelper {
    public static final String def = "Unknown addon";
    public static final Map<String, Map<String, SlimefunItemStack>> rscItems = new HashMap<>();
    public static final Map<String, String> addonLocals = new HashMap<>();
    // depends on rsc addons' info.yml
    public static final Map<String, Set<String>> rscLocals = new HashMap<>();

    static {
        addonLocals.put("Slimefun", "Slimefun");
        addonLocals.put("ColoredEnderChests", "Colored Ender Chests");
        addonLocals.put("DyedBackpacks", "Dyed Backpacks");
        addonLocals.put("EnderCargo", "Ender Cargo");
        addonLocals.put("EcoPower", "Eco Power");
        addonLocals.put("ElectricSpawners", "Electric Spawners");
        addonLocals.put("ExoticGarden", "Exotic Garden");
        addonLocals.put("ExtraGear", "Extra Gear");
        addonLocals.put("ExtraHeads", "Extra Heads");
        addonLocals.put("HotbarPets", "Hotbar Pets");
        addonLocals.put("luckyblocks-sf", "Lucky Blocks"); // Same as SlimefunLuckyBlocks
        addonLocals.put("RedstoneConnector", "Redstone Connector");
        addonLocals.put("PrivateStorage", "Private Storage");
        addonLocals.put("SlimefunOreChunks", "Ore Chunks");
        addonLocals.put("SlimyTreeTaps", "Slimy Tree Taps");
        addonLocals.put("SoulJars", "Soul Jars");
        addonLocals.put("MoreTools", "More Tools");
        addonLocals.put("LiteXpansion", "Lite Xpansion");
        addonLocals.put("MobCapturer", "Mob Capturer");
        addonLocals.put("SoundMuffler", "Sound Muffler");
        addonLocals.put("ExtraTools", "Extra Tools");
        addonLocals.put("TranscEndence", "TranscEndence");
        addonLocals.put("Liquid", "Liquid");
        addonLocals.put("SlimefunWarfare", "Warfare");
        addonLocals.put("InfernalExpansion", "Infernal Expansion");
        addonLocals.put("FluffyMachines", "Fluffy Machines");
        addonLocals.put("SlimyRepair", "Slimy Repair");
        addonLocals.put("InfinityExpansion", "Infinity Expansion");
        addonLocals.put("FoxyMachines", "Foxy Machines");
        addonLocals.put("GlobalWarming", "Global Warming");
        addonLocals.put("GlobiaMachines", "Globia Machines");
        addonLocals.put("DynaTech", "DynaTech");
        addonLocals.put("GeneticChickengineering", "Genetic Chickengineering"); // Same as GeneticChickengineering-Reborn
        addonLocals.put("GeneticChickengineering-Reborn", "Genetic Chickengineering"); // Same as GeneticChickengineering
        addonLocals.put("ClayTech", "ClayTech"); // Same as ClayTech-Fixed
        addonLocals.put("ClayTech-Fixed", "ClayTech"); // Same as ClayTech
        addonLocals.put("SpaceTech", "SpaceTech"); // Same as SpaceTech-Fixed
        addonLocals.put("SpaceTech-Fixed", "SpaceTech"); // Same as SpaceTech
        addonLocals.put("FNAmplifications", "FN Amplifications");
        addonLocals.put("SimpleMaterialGenerators", "SMG");
        addonLocals.put("Netheopoiesis", "Netheopoiesis");
        addonLocals.put("Networks", "Networks"); // Same as Networks-Changed (sometimes it is NetworksExpansion)
        addonLocals.put("EMC2", "EMC2"); // Avoid conflict with EquivalencyTech
        addonLocals.put("Nexcavate", "Nexcavate");
        addonLocals.put("SimpleStorage", "Simple Storage");
        addonLocals.put("SimpleUtils", "Simple Utils");
        addonLocals.put("AlchimiaVitae", "Alchimia Vitae");
        addonLocals.put("SlimeTinker", "SlimeTinker");
        addonLocals.put("PotionExpansion", "Potion Expansion");
        addonLocals.put("FlowerPower", "FlowerPower");
        addonLocals.put("Galactifun", "Galactifun");
        addonLocals.put("Galactifun2", "Galactifun2");
        addonLocals.put("ElementManipulation", "Element Manipulation");
        addonLocals.put("CrystamaeHistoria", "Crystamae Historia");
        addonLocals.put("DankTech", "DankTech");
        addonLocals.put("DankTech2", "DankTech - 2");
        addonLocals.put("Networks-Changed", "Networks"); // Same as Networks
        addonLocals.put("VillagerUtil", "Villager Util");
        addonLocals.put("MissileWarfare", "Missile Warfare");
        addonLocals.put("SensibleToolbox", "STB");
        addonLocals.put("Endrex", "Endrex");
        addonLocals.put("Bump", "Bump");
        addonLocals.put("FinalTech", "Final Tech"); // Same as FinalTECH
        addonLocals.put("FinalTECH", "Final TECH"); // Same as FinalTech
        addonLocals.put("SlimefunLuckyBlocks", "Lucky Blocks"); // Same as luckyblocks-sf
        addonLocals.put("FutureTech", "Future Tech");
        addonLocals.put("DemonicExpansion", "Demonic Expansion");
        addonLocals.put("BedrockTechnology", "Bedrock Technology");
        addonLocals.put("SlimefunItemExpansion", "Slimefun Item Expansion");
        addonLocals.put("SupplementalServiceableness", "Supplemental Serviceableness");
        addonLocals.put("GuizhanCraft", "Guizhan Craft");
        addonLocals.put("Magmanimous", "Magmanimous");
        addonLocals.put("UltimateGenerators-RC27", "Ultimate Generators"); // Same as UltimateGenerators
        addonLocals.put("UltimateGenerators", "Ultimate Generators"); // Same as UltimateGenerators-RC27"); // Same as UltimateGenerators-RC27
        addonLocals.put("UltimateGenerators2", "Ultimate Generators 2");
        addonLocals.put("CrispyMachine", "Crispy Machine");
        addonLocals.put("Chocoholics", "Chocoholics"); // Same as ChocoHills
        addonLocals.put("ChocoHills", "ChocoHills"); // Same as Chocoholics
        addonLocals.put("draconic", "Draconic"); // Same as DracFun
        addonLocals.put("DracFun", "DracFun"); // Same as draconic
        addonLocals.put("EzSFAddon", "EZ Tech"); // Same as EzTech, EzSlimeFunAddon
        addonLocals.put("EzTech", "EZ Tech"); // Same as EzSFAddon, EzSlimeFunAddon
        addonLocals.put("EzSlimeFunAddon", "EZ Tech"); // Same as EzSFAddon, EzTech
        addonLocals.put("RandomExpansion", "Random Expansion");
        addonLocals.put("SlimyBees", "Slimy Bees");
        addonLocals.put("ObsidianExpansion", "Obsidian Expansion");
        addonLocals.put("EMCTech", "EMC Tech");
        addonLocals.put("RelicsOfCthonia", "Relics of Cthonia");
        addonLocals.put("Supreme", "Supreme");
        addonLocals.put("DyeBench", "DyeBench");
        addonLocals.put("MiniBlocks", "MiniBlocks");
        addonLocals.put("SpiritsUnchained", "Spirits Unchained");
        addonLocals.put("Cultivation", "Cultivation");
        addonLocals.put("Gastronomicon", "Gastronomicon");
        addonLocals.put("SmallSpace", "Small Space");
        addonLocals.put("BetterReactor", "Better Reactor"); // Avoid conflict with Fusion
        addonLocals.put("VillagerTrade", "Villager Trade");
        addonLocals.put("SlimeFrame", "SlimeFrame");
        addonLocals.put("AdvancedTech", "Advanced Tech");
        addonLocals.put("Quaptics", "Quaptics");
        addonLocals.put("CompressionCraft", "Compression Craft");
        addonLocals.put("ThermalFun", "Thermal Fun");
        addonLocals.put("FastMachines", "Fast Machines");
        addonLocals.put("MomoTech", "Momo Tech");
        addonLocals.put("LogicTech", "LogicTech"); // Same as LogicTECH, a SlimefunCustomizer configuration
        addonLocals.put("LogiTech", "LogiTech"); // Same as LogiTECH, a Slimefun addon
        addonLocals.put("LogicTECH", "LogicTECH"); // Same as LogicTech
        addonLocals.put("LogiTECH", "LogiTECH"); // Same as LogiTech
        addonLocals.put("SlimeAEPlugin", "Slime AE 2");
        addonLocals.put("SlimeChem", "Slime Chem");
        addonLocals.put("WilderNether", "Wilder Nether");
        addonLocals.put("MapJammers", "MapJammers");
        addonLocals.put("Cakecraft", "Cakecraft"); // Same as MyFirstAddon
        addonLocals.put("SFMobDrops", "Custom Mob Drops");
        addonLocals.put("Drugfun", "Custom Drug");
        addonLocals.put("SlimefunNukes", "Slimefun Nukes");
        addonLocals.put("SlimeCustomizer", "SlimeCustomizer");
        addonLocals.put("RykenSlimeCustomizer", "Ryken SlimefunCustomizer"); // Same as RykenSlimefunCustomizer
        addonLocals.put("RykenSlimefunCustomizer", "Ryken SlimefunCustomizer"); // Same as RykenSlimeCustomizer
        addonLocals.put("FinalTECH-Changed", "FinalTECH-Changed");
        addonLocals.put("BloodAlchemy", "Blood Alchemy");
        addonLocals.put("Laboratory", "Laboratory");
        addonLocals.put("MobEngineering", "Mob Engineering");
        addonLocals.put("TsingshanTechnology", "Tsingshan Technology"); // Same as TsingshanTechnology-Fixed
        addonLocals.put("TsingshanTechnology-Fixed", "Tsingshan Technology"); // Same as TsingshanTechnology
        addonLocals.put("PomaExpansion", "Poma Expansion");
        addonLocals.put("BuildingStaff", "Building Staff");
        addonLocals.put("IDreamOfEasy", "I Dream of Easy");
        addonLocals.put("Magic8Ball", "Magic 8 Ball");
        addonLocals.put("InfinityExpansionAutomation", "Infinity Expansion Automation");
        addonLocals.put("ZeroTech", "Zero Tech");
        addonLocals.put("Ex-Limus", "Ex-Limus");
        addonLocals.put("NotEnoughAddons", "Not Enough Addons");
        addonLocals.put("SFWorldEdit", "Slimefun WorldEdit [SW]"); // Avoid conflict with SlimefunWorldedit
        addonLocals.put("RSCEditor", "RSC Editor");
        addonLocals.put("JustEnoughGuide", "Just Enough Guide");
        addonLocals.put("SummaryHelper", "Summary Helper");
        addonLocals.put("HardcoreSlimefun", "Hardcore Slimefun");
        addonLocals.put("SFCalc", "SF Calc");
        addonLocals.put("SfChunkInfo", "SF Chunk Info");
        addonLocals.put("SlimefunAdvancements", "Slimefun Advancements");
        addonLocals.put("SlimeHUD", "Slime HUD");
        addonLocals.put("RaySlimefunAddon", "Ray Slimefun Addon");
        addonLocals.put("SCrafter", "SC Tech");
        addonLocals.put("CrispyMachines", "Crispy Machines");
        addonLocals.put("DimensionTraveler", "Dimension Traveler");
        addonLocals.put("HardlessMachine", "Hardless Machine");
        addonLocals.put("XingChengCraft", "XingChenCraft"); // Same as XingChenCraft
        addonLocals.put("XingChenCraft", "XingChenCraft"); // Same as XingChengCraft
        addonLocals.put("DefoLiationTech", "DefoLiation Tech");
        addonLocals.put("HaimanTech2", "HaimanTech2");
        addonLocals.put("HaimanTech", "HaimanTech");
        addonLocals.put("InfiniteExtensionV2", "Infinite Extension V2");
        addonLocals.put("InfiniteExtension", "Infinite Extension");
        addonLocals.put("OrangeTech", "Orange Tech");
        addonLocals.put("GreedAndCreation", "Green and Creation");
        addonLocals.put("BocchiTechnology", "Bocchi Technology");
        addonLocals.put("OreTech", "Ore Tech");
        addonLocals.put("HLGtech", "HLG Tech");
        addonLocals.put("InfiniteExtensionV2-Reconfiguration", "Infinite Extension V2-Changed");
        addonLocals.put("BigSnakeTech", "Big Snake Tech");
        addonLocals.put("EpoTech", "EpoTech");
        addonLocals.put("EnchanterLimit", "Enchanter Limit");
        addonLocals.put("BlockLimiter", "Block Limiter");
        addonLocals.put("SfItemsExporter", "SF Items Exporter");
        addonLocals.put("SlimeGlue", "SlimeGlue");
        addonLocals.put("KeepSoulbound", "Advanced Soulbound");
        addonLocals.put("SlimeFunItemBanned", "Slimefun Item Banned");
        addonLocals.put("Azap", "Azap");
        addonLocals.put("CringleBosses", "Cringle Boss");
        addonLocals.put("SlimefunNotchApple", "Notch Apple");
        addonLocals.put("Huolaiy", "Huolaiy");
        addonLocals.put("WonderfulTransmitter", "Wonderful Transmitter");
        addonLocals.put("OreGeneration", "Ore Generation"); // Avoid conflict with Mineralgenerator
        addonLocals.put("SlimeSec", "Slime Security");
        addonLocals.put("Paradoxium", "Paradoxium");
        addonLocals.put("LuckyPandas", "Lucky Pandas");
        addonLocals.put("PhoenixSciences", "Phoenix Sciences");
        addonLocals.put("DarkMatter", "Dark Matter");
        addonLocals.put("GeneticManipulation", "Genetic Manipulation");
        addonLocals.put("MoneyAndThings", "Money and Things");
        addonLocals.put("BeyondHorizons", "Beyond Horizons");
        addonLocals.put("ChestTerminal", "Chest Terminal");
        addonLocals.put("Hohenheim", "Hohenheim");
        addonLocals.put("BetterFarming", "Better Farming");
        addonLocals.put("NewBeginnings", "New Beginnings");
        addonLocals.put("EndCombat", "End Combat");
        addonLocals.put("EnderPanda", "Ender Panda");
        addonLocals.put("SlimeVoid", "Slime Void");
        addonLocals.put("ArcaneExploration", "Arcane Exploration");
        addonLocals.put("MagicXpansion", "Magic Xpansion");
        addonLocals.put("SlimeQuest", "Slime Quest");
        addonLocals.put("CompressedMachines", "Compressed Machines");
        addonLocals.put("DisguiseCookie", "Disguise Cookie");
        addonLocals.put("FireSlime", "Fire Slime");
        addonLocals.put("NetherEnough", "Nether Enough");
        addonLocals.put("BarrelWiper", "Barrel Wiper");
        addonLocals.put("BearFluidTanks", "Bear Fluid Tanks");
        addonLocals.put("Tofu-Addons", "Tofu Addons");
        addonLocals.put("AdditionalWeaponry", "Additional Weaponry");
        addonLocals.put("BoxOfChocolates", "Box of Chocolates");
        addonLocals.put("MagicPowder", "Magic Powder");
        addonLocals.put("XpCreator", "Xp Creator");
        addonLocals.put("SlimefunCombat", "Slimefun Combat");
        addonLocals.put("ObsidianArmor", "Obsidian Armor");
        addonLocals.put("FinalGenerations", "Final Generations");
        addonLocals.put("Fusion", "Fusion"); // Avoid conflict with BetterReactor
        addonLocals.put("Slimedustry", "Slimedustry");
        addonLocals.put("Spikes", "Spikes");
        addonLocals.put("SlimeRP", "SlimeRP");
        addonLocals.put("Brewery", "Brewery"); // Avoid conflict with BreweryMenu
        addonLocals.put("EquivalencyTech", "Equivalency Tech"); // Avoid conflict with EMC2
        addonLocals.put("GeyserHeads", "Geyser Heads");
        addonLocals.put("VariousClutter", "Various Clutter");
        addonLocals.put("Mineralgenerator", "Mineral Generator"); // Avoid conflict with OreGeneration
        addonLocals.put("CivilizationEvolution", "AG Tech");
        addonLocals.put("RemiliasUtilities", "Remilia's Utilities");
        addonLocals.put("BetterChests", "Better Chests");
        addonLocals.put("SlimeFood", "Slime Food");
        addonLocals.put("SlimeVision", "Slime Vision");
        addonLocals.put("WorldeditSlimefun", "Slimefun WorldEdit [WS]"); // Avoid conflict with SFWorldedit
        addonLocals.put("MinimizeFactory", "Minimize Factory");
        addonLocals.put("InfinityCompress", "Infinity Compress");
        addonLocals.put("SlimeFrameExtension", "Slime Frame Extension");
        addonLocals.put("BreweryMenu", "Brewery Menu"); // Avoid conflict with Brewery
        addonLocals.put("MySlimefunAddon", "My Slimefun Addon");
        addonLocals.put("MyFirstAddon", "Cakecraft"); // Same as Cakecraft
        addonLocals.put("StackMachine", "Stack Machine"); // Avoid conflict with SlimefunStackMachine
        addonLocals.put("SlimefunStackMachine", "Slimefun Stack Machine"); // Avoid conflict with StackMachine
        addonLocals.put("CraftableEnchantments", "Craftable Enchantments");
        addonLocals.put("sj_Expansion", "sj's Expansion");
        addonLocals.put("SlimefunZT", "SiciliaCraft");
        addonLocals.put("SlimefunAddon", "CAPTAINchad12's addon"); // Unbelievable...
        addonLocals.put("AngleTech", "Angle Tech");
    }

    @Nonnull
    public static String getOfficialAddonName(@Nonnull ItemGroup itemGroup, @Nonnull String itemId) {
        return getOfficialAddonName(itemGroup.getAddon(), itemId, def);
    }

    @Nonnull
    public static String getOfficialAddonName(@Nonnull ItemGroup itemGroup, @Nonnull String itemId, @NotNull String callback) {
        return itemGroup.getAddon() == null ? def : getOfficialAddonName(itemGroup.getAddon(), itemId, callback);
    }

    @Nonnull
    public static String getOfficialAddonName(@Nullable SlimefunAddon addon, @Nonnull String itemId) {
        return getOfficialAddonName(addon, itemId, def);
    }

    @Nonnull
    public static String getOfficialAddonName(@Nullable SlimefunAddon addon, @Nonnull String itemId, @NotNull String callback) {
        return getOfficialAddonName(addon == null ? "Slimefun" : addon.getName(), itemId, callback);
    }

    @Nonnull
    public static String getOfficialAddonName(@Nonnull String addonName, @Nonnull String itemId) {
        return getOfficialAddonName(addonName, itemId, def);
    }

    @Nonnull
    public static String getOfficialAddonName(@Nonnull String addonName, @Nonnull String itemId, @NotNull String callback) {
        return getAddonName(addonName, itemId, callback) + " (" + addonName + ")";
    }

    @Nonnull
    public static String getAddonName(@Nonnull ItemGroup itemGroup, @Nonnull String itemId) {
        return getAddonName(itemGroup, itemId, def);
    }

    @Nonnull
    public static String getAddonName(@Nonnull ItemGroup itemGroup, @Nonnull String itemId, @Nonnull String callback) {
        return itemGroup.getAddon() == null ? def : getAddonName(itemGroup.getAddon().getName(), itemId, callback);
    }

    @Nonnull
    public static String getAddonName(@Nullable SlimefunAddon addon, @Nonnull String itemId) {
        return getAddonName(addon, itemId, def);
    }

    @Nonnull
    public static String getAddonName(@Nullable SlimefunAddon addon, @Nonnull String itemId, @NotNull String callback) {
        return getAddonName(addon == null ? addonLocals.get("Slimefun") : addon.getName(), itemId, callback);
    }

    @Nonnull
    public static String getAddonName(@Nonnull String addonName, @Nonnull String itemId) {
        return getAddonName(addonName, itemId, def);
    }

    @Nonnull
    public static String getAddonName(@Nonnull String addonName, @Nonnull String itemId, @NotNull String callback) {
        if (addonName == null) {
            return callback;
        }

        if ("RykenSlimefunCustomizer".equalsIgnoreCase(addonName) || "RykenSlimeCustomizer".equalsIgnoreCase(addonName)) {
            return getRSCLocalName(itemId);
        }
        String localName = addonLocals.get(addonName);
        return localName == null ? callback : localName;
    }

    public static void addRSCLocal(String rscAddonName, String itemId) {
        if (!rscLocals.containsKey(rscAddonName)) {
            rscLocals.put(rscAddonName, new HashSet<>());
        }

        rscLocals.get(rscAddonName).add(itemId);
    }

    // get a rsc addon name by item id
    public static String getRSCLocalName(String itemId) {
        for (Map.Entry<String, Set<String>> entry : rscLocals.entrySet()) {
            if (entry.getValue().contains(itemId)) {
                return entry.getKey();
            }
        }

        String def = addonLocals.get("RykenSlimefunCustomizer");
        if (def == null) {
            def = addonLocals.get("RykenSlimeCustomizer");
        }

        if (rscItems.isEmpty()) {
            try {
                Plugin rsc1 = Bukkit.getPluginManager().getPlugin("RykenSlimefunCustomizer");
                Plugin rsc2 = null;
                if (rsc1 == null) {
                    rsc2 = Bukkit.getPluginManager().getPlugin("RykenSlimeCustomizer");
                    if (rsc2 == null) {
                        return def;
                    }
                }

                Plugin rsc = rsc1 == null ? rsc2 : rsc1;
                if (rsc == null) {
                    return def;
                }
                Object addonManager = ReflectionUtil.getValue(rsc, "addonManager");
                Object projectAddons = ReflectionUtil.getValue(addonManager, "projectAddons");
                @SuppressWarnings("unchecked") Map<Object, Object> map = (Map<Object, Object>) projectAddons;
                for (Map.Entry<Object, Object> entry : map.entrySet()) {
                    Object addon = entry.getValue();
                    Object addonName = ReflectionUtil.getValue(addon, "addonName");
                    String name = (String) addonName;
                    Object preloadItems = ReflectionUtil.getValue(addon, "preloadItems");
                    @SuppressWarnings("unchecked") Map<Object, Object> items = (Map<Object, Object>) preloadItems;
                    Map<String, SlimefunItemStack> read = new HashMap<>();
                    for (Map.Entry<Object, Object> itemEntry : items.entrySet()) {
                        String id = (String) itemEntry.getKey();
                        SlimefunItemStack item = (SlimefunItemStack) itemEntry.getValue();
                        read.put(id, item);
                    }
                    rscItems.put(name, read);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<String, Map<String, SlimefunItemStack>> entry : rscItems.entrySet()) {
            Map<String, SlimefunItemStack> items = entry.getValue();
            if (items.containsKey(itemId)) {
                return entry.getKey();
            }
        }

        return def;
    }

    @NotNull
    public static String getDisplayName(@NotNull ItemGroup itemGroup, @NotNull Player player) {
        ItemMeta meta = itemGroup.getItem(player).getItemMeta();
        if (meta == null) {
            return def;
        }

        return meta.getDisplayName();
    }
}
