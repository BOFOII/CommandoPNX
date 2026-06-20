package com.bofoiii.commando.args;

import cn.nukkit.command.CommandSender;

public class FloatArgument extends BaseArgument {
    public FloatArgument(String name, boolean optional) {
        super(name, optional);
    }

    @Override
    public String getTypeName() {
        return "decimal";
    }

    @Override
    public boolean canParse(String testString, CommandSender sender) {
        return testString.matches("^-?(?:\\d+|\\d*\\.\\d+)$");
    }

    @Override
    public Float parse(String argument, CommandSender sender) {
        return Float.parseFloat(argument);
    }
}
