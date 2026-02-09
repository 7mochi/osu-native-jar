BUILD_DIR := osu-native/Artifacts/bin/osu.Native/release_linux-x64
OUTPUT_DIR := build

clean:
	rm -rf $(OUTPUT_DIR)
	./gradlew clean

build-osu-native:
	dotnet publish osu-native/osu.Native -c Release -r linux-x64 -o $(OUTPUT_DIR)/generated
	cp $(BUILD_DIR)/cabinet.h $(OUTPUT_DIR)/generated/cabinet.h

fix-cabinet-header:
	python scripts/fix_cabinet_header.py

extract-java:
	jextract -t io.github.nanamochi.osu_native.bindings \
	--output src/main/java \
	$(OUTPUT_DIR)/generated/cabinet.h

copy-native-to-resources:
	mkdir -p build/generated/resources/native
	cp $(OUTPUT_DIR)/generated/osu.Native.so build/generated/resources/native/libosu.Native.so

publish-to-maven-local:
	./gradlew publishToMavenLocal

all: build-osu-native fix-cabinet-header extract-java copy-native-to-resources publish-to-maven-local
