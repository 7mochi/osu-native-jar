package io.github.nanamochi.osu_native.wrapper.objects;

import static io.github.nanamochi.osu_native.bindings.cabinet_h.Beatmap_CreateFromFile;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.Beatmap_Destroy;

import io.github.nanamochi.osu_native.bindings.NativeBeatmap;
import io.github.nanamochi.osu_native.bindings.cabinet_h;
import io.github.nanamochi.osu_native.wrapper.utils.NativeHelper;
import io.github.nanamochi.osu_native.wrapper.utils.NativeLoader;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
public class Beatmap implements AutoCloseable {
  static {
    NativeLoader.ensureLoaded();
  }

  @ToString.Exclude @Getter private final MemorySegment handle;
  @ToString.Exclude private final Arena arena;
  @ToString.Exclude @Getter private volatile boolean closed = false;

  public static Beatmap fromFile(String path) {
    Arena arena = Arena.ofConfined();

    MemorySegment nativeString = arena.allocateFrom(path);
    MemorySegment nativeBeatmap = arena.allocate(NativeBeatmap.layout());
    int result = Beatmap_CreateFromFile(nativeString, nativeBeatmap);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      arena.close();
      throw new RuntimeException(
          "Failed to create beatmap from file. Error: " + ErrorCode.fromValue(result));
    }

    return new Beatmap(nativeBeatmap, arena);
  }

  public String getTitle() {
    return NativeHelper.getString(this.handle, cabinet_h::Beatmap_GetTitle);
  }

  public String getArtist() {
    return NativeHelper.getString(this.handle, cabinet_h::Beatmap_GetArtist);
  }

  public String getVersion() {
    return NativeHelper.getString(this.handle, cabinet_h::Beatmap_GetVersion);
  }

  public float getApproachRate() {
    return NativeBeatmap.approachRate(this.handle);
  }

  public float getDrainRate() {
    return NativeBeatmap.drainRate(this.handle);
  }

  public float getOverallDifficulty() {
    return NativeBeatmap.overallDifficulty(this.handle);
  }

  public double getCircleSize() {
    return NativeBeatmap.circleSize(this.handle);
  }

  public double getSliderMultiplier() {
    return NativeBeatmap.sliderMultiplier(this.handle);
  }

  public double getSliderTickRate() {
    return NativeBeatmap.sliderTickRate(this.handle);
  }

  @Override
  public void close() {
    if (!closed) {
      Beatmap_Destroy(handle);
      arena.close();
      closed = true;
    }
  }
}
