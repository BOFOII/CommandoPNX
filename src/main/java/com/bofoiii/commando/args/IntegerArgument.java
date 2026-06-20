package com.bofoiii.commando.args;

import cn.nukkit.command.CommandSender;

public class IntegerArgument extends BaseArgument {
    public IntegerArgument(String name, boolean optional) {
        super(name, optional);
    }

    @Override
    public String getTypeName() {
        return "int";
    }

    @Override
    public boolean canParse(String testString, CommandSender sender) {
        return testString.matches("^-?\\d+$");
    }

    @Override
    public Integer parse(String argument, CommandSender sender) {
        return Integer.parseInt(argument);
    }
}
