package io.github.nanamochi.osu_native.wrapper.calculators.difficulty;

import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.DifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.objects.ModsCollection;
import io.github.nanamochi.osu_native.wrapper.utils.NativeLoader;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class DifficultyCalculator<T extends DifficultyAttributes>
    implements AutoCloseable {
  static {
    NativeLoader.ensureLoaded();
  }

  protected final MemorySegment handle;
  protected final Arena arena;
  protected volatile boolean closed = false;

  public abstract T calculate(ModsCollection mods);

  protected abstract void destroyNative(MemorySegment handle);

  @Override
  public void close() {
    if (!closed) {
      destroyNative(handle);
      arena.close();
      closed = true;
    }
  }
}
