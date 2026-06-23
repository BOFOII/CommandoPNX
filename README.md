# CommandoPNX

Command framework for PowerNukkitX that simplifies command creation with argument validation and client-side argument listing.

A Java port of [CortexPE/Commando](https://github.com/CortexPE/Commando) (PocketMine-MP command framework).

## Installation (for other plugins)

### Via JitPack (recommended)

Add to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.BOFOII</groupId>
        <artifactId>CommandoPNX</artifactId>
        <version>v1.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Via GitHub Releases

Download the JAR from [Releases](https://github.com/BOFOII/CommandoPNX/releases) and add it to your project's classpath.

## Dependencies

- PowerNukkitX 2.0.0-SNAPSHOT (provided) — resolves from `maven.powernukkit.org`

## Package Structure

```
com.bofoiii.commando/
├── BaseCommand              Base class for commands
├── BaseSubCommand           Base class for subcommands
├── args/                    Argument types
│   ├── BaseArgument         Abstract base for all arguments
│   ├── RawStringArgument    Single-word string
│   ├── TextArgument         Multi-word text
│   ├── IntegerArgument      Integer numbers
│   ├── FloatArgument        Decimal numbers
│   ├── BooleanArgument      true/false
│   ├── StringEnumArgument   Custom enum values
│   ├── Vector3Argument      x y z coordinates
│   └── BlockPositionArgument  Integer block coordinates
├── constraint/              Execution constraints
│   ├── BaseConstraint       Abstract base
│   ├── ConsoleRequiredConstraint  Console only
│   └── InGameRequiredConstraint   In-game only
├── exception/               Custom exceptions
│   ├── CommandoException
│   ├── ArgumentOrderException
│   ├── HookAlreadyRegistered
│   └── InvalidErrorCode
└── store/                   State management
    └── SoftEnumStore        Dynamic enum values
```

## Usage

### 1. Basic Command

```java
public class HelloCommand extends BaseCommand {
    public HelloCommand(Plugin plugin) {
        super(plugin, "hello", "Say hello", new String[]{"hi"});
    }

    @Override
    public void prepare() {
        this.registerArgument(0, new RawStringArgument("name", true));
    }

    @Override
    public void onRun(CommandSender sender, String aliasUsed, Map<String, Object> args) {
        String name = (String) args.getOrDefault("name", "World");
        sender.sendMessage("Hello, " + name + "!");
    }
}
```

### 2. Command with SubCommand

```java
public class TeleportCommand extends BaseCommand {
    public TeleportCommand(Plugin plugin) {
        super(plugin, "tp", "Teleport command", new String[]{});
    }

    @Override
    public void prepare() {
        this.registerSubCommand(new TeleportPlayerSub());
        this.registerSubCommand(new TeleportCoordsSub());
    }

    @Override
    public void onRun(CommandSender sender, String aliasUsed, Map<String, Object> args) {
        sender.sendMessage("Usage: /tp <player|coords>");
    }
}
```

### 3. SubCommand

```java
public class TeleportPlayerSub extends BaseSubCommand {
    public TeleportPlayerSub() {
        super("player", "Teleport to player", new String[]{});
        this.setPermission("tp.player");
    }

    @Override
    public void prepare() {
        this.registerArgument(0, new RawStringArgument("target", false));
    }

    @Override
    public void onRun(CommandSender sender, String aliasUsed, Map<String, Object> args) {
        String target = (String) args.get("target");
        sender.sendMessage("Teleporting to " + target + "...");
    }
}
```

### 4. Argument Types

```java
// String
this.registerArgument(0, new RawStringArgument("name", false));

// Integer
this.registerArgument(1, new IntegerArgument("amount", true));

// Float
this.registerArgument(2, new FloatArgument("x", false));

// Boolean
this.registerArgument(3, new BooleanArgument("silent", true));

// Vector3 (x y z)
this.registerArgument(4, new Vector3Argument("position", false));

// BlockPosition (integer x y z)
this.registerArgument(5, new BlockPositionArgument("block", true));
```

### 5. Constraints

```java
// Console only
this.addConstraint(new ConsoleRequiredConstraint(this));

// In-game only
this.addConstraint(new InGameRequiredConstraint(this));
```

### 6. Register Command

```java
@Override
public void onEnable() {
    this.getServer().getCommandMap().register("myplugin", new HelloCommand(this));
}
```

## Build

```bash
# Build
mvn clean package

# Install to local Maven
mvn clean install
```

## Notes

- Ported from PHP virion [CortexPE/Commando](https://github.com/CortexPE/Commando)
- Argument parsing logic is inlined in BaseCommand and BaseSubCommand
- Supports optional arguments and automatic usage message generation
- Built-in error handling for invalid arguments
