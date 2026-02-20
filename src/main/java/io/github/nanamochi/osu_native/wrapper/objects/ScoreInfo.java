package io.github.nanamochi.osu_native.wrapper.objects;

import io.github.nanamochi.osu_native.bindings.NativeScoreInfo;
import io.github.nanamochi.osu_native.wrapper.utils.NativeLoader;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ScoreInfo {
  static {
    NativeLoader.ensureLoaded();
  }

  @ToString.Exclude private MemorySegment rulesetHandle;
  @ToString.Exclude private MemorySegment beatmapHandle;
  @ToString.Exclude private MemorySegment modsHandle;

  private int maxCombo;
  private double accuracy;
  private int countMiss;
  private int countMeh;
  private int countOk;
  private int countGood;
  private int countGreat;
  private int countPerfect;
  private int countSmallTickMiss;
  private int countSmallTickHit;
  private int countLargeTickMiss;
  private int countLargeTickHit;
  private int countSliderTailHit;
  private Long legacyTotalScore;

  public MemorySegment convertToNative(Arena arena) {
    MemorySegment nativeScore = arena.allocate(NativeScoreInfo.layout());

    if (rulesetHandle != null) NativeScoreInfo.rulesetHandle(nativeScore, rulesetHandle);
    if (beatmapHandle != null) NativeScoreInfo.beatmapHandle(nativeScore, beatmapHandle);
    if (modsHandle != null) NativeScoreInfo.modsHandle(nativeScore, modsHandle);

    MemorySegment legacySeg =
        arena.allocate(io.github.nanamochi.osu_native.bindings.Cabinet__Nullable_int64_t.layout());
    if (legacyTotalScore != null) {
      io.github.nanamochi.osu_native.bindings.Cabinet__Nullable_int64_t.hasValue(legacySeg, true);
      io.github.nanamochi.osu_native.bindings.Cabinet__Nullable_int64_t.value(
          legacySeg, legacyTotalScore);
    } else {
      io.github.nanamochi.osu_native.bindings.Cabinet__Nullable_int64_t.hasValue(legacySeg, false);
    }
    NativeScoreInfo.legacyTotalScore(nativeScore, legacySeg);

    NativeScoreInfo.maxCombo(nativeScore, maxCombo);
    NativeScoreInfo.accuracy(nativeScore, accuracy);
    NativeScoreInfo.countMiss(nativeScore, countMiss);
    NativeScoreInfo.countMeh(nativeScore, countMeh);
    NativeScoreInfo.countOk(nativeScore, countOk);
    NativeScoreInfo.countGood(nativeScore, countGood);
    NativeScoreInfo.countGreat(nativeScore, countGreat);
    NativeScoreInfo.countPerfect(nativeScore, countPerfect);
    NativeScoreInfo.countSmallTickMiss(nativeScore, countSmallTickMiss);
    NativeScoreInfo.countSmallTickHit(nativeScore, countSmallTickHit);
    NativeScoreInfo.countLargeTickMiss(nativeScore, countLargeTickMiss);
    NativeScoreInfo.countLargeTickHit(nativeScore, countLargeTickHit);
    NativeScoreInfo.countSliderTailHit(nativeScore, countSliderTailHit);

    return nativeScore;
  }
}
