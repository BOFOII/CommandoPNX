package com.bofoiii.commando;

import com.bofoiii.commando.args.ArgumentParser;
import com.bofoiii.commando.args.BaseArgument;
import com.bofoiii.commando.constraint.BaseConstraint;
import cn.nukkit.command.CommandSender;

import java.util.*;

public abstract class BaseSubCommand {
    private final String name;
    private final String[] aliases;
    private final String description;
    protected String usageMessage;
    private final List<String> permissions = new ArrayList<>();
    protected CommandSender currentSender;
    protected BaseCommand parent;
    private final List<BaseConstraint> constraints = new ArrayList<>();
    private final ArgumentParser parser = new ArgumentParser();

    public BaseSubCommand(String name, String description, String[] aliases) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
        this.prepare();
        this.usageMessage = parser.generateUsageMessage(name);
    }

    protected abstract void prepare();

    public abstract void onRun(CommandSender sender, String aliasUsed, Map<String, Object> args);

    public void registerArgument(int position, BaseArgument argument) {
        parser.registerArgument(position, argument);
    }

    public Map<String, Object>[] parseArguments(String[] rawArgs, CommandSender sender) {
        return parser.parseArguments(rawArgs, sender);
    }

    public List<BaseArgument>[] getArgumentList() {
        return parser.getArgumentList();
    }

    public String getName() { return name; }
    public String[] getAliases() { return aliases; }
    public String getDescription() { return description; }
    public String getUsageMessage() { return usageMessage; }
    public List<String> getPermissions() { return permissions; }

    public void setPermissions(String[] permissions) {
        this.permissions.clear();
        Collections.addAll(this.permissions, permissions);
    }

    public void setPermission(String permission) {
        setPermissions(permission == null ? new String[0] : permission.split(";"));
    }

    public boolean testPermissionSilent(CommandSender sender) {
        if (permissions.isEmpty()) return true;
        for (String perm : permissions) {
            if (sender.hasPermission(perm)) return true;
        }
        return false;
    }

    public void setCurrentSender(CommandSender sender) { this.currentSender = sender; }
    public void setParent(BaseCommand parent) { this.parent = parent; }

    public void sendError(int errorCode, Object... args) {
        if (parent != null) parent.sendError(errorCode, args);
    }

    public void sendUsage() {
        currentSender.sendMessage("/" + parent.getName() + " " + usageMessage);
    }

    public void addConstraint(BaseConstraint constraint) { constraints.add(constraint); }
    public List<BaseConstraint> getConstraints() { return constraints; }
}
