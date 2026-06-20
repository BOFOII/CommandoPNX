package com.bofoiii.commando.args;

import cn.nukkit.command.CommandSender;

import java.util.Map;

public abstract class StringEnumArgument extends BaseArgument {
    public StringEnumArgument(String name, boolean optional) {
        super(name, optional);
    }

    @Override
    public boolean canParse(String testString, CommandSender sender) {
        String lower = testString.toLowerCase();
        for (String key : getEnumValues()) {
            if (key.equalsIgnoreCase(lower)) {
                return true;
            }
        }
        return false;
    }

    public Object getValue(String string) {
        return getValues().get(string.toLowerCase());
    }

    public abstract Map<String, Object> getValues();

    public String[] getEnumValues() {
        return getValues().keySet().toArray(new String[0]);
    }
}
