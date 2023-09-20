# Minecraft GUI Library
[![](https://jitpack.io/v/cjcameron92/MenuApi.svg)](https://jitpack.io/#cjcameron92/MenuApi)

## Introduction

The Minecraft GUI Library is a powerful tool for creating graphical user interfaces (GUIs) in Minecraft plugins. Whether you're a seasoned developer or just starting with plugin development, this library provides you with the means to create professional-grade UIs for your Minecraft server.

## Getting Started

### Gradle

To include the Minecraft GUI Library in your Gradle-based project, add the following repository and dependency to your `build.gradle.kts` file:

```kts
repositories {
    maven("https://jitpack.com")
}

dependencies {
    implementation("com.cjcameron92:menu:${VERSION}")
}
```

### Maven

For Maven-based projects, add the following repository and dependency to your pom.xml:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.cjcameron92</groupId>
    <artifactId>MenuApi</artifactId>
    <version>Tag</version>
</dependency>
```

Replace `${VERSION}` with the desired version of the library.

## Functional Usage

The Minecraft GUI Library allows you to create functional GUIs with ease. Here's an example of how to use it in your code:

```java
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJumpEvent;

public class JumpListener implements Listener {

    private final Plugin plugin;

    public JumpListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        Menu menu = MenuBuilder.newBuilder()
                .shape("####@####")
                .add('#', new ItemStack(Material.PUMPKIN, 1), e -> e.getWhoClicked().sendMessage("Hello!"))
                .add('@', new ItemStack(Material.CACTUS, 1))
                .register(event.getPlayer(), "<gray>Title", plugin);

        // Open the menu
        menu.fire();
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        Menu menu = MenuBuilder.newBuilder()
                .add(4, new ItemStack(Material.PUMPKIN, 1), e -> e.getWhoClicked().sendMessage("Hello!"))
                .add(5, new ItemStack(Material.CACTUS))
                .register(event.getPlayer(), "<gray>Title", plugin);

        // Open the menu
        menu.fire();
    }

}
```

## Non-Functional Usage

The library also supports non-functional GUIs. Here's an example:

```java
import org.bukkit.Material;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class JumpMenu extends Menu {

    public JumpMenu(Plugin plugin, Player player) {
        super(plugin, player, new String[] { "#########" }, MiniMessage.miniMessage().deserialize("<gray>Example"), null);
    }

    @Override
    public void redraw() {
        setItem('#', new ItemStack(Material.CACTUS));
    }
}

// Create and open the menu
final Menu menu = new JumpMenu(plugin, player);
menu.fire();
```


