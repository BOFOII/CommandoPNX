package com.bofoiii.commando.args;

import com.bofoiii.commando.exception.ArgumentOrderException;
import cn.nukkit.command.CommandSender;

import java.util.*;

public class ArgumentParser {
    private List<BaseArgument>[] argumentList = new List[0];
    private boolean[] requiredArgumentCount = new boolean[0];

    public void registerArgument(int position, BaseArgument argument) {
        if (position < 0) {
            throw new ArgumentOrderException("You cannot register arguments at negative positions");
        }
        if (position > 0 && argumentList[position - 1] == null) {
            throw new ArgumentOrderException("There were no arguments before " + position);
        }
        if (position >= argumentList.length) {
            argumentList = Arrays.copyOf(argumentList, position + 1);
            requiredArgumentCount = Arrays.copyOf(requiredArgumentCount, position + 1);
        }
        if (argumentList[position] == null) {
            argumentList[position] = new ArrayList<>();
        }
        for (BaseArgument arg : argumentList[Math.max(0, position - 1)]) {
            if (arg instanceof TextArgument) {
                throw new ArgumentOrderException("No other arguments can be registered after a TextArgument");
            }
            if (arg.isOptional() && !argument.isOptional()) {
                throw new ArgumentOrderException("You cannot register a required argument after an optional argument");
            }
        }
        argumentList[position].add(argument);
        if (!argument.isOptional()) {
            requiredArgumentCount[position] = true;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object>[] parseArguments(String[] rawArgs, CommandSender sender) {
        Map<String, Object>[] result = new Map[]{new LinkedHashMap<>(), new LinkedHashMap<>()};
        List<Map<String, Object>> arguments = new ArrayList<>();
        List<Map<String, Object>> errors = new ArrayList<>();

        int required = 0;
        for (boolean b : requiredArgumentCount) {
            if (b) required++;
        }

        if (!hasArguments() && rawArgs.length > 0) {
            errors.add(Map.of("code", 0x04, "data", Map.of()));
        }

        int offset = 0;
        if (rawArgs.length > 0) {
            for (int pos = 0; pos < argumentList.length; pos++) {
                if (argumentList[pos] == null) continue;
                List<BaseArgument> possibleArguments = new ArrayList<>(argumentList[pos]);
                possibleArguments.sort(Comparator.comparingInt(a ->
                    a.getSpanLength() == Integer.MAX_VALUE ? 1 : -1
                ));

                boolean parsed = false;
                boolean optional = true;
                String arg = "";
                for (BaseArgument argument : possibleArguments) {
                    int len = argument.getSpanLength();
                    arg = String.join(" ", Arrays.copyOfRange(rawArgs, offset, Math.min(offset + len, rawArgs.length))).trim();
                    if (!argument.isOptional()) {
                        optional = false;
                    }
                    if (!arg.isEmpty() && argument.canParse(arg, sender)) {
                        arguments.add(Map.of(argument.getName(), argument.parse(arg, sender)));
                        if (!optional) required--;
                        offset += len;
                        parsed = true;
                        break;
                    }
                    if (offset > rawArgs.length) break;
                }
                if (!parsed && !(optional && arg.isEmpty())) {
                    errors.add(Map.of(
                        "code", 0x01,
                        "data", Map.of("value", offset < rawArgs.length ? rawArgs[offset] : "", "position", pos + 1)
                    ));
                    result[0] = arguments.isEmpty() ? new LinkedHashMap<>() : arguments.get(0);
                    result[1] = errors.isEmpty() ? new LinkedHashMap<>() : errors.get(0);
                    return result;
                }
            }
        }

        if (offset < rawArgs.length) errors.add(Map.of("code", 0x02, "data", Map.of()));
        if (required > 0) errors.add(Map.of("code", 0x03, "data", Map.of()));

        Map<String, Object> argsMap = new LinkedHashMap<>();
        for (Map<String, Object> m : arguments) argsMap.putAll(m);
        result[0] = argsMap;

        Map<String, Object> errMap = new LinkedHashMap<>();
        for (Map<String, Object> m : errors) errMap.putAll(m);
        result[1] = errMap;
        return result;
    }

    public String generateUsageMessage(String commandName) {
        StringBuilder msg = new StringBuilder(commandName + " ");
        List<String> args = new ArrayList<>();
        for (List<BaseArgument> list : argumentList) {
            if (list == null) continue;
            boolean hasOptional = false;
            List<String> names = new ArrayList<>();
            for (BaseArgument arg : list) {
                names.add(arg.getName() + ":" + arg.getTypeName());
                if (arg.isOptional()) hasOptional = true;
            }
            String joined = String.join("|", names);
            args.add(hasOptional ? "[" + joined + "]" : "<" + joined + ">");
        }
        msg.append(String.join(" ", args));
        return msg.toString();
    }

    public boolean hasArguments() {
        for (List<BaseArgument> args : argumentList) {
            if (args != null && !args.isEmpty()) return true;
        }
        return false;
    }

    public boolean hasRequiredArguments() {
        for (List<BaseArgument> list : argumentList) {
            if (list == null) continue;
            for (BaseArgument arg : list) {
                if (!arg.isOptional()) return true;
            }
        }
        return false;
    }

    public List<BaseArgument>[] getArgumentList() {
        return argumentList;
    }
}
