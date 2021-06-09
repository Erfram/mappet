package mchorse.mappet.commands.events;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.events.nodes.EventBaseNode;
import mchorse.mappet.api.utils.nodes.NodeSystem;
import mchorse.mappet.commands.MappetCommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public abstract class CommandEventBase extends MappetCommandBase
{
    protected NodeSystem<EventBaseNode> getEvent(String id) throws CommandException
    {
        NodeSystem<EventBaseNode> event = Mappet.events.load(id);

        if (event == null)
        {
            throw new CommandException("event.missing", id);
        }

        return event;
    }

    @Override
    public int getRequiredArgs()
    {
        return 2;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, Mappet.events.getKeys());
        }

        return super.getTabCompletions(server, sender, args, targetPos);
    }
}