package ca.bkaw.praeter.framework.plugin.test;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestingCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            return false;
        }
        switch (args[0]) {
            case "gui" -> gui(sender);
        }

        return true;
    }

    public void gui(@NotNull CommandSender sender) {
        if (sender instanceof Player player) {
            TestGui gui = new TestGui(new TestCustomGuiRenderer());
            if (Math.random() > 0.5) {
                gui.toggleButton2();
                player.sendRawMessage("Called toggleButton2");
            }
            player.sendRawMessage("Opening gui");
            gui.show(player);
        }
    }
}
