package com.github.jesusmrs05.client.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public final class FairVanillaEasyPlaceConfigScreen extends Screen {

    private static final int SCREEN_WIDTH = 360;
    private static final int LIST_HEIGHT = 180;
    private static final int MAX_SUGGESTIONS = 40;

    private final Screen previousScreen;
    private final FairVanillaEasyPlaceConfig config;

    private EditBox scaffoldSearchBox;
    private BlockSuggestionList suggestionList;

    public FairVanillaEasyPlaceConfigScreen(
            Screen previousScreen,
            FairVanillaEasyPlaceConfig config
    ) {
        super(
                Component.literal(
                        "Fair Vanilla Easy Place"
                )
        );

        this.previousScreen = previousScreen;
        this.config = config;
    }

    @Override
    protected void init() {
        super.init();

        int left = (width - SCREEN_WIDTH) / 2;

        scaffoldSearchBox = new EditBox(
                font,
                left,
                70,
                SCREEN_WIDTH,
                20,
                Component.literal("Scaffold Block")
        );

        scaffoldSearchBox.setValue(
                config.getScaffoldBlockId()
        );

        scaffoldSearchBox.setMaxLength(128);

        suggestionList = new BlockSuggestionList(
                minecraft,
                SCREEN_WIDTH,
                LIST_HEIGHT,
                100,
                20,
                this::selectScaffoldBlock
        );

        suggestionList.setX(left);

        scaffoldSearchBox.setResponder(
                suggestionList::updateSuggestions
        );

        addRenderableWidget(
                scaffoldSearchBox
        );

        addRenderableWidget(
                suggestionList
        );

        Button doneButton = Button.builder(
                Component.literal("Done"),
                button -> onClose()
        ).bounds(
                left,
                height - 35,
                SCREEN_WIDTH,
                20
        ).build();

        addRenderableWidget(
                doneButton
        );

        suggestionList.updateSuggestions(
                scaffoldSearchBox.getValue()
        );

        setInitialFocus(scaffoldSearchBox);
    }

    private void selectScaffoldBlock(Block block) {
        String blockId = getBlockId(block);

        if (blockId == null) {
            return;
        }

        config.setScaffoldBlockId(blockId);

        scaffoldSearchBox.setValue(blockId);
        scaffoldSearchBox.setFocused(true);

        suggestionList.updateSuggestions(blockId);
    }

    private static String getBlockId(Block block) {
        if (block == null) {
            return null;
        }

        Identifier id =
                BuiltInRegistries.BLOCK.getKey(block);

        return id == null
                ? null
                : id.toString();
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        super.extractRenderState(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );

        int left = (width - SCREEN_WIDTH) / 2;

        graphics.text(
                font,
                Component.literal("Scaffold Block"),
                left,
                45,
                0xFFFFFFFF,
                true
        );

        graphics.text(
                font,
                Component.literal(
                        "Search for the block used as scaffolding."
                ),
                left,
                55,
                0xFFAAAAAA,
                false
        );
    }

    @Override
    public void onClose() {
        config.save();

        if (minecraft != null) {
            minecraft.gui.setScreen(
                    previousScreen
            );
        }
    }

    private static final class BlockSuggestionList
            extends ObjectSelectionList<BlockSuggestionList.Entry> {

        private final Consumer<Block> onSelect;

        private BlockSuggestionList(
                Minecraft minecraft,
                int width,
                int height,
                int y,
                int itemHeight,
                Consumer<Block> onSelect
        ) {
            super(
                    minecraft,
                    width,
                    height,
                    y,
                    itemHeight
            );

            this.onSelect = onSelect;
        }

        private void updateSuggestions(String query) {
            clearEntries();

            String search =
                    query == null
                            ? ""
                            : query.trim().toLowerCase(
                            Locale.ROOT
                    );

            List<BlockMatch> matches =
                    new ArrayList<>();

            for (Block block : BuiltInRegistries.BLOCK) {
                Item item = block.asItem();

                if (item == Items.AIR) {
                    continue;
                }

                Identifier id =
                        BuiltInRegistries.BLOCK.getKey(block);

                if (id == null) {
                    continue;
                }

                String idString =
                        id.toString().toLowerCase(
                                Locale.ROOT
                        );

                if (!search.isEmpty()
                        && !idString.contains(search)) {
                    continue;
                }

                int priority;

                if (idString.equals(search)) {
                    priority = 0;
                } else if (idString.startsWith(search)) {
                    priority = 1;
                } else {
                    priority = 2;
                }

                matches.add(
                        new BlockMatch(
                                block,
                                idString,
                                priority
                        )
                );
            }

            matches.sort(
                    Comparator
                            .comparingInt(
                                    BlockMatch::priority
                            )
                            .thenComparing(
                                    BlockMatch::id
                            )
            );

            int count =
                    Math.min(
                            MAX_SUGGESTIONS,
                            matches.size()
                    );

            for (int i = 0; i < count; i++) {
                addEntry(
                        new Entry(
                                matches.get(i).block()
                        )
                );
            }
        }

        private record BlockMatch(
                Block block,
                String id,
                int priority
        ) {
        }

        private final class Entry
                extends ObjectSelectionList.Entry<Entry> {

            private final Block block;

            private Entry(Block block) {
                this.block = block;
            }

            @Override
            public void extractContent(
                    GuiGraphicsExtractor graphics,
                    int mouseX,
                    int mouseY,
                    boolean hovered,
                    float partialTick
            ) {
                Identifier id =
                        BuiltInRegistries.BLOCK.getKey(block);

                graphics.text(
                        minecraft.font,
                        Component.literal(
                                id.toString()
                        ),
                        getContentX(),
                        getContentY() + 5,
                        hovered
                                ? 0xFFFFFFFF
                                : 0xFFCCCCCC,
                        false
                );
            }

            @Override
            public boolean mouseClicked(
                    MouseButtonEvent event,
                    boolean doubleClick
            ) {
                if (event.button() == 0) {
                    onSelect.accept(block);
                    return true;
                }

                return super.mouseClicked(
                        event,
                        doubleClick
                );
            }

            @Override
            public Component getNarration() {
                Identifier id =
                        BuiltInRegistries.BLOCK.getKey(block);

                return Component.literal(
                        id.toString()
                );
            }
        }
    }
}