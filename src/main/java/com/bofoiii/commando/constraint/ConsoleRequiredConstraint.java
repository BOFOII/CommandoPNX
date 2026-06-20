package com.bofoiii.commando.constraint;

import cn.nukkit.command.CommandSender;
import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;

import java.util.Map;

public class ConsoleRequiredConstraint extends BaseConstraint {
    public ConsoleRequiredConstraint(Object context) {
        super(context);
    }

    @Override
    public boolean test(CommandSender sender, String aliasUsed, Map<String, Object> args) {
        return isVisibleTo(sender);
    }

    @Override
    public void onFailure(CommandSender sender, String aliasUsed, Map<String, Object> args) {
        sender.sendMessage(TextFormat.RED + "This command must be executed from a server console.");
    }

    @Override
    public boolean isVisibleTo(CommandSender sender) {
        return !(sender instanceof Player);
    }
}
