package io.github.sree.core;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import io.github.sree.core.spectators.SpectatorService;
import io.github.sree.core.spectators.SpectatorVoiceChat;

public class SreeVoiceChatPlugin implements VoicechatPlugin {

    private final SpectatorService spectatorService;

    public SreeVoiceChatPlugin(SpectatorService spectatorService) {
        this.spectatorService = spectatorService;
    }

    @Override
    public String getPluginId() {
        return "sree-core";
    }

    @Override
    public void initialize(VoicechatApi api) {
        VoicechatServerApi serverApi = (VoicechatServerApi) api;
        SpectatorVoiceChat spectatorVoiceChat = new SpectatorVoiceChat(serverApi);

        spectatorService.setVoiceChat(spectatorVoiceChat);
    }
}
