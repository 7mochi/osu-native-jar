package io.github.nanamochi.osu_native.wrapper.objects;

import static io.github.nanamochi.osu_native.bindings.cabinet_h.Ruleset_CreateFromId;
import static io.github.nanamochi.osu_native.bindings.cabinet_h.Ruleset_Destroy;

import io.github.nanamochi.osu_native.bindings.NativeRuleset;
import io.github.nanamochi.osu_native.wrapper.utils.NativeLoader;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.Map;
import lombok.Getter;

public class Ruleset implements AutoCloseable {
  static {
    NativeLoader.ensureLoaded();
  }

  @Getter private final MemorySegment handle;
  private final Arena arena;
  @Getter private volatile boolean closed = false;

  private static final Map<Integer, String> RULESET_SHORT_NAME_BY_ID =
      Map.of(
          0, "osu",
          1, "taiko",
          2, "catch",
          3, "mania");

  private Ruleset(MemorySegment handle, Arena arena) {
    this.handle = handle;
    this.arena = arena;
  }

  public static Ruleset fromId(int id) {
    Arena arena = Arena.ofConfined();
    MemorySegment nativeRuleset = arena.allocate(NativeRuleset.layout());
    int result = Ruleset_CreateFromId(id, nativeRuleset);
    if (ErrorCode.fromValue(result) != ErrorCode.SUCCESS) {
      arena.close();
      throw new RuntimeException(
          "Failed to create ruleset from ID. Error: " + ErrorCode.fromValue(result));
    }
    return new Ruleset(nativeRuleset, arena);
  }

  public int getRulesetId() {
    return NativeRuleset.rulesetId(this.handle);
  }

  public String getShortName() {
    return RULESET_SHORT_NAME_BY_ID.get(getRulesetId());
  }

  @Override
  public String toString() {
    return "Ruleset{"
        + "rulesetId="
        + getRulesetId()
        + ", shortName='"
        + getShortName()
        + '\''
        + '}';
  }

  @Override
  public void close() {
    if (!closed) {
      Ruleset_Destroy(handle);
      arena.close();
      closed = true;
    }
  }
}
