package com.bofoiii.commando.constraint;

import cn.nukkit.command.CommandSender;

import java.util.Map;

public abstract class BaseConstraint {
    protected final Object context;

    public BaseConstraint(Object context) {
        this.context = context;
    }

    public Object getContext() {
        return context;
    }

    public abstract boolean test(CommandSender sender, String aliasUsed, Map<String, Object> args);

    public abstract void onFailure(CommandSender sender, String aliasUsed, Map<String, Object> args);

    public abstract boolean isVisibleTo(CommandSender sender);
}
