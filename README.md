# FIF Kotlin Decoder
![Build](https://github.com/fifoc/kotlin-decoder/workflows/Build/badge.svg) \
Decodes fif images into png

## Using

First grab one of the [releases](https://github.com/fifoc/kotlin-decoder/releases) or [build one yourself](#Building)

Run `java -jar kotlin-decoder.jar INPUT OUTPUT --option value`

Options: \
`--frame-mode` enables frame mode, decodes fif as a sequence of frames and treats `OUTPUT` as a folder instead \
`--zero-padding` sets amount of zeros to pad to frame sequence names. Default is 4

## Building

Building requires jdk 8 or higher installed on the machine

Build using `./gradlew build` \
Then grab the built jar from `build/libs/kotlin-decoder.jar`

## License

This project is licensed under the AGPL-3.0 License - see the [LICENSE](LICENSE) file for details