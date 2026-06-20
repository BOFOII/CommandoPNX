package com.bofoiii.commando.args;

import cn.nukkit.command.CommandSender;

import java.util.Map;

public class BooleanArgument extends StringEnumArgument {
    private static final Map<String, Boolean> BOOL_VALUES = Map.of(
        "true", true,
        "false", false
    );

    public BooleanArgument(String name, boolean optional) {
        super(name, optional);
    }

    @Override
    public String getTypeName() {
        return "bool";
    }

    @Override
    public Boolean parse(String argument, CommandSender sender) {
        return BOOL_VALUES.get(argument.toLowerCase());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getValues() {
        return (Map<String, Object>) (Map<?, ?>) BOOL_VALUES;
    }
}
