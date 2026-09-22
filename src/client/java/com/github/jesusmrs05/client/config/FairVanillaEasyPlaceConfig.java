package com.github.jesusmrs05.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FairVanillaEasyPlaceConfig {

    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private static final Path CONFIG_PATH =
            FabricLoader.getInstance()
                    .getConfigDir()
                    .resolve("fair-vanilla-easy-place.json");

    private static final String DEFAULT_SCAFFOLD_BLOCK =
            "minecraft:oak_planks";

    private String scaffoldBlock =
            DEFAULT_SCAFFOLD_BLOCK;

    private FairVanillaEasyPlaceConfig() {
    }

    public static FairVanillaEasyPlaceConfig load() {
        FairVanillaEasyPlaceConfig config =
                new FairVanillaEasyPlaceConfig();

        if (!Files.exists(CONFIG_PATH)) {
            config.save();
            return config;
        }

        try {
            String json =
                    Files.readString(CONFIG_PATH);

            JsonObject object =
                    JsonParser.parseString(json)
                            .getAsJsonObject();

            if (object.has("scaffold_block")) {
                config.scaffoldBlock =
                        object.get("scaffold_block")
                                .getAsString();
            }

            if (!config.hasValidScaffoldBlock()) {
                config.scaffoldBlock =
                        DEFAULT_SCAFFOLD_BLOCK;
            }

        } catch (Exception ignored) {
            config.scaffoldBlock =
                    DEFAULT_SCAFFOLD_BLOCK;
        }

        return config;
    }

    public void save() {
        JsonObject object =
                new JsonObject();

        object.addProperty(
                "scaffold_block",
                scaffoldBlock
        );

        try {
            Files.createDirectories(
                    CONFIG_PATH.getParent()
            );

            Files.writeString(
                    CONFIG_PATH,
                    GSON.toJson(object)
            );
        } catch (IOException ignored) {
        }
    }

    public String getScaffoldBlockId() {
        return scaffoldBlock;
    }

    public void setScaffoldBlockId(String blockId) {
        Block block = getBlock(blockId);

        if (block == Blocks.AIR
                || block.asItem() == Items.AIR) {
            return;
        }

        scaffoldBlock = blockId;
    }

    public Block getScaffoldBlock() {
        return getBlock(scaffoldBlock);
    }

    public Item getScaffoldItem() {
        Item item =
                getScaffoldBlock().asItem();

        return item == Items.AIR
                ? Items.OAK_PLANKS
                : item;
    }

    private boolean hasValidScaffoldBlock() {
        Block block =
                getBlock(scaffoldBlock);

        return block != Blocks.AIR
                && block.asItem() != Items.AIR;
    }

    private static Block getBlock(String id) {
        if (id == null || id.isBlank()) {
            return Blocks.AIR;
        }

        try {
            Identifier identifier =
                    Identifier.parse(id);

            return BuiltInRegistries.BLOCK.getValue(
                    identifier
            );
        } catch (Exception ignored) {
            return Blocks.AIR;
        }
    }
}