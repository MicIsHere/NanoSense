package dev.supermic.nanosense.mixins.core.tileentity;

import dev.supermic.nanosense.util.interfaces.ITypeID;
import dev.supermic.nanosense.util.ClassIDRegistry;
import dev.supermic.nanosense.util.interfaces.ITypeID;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TileEntity.class)
public class MixinTileEntity implements ITypeID {
    private static final ClassIDRegistry<TileEntity> registry = new ClassIDRegistry<>();
    private final int typeID = registry.get(((TileEntity) ((Object) this)).getClass());

    @Override
    public int getTypeID() {
        return typeID;
    }
}
