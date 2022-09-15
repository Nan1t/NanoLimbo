package com.ubivashka.limbo.command.exception;

import net.kyori.adventure.text.Component;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.velocity.VelocityCommandActor;

public class SendComponentException extends RuntimeException {
    private final Component component;

    public SendComponentException(Component component) {
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

    public void send(CommandActor actor) {
        actor.as(VelocityCommandActor.class).reply(component);
    }
}
