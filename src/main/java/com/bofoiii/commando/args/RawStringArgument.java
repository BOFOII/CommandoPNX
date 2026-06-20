package com.bofoiii.commando.args;

import cn.nukkit.command.CommandSender;

public class RawStringArgument extends BaseArgument {
    public RawStringArgument(String name, boolean optional) {
        super(name, optional);
    }

    @Override
    public String getTypeName() {
        return "string";
    }

    @Override
    public boolean canParse(String testString, CommandSender sender) {
        return true;
    }

    @Override
    public String parse(String argument, CommandSender sender) {
        return argument;
    }
}
