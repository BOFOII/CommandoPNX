package com.bofoiii.commando;

import com.bofoiii.commando.args.ArgumentParser;
import com.bofoiii.commando.args.BaseArgument;
import com.bofoiii.commando.constraint.BaseConstraint;
import com.bofoiii.commando.exception.InvalidErrorCode;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.TextFormat;

import java.util.*;

public abstract class BaseCommand extends Command {
    public static final int ERR_INVALID_ARG_VALUE = 0x01;
    public static final int ERR_TOO_MANY_ARGUMENTS = 0x02;
    public static final int ERR_INSUFFICIENT_ARGUMENTS = 0x03;
    public static final int ERR_NO_ARGUMENTS = 0x04;

    protected final Map<Integer, String> errorMessages = new LinkedHashMap<>();
    {
        errorMessages.put(ERR_INVALID_ARG_VALUE, TextFormat.RED + "Invalid value '{value}' for argument #{position}");
        errorMessages.put(ERR_TOO_MANY_ARGUMENTS, TextFormat.RED + "Too many arguments given");
        errorMessages.put(ERR_INSUFFICIENT_ARGUMENTS, TextFormat.RED + "Insufficient number of arguments given");
        errorMessages.put(ERR_NO_ARGUMENTS, TextFormat.RED + "No arguments are required for this command");
    }

    protected CommandSender currentSender;
    private final Map<String, BaseSubCommand> subCommands = new LinkedHashMap<>();
    private final List<BaseConstraint> constraints = new ArrayList<>();
    private final Plugin plugin;
    private final ArgumentParser parser = new ArgumentParser();

    public BaseCommand(Plugin plugin, String name, String description, String[] aliases) {
        super(name, description, null, aliases);
        this.plugin = plugin;
        this.prepare();

        Set<String> usages = new LinkedHashSet<>();
        usages.add("/" + parser.generateUsageMessage(name));
        for (BaseSubCommand sub : subCommands.values()) {
            usages.add(sub.getUsageMessage());
        }
        this.setUsage(String.join("\n - /" + name + " ", usages));
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

    public String generateUsageMessage() {
        return parser.generateUsageMessage(this.getName());
    }

    public boolean hasArguments() { return parser.hasArguments(); }
    public boolean hasRequiredArguments() { return parser.hasRequiredArguments(); }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        this.currentSender = sender;
        if (!this.testPermission(sender)) return true;

        Map<String, Object> passArgs = new LinkedHashMap<>();
        List<BaseConstraint> activeConstraints = constraints;

        if (args.length > 0) {
            String label = args[0];
            if (subCommands.containsKey(label)) {
                BaseSubCommand sub = subCommands.get(label);
                sub.setCurrentSender(sender);
                if (!sub.testPermissionSilent(sender)) {
                    String msg = this.getPermissionMessage();
                    if (msg == null) {
                        sender.sendMessage(TextFormat.RED + "%commands.generic.permission");
                    } else if (!msg.isEmpty()) {
                        sender.sendMessage(msg.replace("<permission>", String.join(";", sub.getPermissions())));
                    }
                    return true;
                }
                args = Arrays.copyOfRange(args, 1, args.length);
                activeConstraints = sub.getConstraints();

                Map<String, Object>[] parsed = sub.parseArguments(args, sender);
                if (parsed[1] != null && !((Map<?, ?>) parsed[1]).isEmpty()) {
                    sendParseErrors(parsed[1]);
                    return true;
                }
                passArgs = parsed[0];

                for (BaseConstraint constraint : activeConstraints) {
                    if (!constraint.test(sender, commandLabel, passArgs)) {
                        constraint.onFailure(sender, commandLabel, passArgs);
                        return true;
                    }
                }
                sub.onRun(sender, commandLabel, passArgs);
                return true;
            }

            Map<String, Object>[] parsed = parseArguments(args, sender);
            if (parsed[1] != null && !((Map<?, ?>) parsed[1]).isEmpty()) {
                sendParseErrors(parsed[1]);
                return true;
            }
            passArgs = parsed[0];
        } else if (hasRequiredArguments()) {
            sendError(ERR_INSUFFICIENT_ARGUMENTS);
            return true;
        }

        for (BaseConstraint constraint : activeConstraints) {
            if (!constraint.test(sender, commandLabel, passArgs)) {
                constraint.onFailure(sender, commandLabel, passArgs);
                return true;
            }
        }
        onRun(sender, commandLabel, passArgs);
        return true;
    }

    private void sendParseErrors(Map<String, Object> errors) {
        Object codeObj = errors.get("code");
        if (codeObj instanceof Integer code) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) errors.getOrDefault("data", Map.of());
            sendError(code, data);
        }
    }

    public void sendError(int errorCode, Object... args) {
        String str = errorMessages.get(errorCode);
        if (str == null) return;
        for (Object arg : args) {
            if (arg instanceof Map<?, ?> map) {
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    str = str.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
                }
            }
        }
        currentSender.sendMessage(str);
    }

    public void setErrorFormat(int errorCode, String format) {
        if (!errorMessages.containsKey(errorCode)) {
            throw new InvalidErrorCode("Invalid error code 0x" + Integer.toHexString(errorCode));
        }
        errorMessages.put(errorCode, format);
    }

    public void setErrorFormats(Map<Integer, String> errorFormats) {
        errorFormats.forEach(this::setErrorFormat);
    }

    public void registerSubCommand(BaseSubCommand subCommand) {
        List<String> keys = new ArrayList<>(Arrays.asList(subCommand.getAliases()));
        keys.add(0, subCommand.getName());
        keys = new ArrayList<>(new LinkedHashSet<>(keys));
        for (String key : keys) {
            if (subCommands.containsKey(key)) {
                throw new IllegalArgumentException("SubCommand with same name / alias for '" + key + "' already exists");
            }
            subCommand.setParent(this);
            subCommands.put(key, subCommand);
        }
    }

    public void addConstraint(BaseConstraint constraint) { constraints.add(constraint); }
    public List<BaseConstraint> getConstraints() { return constraints; }
    public Map<String, BaseSubCommand> getSubCommands() { return subCommands; }
    public Plugin getOwningPlugin() { return plugin; }
}
