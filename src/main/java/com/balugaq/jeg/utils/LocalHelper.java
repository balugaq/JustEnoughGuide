/*
 * Copyright (c) 2024-2026 balugaq
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

package com.balugaq.jeg.utils;

import com.balugaq.jeg.implementation.JustEnoughGuide;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author balugaq
 * @since 1.2
 */
@SuppressWarnings({"deprecation", "ExtractMethodRecommender", "unused", "ConstantValue"})
@NullMarked
public class LocalHelper {
    public static final String def = "未知附属";
    public static final Map<String, Map<String, SlimefunItemStack>> rscItems = new HashMap<>();
    // default language is zh-CN
    // support color symbol
    public static final Map<String, String> addonLocals = new HashMap<>();
    // depends on rsc addons' info.yml
    public static final Map<String, Set<String>> rscLocals = new HashMap<>();

    static {
        loadDefault();
        for (Map.Entry<String, String> entry :
                JustEnoughGuide.getConfigManager().getLocalTranslate().entrySet()) {
            addonLocals.put(entry.getKey(), ChatColors.color(entry.getValue()));
        }
    }

    public static void loadDefault() {
        addonLocals.put("Slimefun", "粘液科技");
        addonLocals.put("ColoredEnderChests", "彩色末影箱");
        addonLocals.put("DyedBackpacks", "染色背包");
        addonLocals.put("EnderCargo", "末影货运接口");
        addonLocals.put("EcoPower", "环保能源");
        addonLocals.put("ElectricSpawners", "电动刷怪笼");
        addonLocals.put("ExoticGarden", "异域花园");
        addonLocals.put("ExtraGear", "更多装备");
        addonLocals.put("ExtraHeads", "更多头颅");
        addonLocals.put("HotbarPets", "背包宠物");
        addonLocals.put("luckyblocks-sf", "幸运方块"); // Same as SlimefunLuckyBlocks
        addonLocals.put("RedstoneConnector", "红石连接器");
        addonLocals.put("PrivateStorage", "私人储存");
        addonLocals.put("SlimefunOreChunks", "更多矿石块");
        addonLocals.put("SlimyTreeTaps", "粘液木龙头");
        addonLocals.put("SoulJars", "灵魂罐");
        addonLocals.put("MoreTools", "更多工具");
        addonLocals.put("LiteXpansion", "工业");
        addonLocals.put("MobCapturer", "生物捕捉");
        addonLocals.put("SoundMuffler", "消音器");
        addonLocals.put("ExtraTools", "额外工具");
        addonLocals.put("TranscEndence", "末地科技");
        addonLocals.put("Liquid", "液体");
        addonLocals.put("SlimefunWarfare", "战争工艺");
        addonLocals.put("InfernalExpansion", "下界工艺");
        addonLocals.put("FluffyMachines", "蓬松机器");
        addonLocals.put("SlimyRepair", "粘液物品修复");
        addonLocals.put("InfinityExpansion", "无尽贪婪"); // Avoid conflict with InfinityExpansion-Changed
        addonLocals.put("FoxyMachines", "神秘科技");
        addonLocals.put("GlobalWarming", "全球变暖");
        addonLocals.put("GlobiaMachines", "全球机器");
        addonLocals.put("DynaTech", "动力科技");
        addonLocals.put("GeneticChickengineering", "鸡因工程"); // Same as GeneticChickengineering-Reborn
        addonLocals.put("GeneticChickengineering-Reborn", "鸡因工程"); // Same as GeneticChickengineering
        addonLocals.put("ClayTech", "粘土科技"); // Same as ClayTech-Fixed
        addonLocals.put("ClayTech-Fixed", "粘土科技"); // Same as ClayTech
        addonLocals.put("SpaceTech", "太空科技"); // Same as SpaceTech-Fixed
        addonLocals.put("SpaceTech-Fixed", "太空科技"); // Same as SpaceTech
        addonLocals.put("FNAmplifications", "FN科技");
        addonLocals.put("SimpleMaterialGenerators", "简单材料生成器");
        addonLocals.put("Netheopoiesis", "下界乌托邦");
        addonLocals.put("Networks", "网络"); // Avoid conflict with Networks-Changed, (sometimes it is NetworksExpansion)
        addonLocals.put("EMC2", "等价交换(EMC2)"); // Avoid conflict with EquivalencyTech
        addonLocals.put("Nexcavate", "文明复兴");
        addonLocals.put("SimpleStorage", "简易储存");
        addonLocals.put("SimpleUtils", "简易工具");
        addonLocals.put("AlchimiaVitae", "炼金术自传");
        addonLocals.put("SlimeTinker", "粘液匠魂");
        addonLocals.put("PotionExpansion", "药剂科技");
        addonLocals.put("FlowerPower", "源之花");
        addonLocals.put("Galactifun", "星际");
        addonLocals.put("Galactifun2", "星际2");
        addonLocals.put("ElementManipulation", "化学工程");
        addonLocals.put("CrystamaeHistoria", "魔法水晶编年史");
        addonLocals.put("DankTech", "无底储存");
        addonLocals.put("DankTech2", "无底储存2");
        addonLocals.put("Networks-Changed", "网络"); // Avoid conflict with Networks
        addonLocals.put("VillagerUtil", "村民工具");
        addonLocals.put("MissileWarfare", "导弹科技");
        addonLocals.put("SensibleToolbox", "STB/未来科技");
        addonLocals.put("Endrex", "末地拓展");
        addonLocals.put("Bump", "Bump魔法");
        addonLocals.put("FinalTech", "乱序技艺"); // Same as FinalTECH
        addonLocals.put("FinalTECH", "乱序技艺"); // Same as FinalTech
        addonLocals.put("SlimefunLuckyBlocks", "幸运方块"); // Same as luckyblocks-sf
        addonLocals.put("FutureTech", "未来科技");
        addonLocals.put("DemonicExpansion", "魑魅拓展");
        addonLocals.put("BedrockTechnology", "基岩科技");
        addonLocals.put("SlimefunItemExpansion", "更多物品");
        addonLocals.put("SupplementalServiceableness", "更多日用物品");
        addonLocals.put("GuizhanCraft", "鬼斩科技");
        addonLocals.put("Magmanimous", "熔岩之息");
        addonLocals.put("UltimateGenerators-RC27", "终极发电机"); // Same as UltimateGenerators
        addonLocals.put("UltimateGenerators", "终极发电机"); // Same as UltimateGenerators-RC27
        addonLocals.put("UltimateGenerators2", "终极发电机2");
        addonLocals.put("CrispyMachine", "酥脆科技");
        addonLocals.put("Chocoholics", "虫火谷工艺"); // Same as ChocoHills
        addonLocals.put("ChocoHills", "虫火谷工艺"); // Same as Chocoholics
        addonLocals.put("draconic", "龙之研究"); // Same as DracFun
        addonLocals.put("DracFun", "龙之研究"); // Same as draconic
        addonLocals.put("EzSFAddon", "EZ科技"); // Same as EzTech, EzSlimeFunAddon
        addonLocals.put("EzTech", "EZ科技"); // Same as EzSFAddon, EzSlimeFunAddon
        addonLocals.put("EzSlimeFunAddon", "EZ科技"); // Same as EzSFAddon, EzTech
        addonLocals.put("RandomExpansion", "随机拓展");
        addonLocals.put("SlimyBees", "林业蜜蜂");
        addonLocals.put("ObsidianExpansion", "黑曜石科技");
        addonLocals.put("EMCTech", "EMC科技");
        addonLocals.put("RelicsOfCthonia", "克苏尼亚遗物");
        addonLocals.put("Supreme", "至尊研究院");
        addonLocals.put("DyeBench", "染色科技");
        addonLocals.put("MiniBlocks", "迷你方块");
        addonLocals.put("SpiritsUnchained", "灵魂巧匠");
        addonLocals.put("Cultivation", "农耕工艺");
        addonLocals.put("Gastronomicon", "美食家");
        addonLocals.put("SmallSpace", "小世界");
        addonLocals.put("BetterReactor", "工业反应堆"); // Avoid conflict with Fusion
        addonLocals.put("VillagerTrade", "村民交易");
        addonLocals.put("SlimeFrame", "粘液战甲");
        addonLocals.put("AdvancedTech", "先进科技");
        addonLocals.put("Quaptics", "量子光学");
        addonLocals.put("CompressionCraft", "压缩工艺");
        addonLocals.put("ThermalFun", "灼岩科技");
        addonLocals.put("FastMachines", "快捷机器");
        addonLocals.put("MomoTech", "乱码科技");
        addonLocals.put("LogicTech", "逻辑工艺"); // Same as LogicTECH, a SlimefunCustomizer configuration
        addonLocals.put("LogiTech", "逻辑工艺"); // Same as LogiTECH, a Slimefun addon
        addonLocals.put("LogicTECH", "逻辑工艺"); // Same as LogicTech
        addonLocals.put("LogiTECH", "逻辑工艺"); // Same as LogiTech
        addonLocals.put("SlimeAEPlugin", "能源与应用2");
        addonLocals.put("SlimeChem", "粘液化学");
        addonLocals.put("WilderNether", "迷狱生机");
        addonLocals.put("MapJammers", "地图干扰");
        addonLocals.put("Cakecraft", "蛋糕工艺"); // Same as MyFirstAddon
        addonLocals.put("SFMobDrops", "自定义生物掉落");
        addonLocals.put("Drugfun", "自定义医药用品");
        addonLocals.put("SlimefunNukes", "粘液核弹");
        addonLocals.put(
                "SlimeCustomizer",
                "自定义粘液附属"
        ); // Avoid conflict with RaySlimefunAddon, RykenSlimefunCustomizer, RykenSlimeCustomizer
        addonLocals.put(
                "RykenSlimeCustomizer",
                "Ryken自定义附属"
        ); // Same as RykenSlimefunCustomizer, avoid conflict with RaySlimefunAddon
        addonLocals.put(
                "RykenSlimefunCustomizer",
                "Ryken自定义附属"
        ); // Same as RykenSlimeCustomizer, avoid conflict with RaySlimefunAddon
        addonLocals.put("FinalTECH-Changed", "乱序技艺-改版");
        addonLocals.put("BloodAlchemy", "血炼金工艺"); // Same as BloodyAlchemy
        addonLocals.put("Laboratory", "实验室");
        addonLocals.put("MobEngineering", "生物工程");
        addonLocals.put("TsingshanTechnology", "青山科技"); // Same as TsingshanTechnology-Fixed
        addonLocals.put("TsingshanTechnology-Fixed", "青山科技"); // Same as TsingshanTechnology
        addonLocals.put("PomaExpansion", "高级安卓机器人");
        addonLocals.put("BuildingStaff", "建筑魔杖");
        addonLocals.put("IDreamOfEasy", "易梦");
        addonLocals.put("Magic8Ball", "魔法8号球");
        addonLocals.put("InfinityExpansionAutomation", "无尽自动化");
        addonLocals.put("ZeroTech", "澪数工艺");
        addonLocals.put("Ex-Limus", "新手工具");
        addonLocals.put("NotEnoughAddons", "多彩科技");
        addonLocals.put("SFWorldEdit", "粘液创世神[SW]"); // Avoid conflict with SlimefunWorldedit
        addonLocals.put("RSCEditor", "RSC编辑器");
        addonLocals.put("JustEnoughGuide", "更好的粘液书");
        addonLocals.put("SummaryHelper", "粘液刻管理");
        addonLocals.put("HardcoreSlimefun", "硬核粘液");
        addonLocals.put("SFCalc", "粘液计算器");
        addonLocals.put("SfChunkInfo", "区块信息");
        addonLocals.put("SlimefunAdvancements", "自定义粘液任务");
        addonLocals.put("SlimeHUD", "方块信息显示"); // Same as SlimeHUDPlus
        addonLocals.put("SlimeHUDPlus", "方块信息显示"); // Same as SlimeHUD
        addonLocals.put(
                "RaySlimefunAddon",
                "高级自定义粘液附属"
        ); // Avoid conflict with SlimeCustomizer, RykenSlimefunCustomizer, RykenSlimeCustomizer
        addonLocals.put("SCrafter", "SC科技"); // Same as SlimefunZT
        addonLocals.put("CrispyMachines", "酥脆机器");
        addonLocals.put("DimensionTraveler", "维度旅者");
        addonLocals.put("HardlessMachine", "弹跳工具");
        addonLocals.put("XingChengCraft", "星辰工艺"); // Same as XingChenCraft, XingCheng_Craft
        addonLocals.put("XingChenCraft", "星辰工艺"); // Same as XingChengCraft, XingCheng_Craft
        addonLocals.put("DefoLiationTech", "落叶科技"); // Same as DefoliationTech
        addonLocals.put("HaimanTech2", "海曼科技院");
        addonLocals.put("HaimanTech", "海曼科技");
        addonLocals.put("InfiniteExtensionV2", "无尽扩展V2");
        addonLocals.put("InfiniteExtension", "无尽扩展");
        addonLocals.put("OrangeTech", "橘子科技");
        addonLocals.put("GreedAndCreation", "贪婪与创世");
        addonLocals.put("BocchiTechnology", "波奇科技"); // Same as Bocchi_Technology
        addonLocals.put("Bocchi_Technology", "波奇科技"); // Same as BocchiTechnology
        addonLocals.put("OreTech", "矿物科技");
        addonLocals.put("HLGtech", "生物科技"); // Avoid conflict with MobTech
        addonLocals.put("InfiniteExtensionV2-Reconfiguration", "无尽扩展V2-改版");
        addonLocals.put("BigSnakeTech", "大蛇科技"); // Same as BigSnake-Tech
        addonLocals.put("BigSnake-Tech", "大蛇科技"); // Same as BigSnakeTech
        addonLocals.put("EpoTech", "纪元科技");
        addonLocals.put("EnchanterLimit", "限制附魔机");
        addonLocals.put("BlockLimiter", "方块限制");
        addonLocals.put("SfItemsExporter", "粘液物品导出");
        addonLocals.put("SlimeGlue", "粘液胶");
        addonLocals.put("KeepSoulbound", "高级灵魂绑定");
        addonLocals.put("SlimeFunItemBanned", "禁用物品");
        addonLocals.put("Azap", "狱刑");
        addonLocals.put("CringleBosses", "混沌Boss");
        addonLocals.put("SlimefunNotchApple", "粘液Notch旗帜图案");
        addonLocals.put("Huolaiy", "火莱伊工艺");
        addonLocals.put("WonderfulTransmitter", "奇妙发射器");
        addonLocals.put("OreGeneration", "矿物生成器"); // Avoid conflict with Mineralgenerator
        addonLocals.put("SlimeSec", "安全粘液");
        addonLocals.put("Paradoxium", "凤凰科技");
        addonLocals.put("LuckyPandas", "幸运熊猫");
        addonLocals.put("PhoenixSciences", "凤凰科学");
        addonLocals.put("DarkMatter", "夜魅");
        addonLocals.put("GeneticManipulation", "遗传学基因");
        addonLocals.put("MoneyAndThings", "固态货币");
        addonLocals.put("BeyondHorizons", "以太");
        addonLocals.put("ChestTerminal", "箱子终端");
        addonLocals.put("Hohenheim", "嬗变工艺"); // Same as hohenheim
        addonLocals.put("BetterFarming", "高级农场"); // Same as betterfarming
        addonLocals.put("NewBeginnings", "新生"); // Same as New-Beginnings
        addonLocals.put("EndCombat", "终焉");
        addonLocals.put("EnderPanda", "末地熊猫");
        addonLocals.put("SlimeVoid", "虚无粘液"); // Same as SlimefunVoid
        addonLocals.put("ArcaneExploration", "怪物强化");
        addonLocals.put("MagicXpansion", "霊幻之梦");
        addonLocals.put("SlimeQuest", "粘液任务");
        addonLocals.put("CompressedMachines", "压缩机器");
        addonLocals.put("DisguiseCookie", "伪装曲奇");
        addonLocals.put("FireSlime", "碳泥科技");
        addonLocals.put("NetherEnough", "深渊幻章");
        addonLocals.put("BarrelWiper", "蓬松拆桶器");
        addonLocals.put("BearFluidTanks", "熊式储液罐");
        addonLocals.put("Tofu-Addons", "豆腐工艺");
        addonLocals.put("AdditionalWeaponry", "武器工厂");
        addonLocals.put("BoxOfChocolates", "巧克力工艺");
        addonLocals.put("MagicPowder", "魔芋工艺"); // Same as magic-powder
        addonLocals.put("XpCreator", "造物主工艺");
        addonLocals.put("SlimefunCombat", "原子弹模型");
        addonLocals.put("ObsidianArmor", "黑曜石合金装甲"); // Same as Obsidian-Armor
        addonLocals.put("FinalGenerations", "世代同堂");
        addonLocals.put("Fusion", "工业反应堆 Fusion"); // Avoid conflict with BetterReactor
        addonLocals.put("Slimedustry", "粘液工业");
        addonLocals.put("Spikes", "更多地刺");
        addonLocals.put("SlimeRP", "现代工厂");
        addonLocals.put("Brewery", "酿酒"); // Avoid conflict with BreweryMenu
        addonLocals.put("EquivalencyTech", "等价交换(ET)"); // Avoid conflict with EMC2
        addonLocals.put("GeyserHeads", "互通头颅材质");
        addonLocals.put("VariousClutter", "杂乱物品");
        addonLocals.put("Mineralgenerator", "Mineral 矿物生成器"); // Avoid conflict with OreGeneration
        addonLocals.put("CivilizationEvolution", "AG科技"); // Avoid conflict with AgTech
        addonLocals.put("RemiliasUtilities", "雷米科技");
        addonLocals.put("BetterChests", "更好的箱子");
        addonLocals.put("SlimeFood", "粘液美食");
        addonLocals.put("SlimeVision", "粘液可视化");
        addonLocals.put("WorldeditSlimefun", "粘液创世神[WS]"); // Avoid conflict with SFWorldedit
        addonLocals.put("MinimizeFactory", "最小化工厂");
        addonLocals.put("InfinityCompress", "无尽压缩");
        addonLocals.put("SlimeFrameExtension", "粘液战甲扩展");
        addonLocals.put("BreweryMenu", "酿酒GUI"); // Avoid conflict with Brewery
        addonLocals.put("MySlimefunAddon", "自制拓展");
        addonLocals.put("MyFirstAddon", "蛋糕工艺"); // Same as Cakecraft
        addonLocals.put("StackMachine", "堆叠机器"); // Avoid conflict with SlimefunStackMachine
        addonLocals.put("SlimefunStackMachine", "粘液堆叠机器"); // Avoid conflict with StackMachine
        addonLocals.put("CraftableEnchantments", "附魔工艺");
        addonLocals.put("sj_Expansion", "sjの粘液拓展");
        addonLocals.put("SlimefunZT", "SC科技"); // Same as SCrafter
        addonLocals.put("SlimefunAddon", "CAPTAINchad12自制拓展"); // Unbelievable...
        addonLocals.put("AngleTech", "倾斜科技");
        addonLocals.put("magicexpansion", "魔法拓展"); // Same as MagicExpansion, avoid conflict with Magic
        addonLocals.put("MagicExpansion", "魔法拓展"); // Same as magicexpansion, avoid conflict with Magic
        addonLocals.put("SlimefunHopper", "粘液漏斗");
        addonLocals.put("SlimefunAccessor", "远程访问器");
        addonLocals.put("ExoticGardenComplex", "异域花园"); // Same as ExoticGarden
        addonLocals.put("magic-powder", "魔芋工艺"); // Same as MagicPowder
        addonLocals.put("Obsidian-Armor", "黑曜石合金装甲"); // Same as ObsidianArmor
        addonLocals.put("BloodyAlchemy", "血炼金工艺"); // Same as BloodAlchemy
        addonLocals.put("hohenheim", "嬗变工艺"); // Same as Hohenheim
        addonLocals.put("HALsAddon", "终界之地"); // Same as slimestack
        addonLocals.put("slimestack", "终界之地"); // Same as HALsAddon
        addonLocals.put("SlimefunVoid", "虚无粘液"); // Same as SlimeVoid
        addonLocals.put("betterfarming", "高级农场"); // Same as BetterFarming
        addonLocals.put("New-Beginnings", "新生"); // Same as NewBeginnings
        addonLocals.put("ExLimus", "新手工具"); // Same as Ex-Limus
        addonLocals.put("Aeterum", "众神之马");
        addonLocals.put("PoseidonAddon", "浪涌科技");
        addonLocals.put("Aircraft", "粘液飞机");
        addonLocals.put("InfinityExpansion2", "无尽贪婪2");
        addonLocals.put("EtherTech", "虚素科技");
        addonLocals.put("SlimefunTimeit", "性能监视器");
        addonLocals.put("AgTech", "AG科技 - RSC"); // Avoid conflict with CivilizationEvolution
        addonLocals.put("CavernTech", "洞穴科技");
        addonLocals.put("Creation", "创世");
        addonLocals.put("Greed", "贪婪");
        addonLocals.put("HoosierTech", "胡希尔科技");
        addonLocals.put("HorizonsGears", "地平线装甲");
        addonLocals.put("langui", "懒鬼科技");
        addonLocals.put("Magic", "魔法"); // Same as MagicExpansion, magicexpansion
        addonLocals.put("MetaCoin", "数字硬币");
        addonLocals.put("MobSimulationPlus", "生物芯片扩展");
        addonLocals.put("MoreUniqueTools", "更多奇妙的工具");
        addonLocals.put("PinksheepTech", "粉羊科技");
        addonLocals.put("PinksheepTech_EpoTech", "纪元拓展");
        addonLocals.put("RepairStation", "SC修复站"); // Same as SlimeCustomizerRepairStation
        addonLocals.put("SlimeCustomizerRepairStation", "SC修复站"); // Same as RepairStation
        addonLocals.put("SlimefunNetherTech2", "下界科技2");
        addonLocals.put("snion", "工业驱动");
        addonLocals.put("SuperFood", "超级食品");
        addonLocals.put("Typhfun", "Typhfun");
        addonLocals.put("WolfyMachines", "狼之机器");
        addonLocals.put("XingCheng_Craft", "星辰工艺"); // Same as XingChengCraft, XingChenCraft
        addonLocals.put("WorldTaste", "尘世百味");
        addonLocals.put("Automation", "粘液电脑");
        addonLocals.put("MobTech", "生物科技(MT)"); // Avoid conflict with HLGTech
        addonLocals.put("Strophodungeons", "地牢迷宫");
        addonLocals.put("SFTeacher", "粘液科技教程");
        addonLocals.put("Slimefunexpansion", "粘液科技拓展");
        addonLocals.put("HiveCorporation", "H公司");
        addonLocals.put("BlackFishTech", "黑鱼科技");
        addonLocals.put("MerakTech", "天璇科技");
        addonLocals.put("TinselStar", "箔澜星");
        addonLocals.put("Annihilation_Tech", "湮灭科技"); // Same as AnnihilationTech
        addonLocals.put("AnnihilationTech", "湮灭科技"); // Same as Annihilation_Tech
        addonLocals.put("HiServerTech", "Hi世界科技");
        addonLocals.put("DefoliationTech", "落叶科技"); // Same as DefoLiationTech
        addonLocals.put("HeadQuantumStorage", "头颅版量子存储");
        addonLocals.put("LengShangTech", "冷殇科技");
        addonLocals.put("MuzhouTech", "暮舟科技");
        addonLocals.put("DFD_InfiniteExtensionV2", "DFD - 无尽V2重构版");
        addonLocals.put("DFD_Expand", "DFD - 缘落科技");
        addonLocals.put("Blocktreetech", "方块树");
        addonLocals.put("TinCraft", "&kabc &fTinCraft &kabc&r");
        addonLocals.put("InfinityExpansion-Changed", "无尽贪婪(心孜改版)"); // Avoid conflict with InfinityExpansion
        addonLocals.put("fm_tech", "浮木科技");
        addonLocals.put("Supermarket", "超能力机器");
        addonLocals.put("HseerTech", "HseerMC科技");
        addonLocals.put("ZeroSequenceTechnique", "零序技艺");
        addonLocals.put("OriginTech", "起源工艺");
        addonLocals.put("SimpleTech", "简单科技");
        addonLocals.put("Komutech", "口木科技");
        addonLocals.put("SlimefunInfiniteBlocks", "粘液无尽方块");
        addonLocals.put("RSC_YunQi_History", "云启文明");
        addonLocals.put("fvvtech", "fvv科技");
        addonLocals.put("AeroDragonTech", "飞龙科技");
        addonLocals.put("REGS", "沐莱物语");
    }

    public static String getOfficialAddonName(ItemGroup itemGroup, String itemId) {
        return getOfficialAddonName(itemGroup.getAddon(), itemId, def);
    }

    public static String getOfficialAddonName(
            @Nullable SlimefunAddon addon, String itemId, String callback) {
        return getOfficialAddonName(addon == null ? "Slimefun" : addon.getName(), itemId, callback);
    }

    public static String getOfficialAddonName(
            String addonName, String itemId, String callback) {
        return getAddonName(addonName, itemId, callback) + " (" + addonName + ")";
    }

    public static String getAddonName(String addonName, String itemId, String callback) {
        if (addonName == null) {
            return ChatColors.color(callback);
        }

        if ("RykenSlimefunCustomizer".equalsIgnoreCase(addonName)
                || "RykenSlimeCustomizer".equalsIgnoreCase(addonName)) {
            return getRSCLocalName(itemId);
        }
        String localName = addonLocals.get(addonName);
        return ChatColors.color(localName == null ? callback : localName);
    }

    // get a rsc addon name by item id
    public static String getRSCLocalName(String itemId) {
        for (Map.Entry<String, Set<String>> entry : rscLocals.entrySet()) {
            if (entry.getValue().contains(itemId)) {
                return ChatColors.color(entry.getKey());
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
                if (addonManager != null) {
                    Object projectAddons = ReflectionUtil.getValue(addonManager, "projectAddons");
                    @SuppressWarnings("unchecked")
                    Map<Object, Object> map = (Map<Object, Object>) projectAddons;
                    if (map != null) {
                        for (Map.Entry<Object, Object> entry : map.entrySet()) {
                            Object addon = entry.getValue();
                            Object addonName = ReflectionUtil.getValue(addon, "addonName");
                            String name = (String) addonName;
                            if (name == null) {
                                continue;
                            }
                            Object preloadItems = ReflectionUtil.getValue(addon, "preloadItems");
                            @SuppressWarnings("unchecked")
                            Map<Object, Object> items = (Map<Object, Object>) preloadItems;
                            Map<String, SlimefunItemStack> read = new HashMap<>();
                            if (items != null) {
                                for (Map.Entry<Object, Object> itemEntry : items.entrySet()) {
                                    String id = (String) itemEntry.getKey();
                                    SlimefunItemStack item = (SlimefunItemStack) itemEntry.getValue();
                                    read.put(id, item);
                                }
                            }
                            rscItems.put(name, read);
                        }
                    }
                }
            } catch (Exception e) {
                Debug.trace(e);
            }
        }

        for (Map.Entry<String, Map<String, SlimefunItemStack>> entry : rscItems.entrySet()) {
            Map<String, SlimefunItemStack> items = entry.getValue();
            if (items.containsKey(itemId)) {
                return ChatColors.color(entry.getKey());
            }
        }

        return ChatColors.color(def);
    }

    public static String getOfficialAddonName(
            ItemGroup itemGroup, String itemId, String callback) {
        return itemGroup.getAddon() == null ? def : getOfficialAddonName(itemGroup.getAddon(), itemId, callback);
    }

    public static String getOfficialAddonName(@Nullable SlimefunAddon addon, String itemId) {
        return getOfficialAddonName(addon, itemId, def);
    }

    public static String getOfficialAddonName(String addonName, String itemId) {
        return getOfficialAddonName(addonName, itemId, def);
    }

    public static String getAddonName(ItemGroup itemGroup, String itemId) {
        return getAddonName(itemGroup, itemId, def);
    }

    public static String getAddonName(ItemGroup itemGroup, String itemId, String callback) {
        return itemGroup.getAddon() == null
                ? def
                : getAddonName(itemGroup.getAddon().getName(), itemId, callback);
    }

    public static String getAddonName(@Nullable SlimefunAddon addon) {
        if (addon == null) return def;
        return ChatColors.color(addonLocals.getOrDefault(addon.getName(), def));
    }

    public static String getAddonName(@Nullable SlimefunAddon addon, String itemId) {
        return getAddonName(addon, itemId, def);
    }

    public static String getAddonName(@Nullable SlimefunAddon addon, String itemId, String callback) {
        return getAddonName(addon == null ? addonLocals.get("Slimefun") : addon.getName(), itemId, callback);
    }

    public static String getAddonName(String addonName, String itemId) {
        return getAddonName(addonName, itemId, def);
    }

    public static void addRSCLocal(String rscAddonName, String itemId) {
        if (!rscLocals.containsKey(rscAddonName)) {
            rscLocals.put(rscAddonName, new HashSet<>());
        }

        rscLocals.get(rscAddonName).add(itemId);
    }

    public static String getDisplayName(ItemGroup itemGroup, Player player) {
        ItemMeta meta = itemGroup.getItem(player).getItemMeta();
        if (meta == null) {
            return def;
        }

        return meta.getDisplayName();
    }
}
