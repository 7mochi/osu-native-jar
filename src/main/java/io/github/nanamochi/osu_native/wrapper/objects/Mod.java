package io.github.nanamochi.osu_native.wrapper.objects;

import static io.github.nanamochi.osu_native.bindings.cabinet_h.Mod_Create;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.Mod_Destroy;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.Mod_SetSettingBool;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.Mod_SetSettingFloat;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.Mod_SetSettingInteger;

import io.github.nanamochi.osu_native.bindings.NativeMod;
import io.github.nanamochi.osu_native.wrapper.utils.NativeLoader;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Mod implements AutoCloseable {
  static {
    NativeLoader.ensureLoaded();
  }

  @Getter private final MemorySegment handle;
  private final Arena arena;
  @Getter private volatile boolean closed = false;

  public static Mod create(String acronym) {
    Arena arena = Arena.ofConfined();

    MemorySegment nativeAcronym = arena.allocateFrom(acronym);
    MemorySegment nativeMod = arena.allocate(NativeMod.layout());
    int result = Mod_Create(nativeAcronym, nativeMod);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      arena.close();
      throw new RuntimeException("Failed to create mod. Error: " + ErrorCode.fromValue(result));
    }

    return new Mod(nativeMod, arena);
  }

  public void setSettingBoolean(String key, boolean value) {
    MemorySegment nativeKey = arena.allocateFrom(key);
    int result = Mod_SetSettingBool(handle, nativeKey, value);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      throw new RuntimeException(
          "Failed to set boolean mod setting: " + ErrorCode.fromValue(result));
    }
  }

  public void setSettingsInteger(String key, int value) {
    MemorySegment nativeKey = arena.allocateFrom(key);
    int result = Mod_SetSettingInteger(handle, nativeKey, value);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      throw new RuntimeException(
          "Failed to set integer mod setting: " + ErrorCode.fromValue(result));
    }
  }

  public void setSettingsFloat(String key, float value) {
    MemorySegment nativeKey = arena.allocateFrom(key);
    int result = Mod_SetSettingFloat(handle, nativeKey, value);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      throw new RuntimeException("Failed to set float mod setting: " + ErrorCode.fromValue(result));
    }
  }

  @Override
  public void close() {
    if (!closed) {
      Mod_Destroy(handle);
      arena.close();
      closed = true;
    }
  }
}
