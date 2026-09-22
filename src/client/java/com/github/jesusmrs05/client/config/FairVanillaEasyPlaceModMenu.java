package com.github.jesusmrs05.client.config;

import com.github.jesusmrs05.client.FairVanillaEasyPlaceClient;
import com.github.jesusmrs05.client.config.FairVanillaEasyPlaceConfig;
import com.github.jesusmrs05.client.config.FairVanillaEasyPlaceConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public final class FairVanillaEasyPlaceModMenu
        implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return previousScreen ->
                new FairVanillaEasyPlaceConfigScreen(
                        previousScreen,
                        FairVanillaEasyPlaceClient.getConfig()
                );
    }
}