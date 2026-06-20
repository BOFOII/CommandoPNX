package com.bofoiii.commando.args;

import cn.nukkit.command.CommandSender;
import cn.nukkit.math.Vector3;

public class BlockPositionArgument extends Vector3Argument {
    public BlockPositionArgument(String name, boolean optional) {
        super(name, optional);
    }

    @Override
    public boolean isValidCoordinate(String coordinate, boolean locatable) {
        String pattern = "^(?:" + (locatable ? "(?:~-|~\\+)?" : "") + "-?\\d+)" + (locatable ? "|~" : "") + "$";
        return coordinate.matches(pattern);
    }

    @Override
    public Vector3 parse(String argument, CommandSender sender) {
        Vector3 v = super.parse(argument, sender);
        return v.floor();
    }
}
