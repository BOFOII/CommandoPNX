package com.bofoiii.commando.args;

import cn.nukkit.command.CommandSender;

public class TextArgument extends RawStringArgument {
    public TextArgument(String name, boolean optional) {
        super(name, optional);
    }

    @Override
    public String getTypeName() {
        return "text";
    }

    @Override
    public int getSpanLength() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canParse(String testString, CommandSender sender) {
        return !testString.isEmpty();
    }
}
