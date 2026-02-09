package io.github.nanamochi.osu_native.wrapper.objects;

import static io.github.nanamochi.osu_native.bindings.cabinet_h.ModsCollection_Add;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.ModsCollection_Create;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.ModsCollection_Debug;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.ModsCollection_Destroy;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.ModsCollection_Remove;

import io.github.nanamochi.osu_native.bindings.*;
import io.github.nanamochi.osu_native.wrapper.utils.NativeLoader;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class ModsCollection implements AutoCloseable {
  static {
    NativeLoader.ensureLoaded();
  }

  @Getter private final MemorySegment handle;
  private final Arena arena;
  private final List<Mod> mods;
  @Getter private volatile boolean closed = false;

  private ModsCollection(MemorySegment handle, Arena arena) {
    this.handle = handle;
    this.arena = arena;
    mods = new ArrayList<>();
  }

  public static ModsCollection create() {
    Arena arena = Arena.ofConfined();

    MemorySegment nativeModsCollection = arena.allocate(NativeModsCollection.layout());
    int result = ModsCollection_Create(nativeModsCollection);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      arena.close();
      throw new RuntimeException(
          "Failed to create ModsCollection. Error: " + ErrorCode.fromValue(result));
    }

    return new ModsCollection(nativeModsCollection, arena);
  }

  public void add(Mod mod) {
    int result = ModsCollection_Add(this.handle, mod.getHandle());
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      throw new RuntimeException(
          "Failed to set integer mod setting: " + ErrorCode.fromValue(result));
    }
    mods.add(mod);
  }

  public boolean has(Mod mod) {
    return mods.contains(mod);
  }

  public void remove(Mod mod) {
    int result = ModsCollection_Remove(this.handle, mod.getHandle());
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      throw new RuntimeException(
          "Failed to set integer mod setting: " + ErrorCode.fromValue(result));
    }
    mods.remove(mod);
  }

  public void debug() {
    int result = ModsCollection_Debug(this.handle);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      System.out.println("Debug failed: " + ErrorCode.fromValue(result));
    }
  }

  @Override
  public void close() {
    if (!closed) {
      for (Mod mod : mods) {
        try {
          mod.close();
        } catch (Exception ignored) {
        }
      }
      ModsCollection_Destroy(handle);
      arena.close();
      closed = true;
    }
  }
}
