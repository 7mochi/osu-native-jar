# Detect platform (can be overridden with PLATFORM= env var for CI)
ifndef PLATFORM
ifeq ($(OS),Windows_NT)
    PLATFORM := win-x64
    LIB_EXT := dll
    LIB_NAME := osu.Native.dll
    LIB_DIR := windows-x86-64
else
    UNAME_S := $(shell uname -s)
    ifeq ($(UNAME_S),Linux)
        PLATFORM := linux-x64
        LIB_EXT := so
        LIB_NAME := osu.Native.so
        LIB_DIR := linux-x86-64
    endif
    ifeq ($(UNAME_S),Darwin)
        UNAME_M := $(shell uname -m)
        ifeq ($(UNAME_M),arm64)
            PLATFORM := osx-arm64
            LIB_DIR := darwin-aarch64
        else
            PLATFORM := osx-x64
            LIB_DIR := darwin-x86-64
        endif
        LIB_EXT := dylib
        LIB_NAME := osu.Native.dylib
    endif
endif
endif

BUILD_DIR := osu-native/Artifacts/bin/osu.Native/release_$(PLATFORM)
OUTPUT_DIR := build
NATIVE_RESOURCES_DIR := $(OUTPUT_DIR)/generated/resources/native/$(LIB_DIR)

.PHONY: all build-osu-native fix-cabinet-header extract-java copy-native-to-resources publish-to-maven-local clean

clean:
	rm -rf $(OUTPUT_DIR)
	./gradlew clean

build-osu-native:
	dotnet publish osu-native/osu.Native -c Release -r $(PLATFORM) -o $(OUTPUT_DIR)/generated
	cp $(BUILD_DIR)/cabinet.h $(OUTPUT_DIR)/generated/cabinet.h

fix-cabinet-header:
	python scripts/fix_cabinet_header.py $(OUTPUT_DIR)/generated/cabinet.h

extract-java:
	jextract -t io.github.nanamochi.osu_native.bindings \
	--output src/main/java \
	--use-system-load-library \
	$(OUTPUT_DIR)/generated/cabinet.h

copy-native-to-resources:
	mkdir -p $(NATIVE_RESOURCES_DIR)
ifeq ($(LIB_EXT),so)
	cp $(OUTPUT_DIR)/generated/$(LIB_NAME) $(NATIVE_RESOURCES_DIR)/lib$(LIB_NAME)
else ifeq ($(LIB_EXT),dylib)
	cp $(OUTPUT_DIR)/generated/$(LIB_NAME) $(NATIVE_RESOURCES_DIR)/lib$(LIB_NAME)
else
	cp $(OUTPUT_DIR)/generated/$(LIB_NAME) $(NATIVE_RESOURCES_DIR)/$(LIB_NAME)
endif

publish-to-maven-local:
	./gradlew publishToMavenLocal

all: build-osu-native fix-cabinet-header extract-java copy-native-to-resources publish-to-maven-local
