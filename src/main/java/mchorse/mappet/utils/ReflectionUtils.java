package mchorse.mappet.utils;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class ReflectionUtils
{
    public static Set<TileEntity> getGlobalTiles(RenderGlobal global)
    {
        return ObfuscationReflectionHelper.<Set<TileEntity>, RenderGlobal>getPrivateValue(RenderGlobal.class, global, "setTileEntities", "field_181024_n");
    }

    public static Method getMethod(Class<?> clazz, String methodName, Object... args) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, Arrays.stream(args).map(Object::getClass).toArray(Class[]::new));
            method.setAccessible(true);
            return method;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Method getMethod(Class<?> clazz, String methodName) {
        return getMethod(clazz, methodName, new Object[0]);
    }

    public static Object getAndInvokeMethod(Class<?> clazz, String methodName, Object target, Object... args) {
        try {
            Method method = getMethod(clazz, methodName, args);
            return method.invoke(target, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getAndInvokeMethod(Class<?> clazz, String methodName, Object target) {
        return getAndInvokeMethod(clazz, methodName, target, new Object[0]);
    }
}