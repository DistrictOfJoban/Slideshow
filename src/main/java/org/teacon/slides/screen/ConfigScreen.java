package org.teacon.slides.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;
import org.teacon.slides.config.Config;

public final class ConfigScreen extends Screen {
    private final Screen parent;

    private static final int SQUARE_SIZE = 20;
    private static final int TEXT_HEIGHT = 8;
    private static final int TEXT_PADDING = 6;
    private static final int TEXT_FIELD_PADDING = 4;
    private static final int ARGB_WHITE = 0xFFFFFFFF;
    private static final int MAX_HOST_LENGTH = 256;
    private static final int MAX_PORT_LENGTH = 5;
    private static final int BUTTON_WIDTH = 60;
    private static final int BUTTON_HEIGHT = TEXT_HEIGHT + TEXT_PADDING;

    private static final Component CONFIG_TEXT = Component.translatable("gui.slide_show.config");
    private static final Component PROXY_SWITCH = Component.translatable("gui.slide_show.config.proxy_switch");
    private static final Component PROXY_SWITCH_ON = Component.translatable("gui.slide_show.config.proxy_switch_on");
    private static final Component PROXY_SWITCH_OFF = Component.translatable("gui.slide_show.config.proxy_switch_off");
    private static final Component CONFIG_HOST = Component.translatable("gui.slide_show.config.host");
    private static final Component CONFIG_PORT = Component.translatable("gui.slide_show.config.port");
    private static final Component CONFIG_VIEW_DISTANCE = Component.translatable("gui.slide_show.config.view_distance");

    private boolean proxySwitch;
    private String host;
    private int port;
    private int viewDistance;
    private boolean isChanged;

    public ConfigScreen(Screen parent) {
        super(Component.literal(""));
        this.parent = parent;
        this.proxySwitch = Config.isProxySwitch();
        this.host = Config.getHost();
        this.port = Config.getPort();
        this.viewDistance = Config.getViewDistance();
        this.isChanged = false;
    }

    @Override
    protected void init() {
        final Button buttonProxySwitch = new Button(this.width - SQUARE_SIZE - BUTTON_WIDTH, (SQUARE_SIZE + TEXT_FIELD_PADDING) + SQUARE_SIZE, BUTTON_WIDTH, BUTTON_HEIGHT, Config.isProxySwitch() ? PROXY_SWITCH_ON : PROXY_SWITCH_OFF, button -> {
            this.proxySwitch = !this.proxySwitch;
            button.setMessage(this.proxySwitch ? PROXY_SWITCH_ON : PROXY_SWITCH_OFF);
            this.isChanged = true;
        });

        final EditBox textFieldHost = new EditBox(this.font, width - (SQUARE_SIZE * 10) - BUTTON_WIDTH, (SQUARE_SIZE + TEXT_FIELD_PADDING) * 2 + SQUARE_SIZE, BUTTON_WIDTH - TEXT_PADDING + (SQUARE_SIZE * 9), SQUARE_SIZE, CONFIG_HOST);
        textFieldHost.setValue(Config.getHost());
        textFieldHost.setResponder(text -> {
            if (StringUtils.isEmpty(text)) {
                textFieldHost.setSuggestion("localhost");
            } else {
                textFieldHost.setSuggestion("");
            }
            if (StringUtils.isNotBlank(text) && text.length() <= MAX_HOST_LENGTH) {
                this.host = text;
                textFieldHost.setTextColor(0XFFFFFF);
                this.isChanged = true;
            } else {
                textFieldHost.setTextColor(0XFF0000);
            }
        });

        final EditBox textFieldPort = new EditBox(this.font, width - (SQUARE_SIZE * 10) - BUTTON_WIDTH, (SQUARE_SIZE + TEXT_FIELD_PADDING) * 3 + SQUARE_SIZE, BUTTON_WIDTH - TEXT_PADDING + (SQUARE_SIZE * 9), SQUARE_SIZE, CONFIG_PORT);
        textFieldPort.setValue(String.valueOf(Config.getPort()));
        textFieldPort.setResponder(text -> {
            if (StringUtils.isEmpty(text)) {
                textFieldPort.setSuggestion("8080");
            } else {
                textFieldPort.setSuggestion("");
            }
            try {
                if (text.length() >= 4 && text.length() <= MAX_PORT_LENGTH) {
                    final int temp = Integer.parseInt(text);
                    if (temp >= 1024 && temp <= 65535) {
                        this.port = temp;
                        textFieldPort.setTextColor(0XFFFFFF);
                        this.isChanged = true;
                    } else {
                        textFieldPort.setTextColor(0xFF0000);
                    }
                } else {
                    textFieldPort.setTextColor(0xFF0000);
                }
            } catch (Exception ignored) {
                textFieldPort.setTextColor(0xFF0000);
            }
        });

        final EditBox sliderViewDistance = new EditBox(this.font, width - (SQUARE_SIZE * 10) - BUTTON_WIDTH, (SQUARE_SIZE + TEXT_FIELD_PADDING) * 4 + SQUARE_SIZE, BUTTON_WIDTH - TEXT_PADDING + (SQUARE_SIZE * 9), SQUARE_SIZE, CONFIG_VIEW_DISTANCE);
        sliderViewDistance.setValue(String.valueOf(Config.getViewDistance()));
        sliderViewDistance.setResponder(text -> {
            if (StringUtils.isEmpty(text)) {
                sliderViewDistance.setSuggestion("256");
            } else {
                sliderViewDistance.setSuggestion("");
            }
            try {
                if (text.length() <= 3) {
                    final int temp = Integer.parseInt(text);
                    if (temp > 0 && temp <= Config.MAX_VIEW_DISTANCE) {
                        this.viewDistance = temp;
                        sliderViewDistance.setTextColor(0XFFFFFF);
                        this.isChanged = true;
                    } else {
                        sliderViewDistance.setTextColor(0xFF0000);
                    }
                } else {
                    sliderViewDistance.setTextColor(0xFF0000);
                }
            } catch (Exception e) {
                sliderViewDistance.setTextColor(0xFF0000);
            }
        });

        addRenderableWidget(buttonProxySwitch);
        addRenderableWidget(textFieldHost);
        addRenderableWidget(textFieldPort);
        addRenderableWidget(sliderViewDistance);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);
        this.font.draw(poseStack, CONFIG_TEXT.getString(), (this.width - this.font.width(CONFIG_TEXT)) / 2.0F, TEXT_PADDING, ARGB_WHITE);
        this.font.draw(poseStack, PROXY_SWITCH.getString(), SQUARE_SIZE, (SQUARE_SIZE + TEXT_FIELD_PADDING) + SQUARE_SIZE + TEXT_PADDING, ARGB_WHITE);
        this.font.draw(poseStack, CONFIG_HOST.getString(), SQUARE_SIZE, (SQUARE_SIZE + TEXT_FIELD_PADDING) * 2 + SQUARE_SIZE + TEXT_PADDING, ARGB_WHITE);
        this.font.draw(poseStack, CONFIG_PORT.getString(), SQUARE_SIZE, (SQUARE_SIZE + TEXT_FIELD_PADDING) * 3 + SQUARE_SIZE + TEXT_PADDING, ARGB_WHITE);
        this.font.draw(poseStack, CONFIG_VIEW_DISTANCE.getString(), SQUARE_SIZE, (SQUARE_SIZE + TEXT_FIELD_PADDING) * 4 + SQUARE_SIZE + TEXT_PADDING, ARGB_WHITE);
        super.render(poseStack, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(parent);
            if (this.isChanged) {
                Config.setProxySwitch(this.proxySwitch);
                Config.setHost(this.host);
                Config.setPort(this.port);
                Config.setViewDistance(this.viewDistance);
                Config.saveToFile();
            }
            Config.refreshProperties();
        }
    }
}