package com.bofoiii.commando.args;

import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.math.Vector3;

public class Vector3Argument extends BaseArgument {
    public Vector3Argument(String name, boolean optional) {
        super(name, optional);
    }

    @Override
    public String getTypeName() {
        return "x y z";
    }

    @Override
    public boolean canParse(String testString, CommandSender sender) {
        String[] coords = testString.split(" ");
        if (coords.length == 3) {
            for (String coord : coords) {
                if (!isValidCoordinate(coord, sender instanceof Entity)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean isValidCoordinate(String coordinate, boolean locatable) {
        String pattern = "^(?:" + (locatable ? "(?:~-|~\\+)?" : "") + "-?\\d+(?:\\.\\d+)?)" + (locatable ? "|~" : "") + "$";
        return coordinate.matches(pattern);
    }

    @Override
    public Vector3 parse(String argument, CommandSender sender) {
        String[] coords = argument.split(" ");
        double[] vals = new double[3];
        for (int k = 0; k < coords.length; k++) {
            String coord = coords[k];
            double offset = 0;
            if (sender instanceof Entity && (coord.startsWith("~-") || coord.startsWith("~+") || coord.equals("~"))) {
                offset = Double.parseDouble(coord.substring(1));
                Vector3 position = ((Entity) sender).getPosition();
                coord = switch (k) {
                    case 0 -> String.valueOf(position.getX());
                    case 1 -> String.valueOf(position.getY());
                    case 2 -> String.valueOf(position.getZ());
                    default -> coord;
                };
            }
            vals[k] = Double.parseDouble(coord) + offset;
        }
        return new Vector3(vals[0], vals[1], vals[2]);
    }

    @Override
    public int getSpanLength() {
        return 3;
    }
}
