package io.github.nanamochi.osu_native.wrapper.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  BUFFER_SIZE_QUERY(-1),
  SUCCESS(0),
  OBJECT_NOT_RESOLVED(1),
  RULESET_UNAVAILABLE(2),
  UNEXPECTED_RULESET(3),
  FAILURE(127);

  private final int value;

  public static ErrorCode fromValue(int value) {
    for (ErrorCode code : values()) {
      if (code.value == value) {
        return code;
      }
    }
    throw new IllegalArgumentException("Unknown error code: " + value);
  }
}
