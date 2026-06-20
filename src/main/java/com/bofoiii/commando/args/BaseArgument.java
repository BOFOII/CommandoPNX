package com.bofoiii.commando.args;

import cn.nukkit.command.CommandSender;

public abstract class BaseArgument {
    protected final String name;
    protected final boolean optional;

    public BaseArgument(String name, boolean optional) {
        this.name = name;
        this.optional = optional;
    }

    public abstract boolean canParse(String testString, CommandSender sender);

    public abstract Object parse(String argument, CommandSender sender);

    public String getName() {
        return name;
    }

    public boolean isOptional() {
        return optional;
    }

    public int getSpanLength() {
        return 1;
    }

    public abstract String getTypeName();
}
