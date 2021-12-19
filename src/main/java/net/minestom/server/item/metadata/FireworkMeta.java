package net.minestom.server.item.metadata;

import net.minestom.server.item.ItemMeta;
import net.minestom.server.item.ItemMetaBuilder;
import net.minestom.server.item.firework.FireworkEffect;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTList;
import org.jglrxavpok.hephaistos.nbt.NBTType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class FireworkMeta extends ItemMeta implements ItemMetaBuilder.Provider<FireworkMeta.Builder> {

    private final List<FireworkEffect> effects;
    private final byte flightDuration;

    protected FireworkMeta(@NotNull ItemMetaBuilder metaBuilder, List<FireworkEffect> effects,
                           byte flightDuration) {
        super(metaBuilder);
        this.effects = List.copyOf(effects);
        this.flightDuration = flightDuration;
    }

    public List<FireworkEffect> getEffects() {
        return effects;
    }

    public byte getFlightDuration() {
        return flightDuration;
    }

    public static class Builder extends ItemMetaBuilder {

        private List<FireworkEffect> effects = new CopyOnWriteArrayList<>();
        private byte flightDuration;

        public Builder effects(List<FireworkEffect> effects) {
            this.effects = effects;
            handleCompound("Fireworks", nbtCompound -> {
                nbtCompound.set("Explosions", NBT.List(
                        NBTType.TAG_Compound,
                        effects.stream()
                                .map(FireworkEffect::asCompound)
                                .toList()
                ));
            });
            return this;
        }

        public Builder flightDuration(byte flightDuration) {
            this.flightDuration = flightDuration;
            handleCompound("Fireworks", nbtCompound ->
                    nbtCompound.setByte("Flight", this.flightDuration));
            return this;
        }

        @Override
        public @NotNull FireworkMeta build() {
            return new FireworkMeta(this, effects, flightDuration);
        }

        @Override
        public void read(@NotNull NBTCompound nbtCompound) {
            if (nbtCompound.containsKey("Fireworks")) {
                NBTCompound fireworksCompound = nbtCompound.getCompound("Fireworks");

                if (fireworksCompound.containsKey("Flight")) {
                    flightDuration(fireworksCompound.getAsByte("Flight"));
                }

                if (fireworksCompound.containsKey("Explosions")) {
                    NBTList<NBTCompound> explosions = fireworksCompound.getList("Explosions");

                    for (NBTCompound explosion : explosions) {
                        this.effects.add(FireworkEffect.fromCompound(explosion));
                    }
                    effects(effects);
                }
            }
        }

        @Override
        protected @NotNull Supplier<ItemMetaBuilder> getSupplier() {
            return Builder::new;
        }
    }
}