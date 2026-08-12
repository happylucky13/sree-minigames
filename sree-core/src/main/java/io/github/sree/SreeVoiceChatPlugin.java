package io.github.sree;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;

public class SreeVoiceChatPlugin implements VoicechatPlugin {

    private VoicechatServerApi api;

    @Override
    public String getPluginId() {
        return "sree-core";
    }

    @Override
    public void initialize(VoicechatApi api) {
        this.api = (VoicechatServerApi) api;
    }

    public VoicechatServerApi getApi() {
        return api;
    }
}
