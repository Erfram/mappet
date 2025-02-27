package mchorse.mappet.network.client.scripts;

import mchorse.chameleon.animation.ActionConfig;
import mchorse.chameleon.animation.Animator;
import mchorse.chameleon.metamorph.ChameleonMorph;
import mchorse.mappet.network.common.scripts.PacketPlayAnimation;
import mchorse.mappet.utils.ReflectionUtils;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.Morphing;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class ClientHandlerPlayAnimation extends ClientMessageHandler<PacketPlayAnimation> {

    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketPlayAnimation message) {
        player.world.getLoadedEntityList().stream().filter(entity -> entity.getUniqueID().equals(UUID.fromString(message.uuid))).forEach(entity -> {
            AbstractMorph morph = getMorph(entity);

            if (!(morph instanceof ChameleonMorph)) {
                return;
            }
            ChameleonMorph chameleonMorph = (ChameleonMorph)morph;
            Animator animator = (Animator) ReflectionUtils.getAndInvokeMethod(ChameleonMorph.class, "getAnimator", chameleonMorph);
            ActionConfig config = chameleonMorph.actions.getConfig(message.animation);
            animator.addAction(animator.createAction(animator.animation, config, false));
        });
    }

    public AbstractMorph getMorph(Entity entity) {
        if (entity instanceof IMorphProvider) {
            return ((IMorphProvider)entity).getMorph();
        } else if (entity instanceof EntityPlayer) {
            return Morphing.get((EntityPlayer) entity).getCurrentMorph();
        } else {
            return null;
        }
    }
}