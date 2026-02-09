package io.github.nanamochi.osu_native.wrapper.calculators.performance;

import io.github.nanamochi.osu_native.wrapper.attributes.difficulty.DifficultyAttributes;
import io.github.nanamochi.osu_native.wrapper.attributes.performance.PerformanceAttributes;
import io.github.nanamochi.osu_native.wrapper.objects.Beatmap;
import io.github.nanamochi.osu_native.wrapper.objects.ModsCollection;
import io.github.nanamochi.osu_native.wrapper.objects.Ruleset;
import io.github.nanamochi.osu_native.wrapper.objects.ScoreInfo;
import io.github.nanamochi.osu_native.wrapper.utils.NativeLoader;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class PerformanceCalculator implements AutoCloseable {
  static {
    NativeLoader.ensureLoaded();
  }

  protected final MemorySegment handle;
  protected final Arena arena;
  protected volatile boolean closed = false;

  public abstract PerformanceAttributes calculate(
      Ruleset ruleset,
      Beatmap beatmap,
      ModsCollection mods,
      ScoreInfo scoreInfo,
      DifficultyAttributes difficultyAttributes);

  protected abstract void destroyNative(MemorySegment handle);

  @Override
  public void close() throws Exception {
    if (!closed) {
      destroyNative(handle);
      arena.close();
      closed = true;
    }
  }
}
