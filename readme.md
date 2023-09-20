## Minecraft GUI Library

Implementation


Gradle

Repository

```kts
maven("https://jitpack.com")
```

Dependency
```kts
implementation("com.cjcameron92:menu:${VERSION}"
```

Maven

Repository
```xml
<repository>
    <url>https://jitpack.com</url>
</repository>
```

Dependency
```xml
<dependency>
    <groupId>com.cjcameron92</groupId>
    <artifactId>menu</artifactId>
    <version>LATEST</version>
    <scope>provided</scope>
</dependency>
```

## Functional
```java
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

        // open menu
        menu.fire();
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        Menu menu = MenuBuilder.newBuilder()
                .add(4, new ItemStack(Material.PUMPKIN, 1), e -> e.getWhoClicked().sendMessage("Hello!"))
                .add(5, new ItemStack(Material.CACTUS))
                .register(event.getPlayer(), "<gray>Title", plugin);

        // open menu
        menu.fire();
    }

}
```

## Non-Functional 
```java
public class JumpMenu extends Menu {

    public JumpMenu(Plugin plugin, Player player) {
        super(plugin, player, new String[] { "#########" }, MiniMessage.miniMessage().deserialize("<gray>Example"), null);
    }

    @Override
    public void redraw() {
        setItem('#', new ItemStack(Material.CACTUS));
    }
}


final Menu menu = new JumpMenu(plugin, player);
menu.fire();
```