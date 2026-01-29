package au.ellie.hyui.commands;

import au.ellie.hyui.HyUIPluginLogger;
import au.ellie.hyui.builders.ButtonBuilder;
import au.ellie.hyui.builders.PageBuilder;
import au.ellie.hyui.builders.TabNavigationBuilder;
import au.ellie.hyui.html.TemplateProcessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.CompletableFuture;

public class HyUITabsCommand extends AbstractAsyncCommand {

    public HyUITabsCommand() {
        super("tabs", "Opens the HyUI tabs tutorial demo");
        if (!HyUIPluginLogger.IS_DEV) {
            return;
        }
        this.setPermissionGroup(GameMode.Adventure);
    }

    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
        if (!HyUIPluginLogger.IS_DEV) {
            return CompletableFuture.completedFuture(null);
        }
        var sender = commandContext.sender();
        if (sender instanceof Player player) {
            Ref<EntityStore> ref = player.getReference();
            if (ref != null && ref.isValid()) {
                Store<EntityStore> store = ref.getStore();
                World world = store.getExternalData().getWorld();
                return CompletableFuture.runAsync(() -> {
                    PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
                    if (playerRef != null) {
                        openTabsDemo(playerRef, store);
                    }
                }, world);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    private void openTabsDemo(PlayerRef playerRef, Store<EntityStore> store) {
        if (!HyUIPluginLogger.IS_DEV) {
            return;
        }

        String html = """
            <div class="page-overlay">
                <div class="decorated-container" data-hyui-title="Workshop Tabs" style="anchor-width: 720; anchor-height: 480;">
                    <div class="container-contents" style="layout-mode: Top; padding: 6;">
                        <nav id="workshop-tabs" class="tabs"
                             data-tabs="blueprints:Blueprints:blueprints-content,materials:Materials:materials-content{{#if isAdmin}},tools:Tools:tools-content{{/if}}"
                             data-selected="blueprints">
                        </nav>

                        <div id="blueprints-content" class="tab-content" data-hyui-tab-id="blueprints">
                            <p>Blueprint drafts live here.</p>
                        </div>

                        <div id="materials-content" class="tab-content" data-hyui-tab-id="materials">
                            <p>Material stacks and salvage.</p>
                        </div>

                        {{#if isAdmin}}
                        <div id="tools-content" class="tab-content" data-hyui-tab-id="tools">
                            <p>Workbench tools and kits.</p>
                        </div>
                        {{/if}}

                        <button id="upgrade-tabs" class="secondary-button">Upgrade Materials Tab</button>
                    </div>
                </div>
            </div>
            """;

        TemplateProcessor template = new TemplateProcessor()
                .setVariable("isAdmin", false);

        PageBuilder builder = PageBuilder.pageForPlayer(playerRef)
                .fromTemplate(html, template)
                .enableRuntimeTemplateUpdates(true)
                .withLifetime(CustomPageLifetime.CanDismiss);

        builder.addEventListener("upgrade-tabs", CustomUIEventBindingType.Activating, (data, ctx) -> {
            ctx.getById("workshop-tabs", TabNavigationBuilder.class).ifPresent(nav -> {
                TabNavigationBuilder.Tab existing = nav.getTab("materials");
                if (existing == null) {
                    return;
                }

                ButtonBuilder customButton = ButtonBuilder.smallTertiaryTextButton();
                TabNavigationBuilder.Tab updated = new TabNavigationBuilder.Tab(
                        existing.id(),
                        "Materials+",
                        existing.contentId(),
                        existing.selected(),
                        customButton
                );

                nav.updateTab("materials", updated);
            });

            ctx.updatePage(true);
        });

        builder.open(store);
    }
}
