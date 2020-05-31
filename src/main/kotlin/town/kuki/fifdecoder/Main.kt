package town.kuki.fifdecoder

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

var frameMode = false
var zeroPadding = 4

fun main(argArray: Array<String>) {
    val args = argArray.toList()
    //I should implement a better argument parser
    assert(args.size>1)
    frameMode = args.contains("--frame-mode")
    if(args.contains("--zero-padding")) zeroPadding = args.get(args.indexOf("--zero-padding")+1).toInt()
    fif2png(File(args[0]), File(args[1]))
}

fun fif2png(input: File, output: File) {
    if(frameMode) output.mkdirs()
    var frame = 0
    val bin = input.inputStream()
    if(bin.read(ByteArray(6)).toString() == "FastIF") throw IllegalArgumentException("Not an FastIF file")
    val width = bin.read()*2
    val height = bin.read()*4
    val render = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    var bg = Color.WHITE
    var fg = Color.BLACK
    loop@while (bin.available()>0) {
        when(bin.read()) {
            0x01 -> {
                bg = Color(bin.read(), bin.read(), bin.read())
            }
            0x02 -> {
                fg = Color(bin.read(), bin.read(), bin.read())
            }
            0x10 -> {
                val x = bin.read()
                val y = bin.read()
                val length = bin.read()
                for(i in x until x+length) {
                    render.setBraille(i*2, y*4, bin.read(), bg, fg)
                }
            }
            0x11 -> {
                val x = bin.read()
                val y = bin.read()
                val width = bin.read()
                val height = bin.read()
                val char = bin.read()
                for(i in x until x+width) {
                    for(ii in y until y+height) {
                        render.setBraille(i*2, ii*4, char, bg, fg)
                    }
                }
            }
            0x12 -> {
                bin.skip(1)
                if(frameMode) {
                    ImageIO.write(render, "png", output.getSubFile("${frame.toString().padStart(zeroPadding, '0')}.png"))
                    frame++
                }
            }
            0x13 -> {
                val x = bin.read()
                val y = bin.read()
                val length = bin.read()
                for(i in y until y+length) {
                    render.setBraille(x*2, i*4, bin.read(), bg, fg)
                }
            }
            0x20 -> {
                break@loop
            }
        }
    }
    if(!frameMode) {
        ImageIO.write(render, "png", output)
    } else {
        ImageIO.write(render, "png", output.getSubFile("${frame.toString().padStart(zeroPadding, '0')}.png"))
    }
}

fun BufferedImage.setBraille(x: Int, y: Int, braille: Int, bg: Color, fg: Color) {
    this.setRGB(x, y, if(braille shr 0 and 0x1 == 1) fg.rgb else bg.rgb)
    this.setRGB(x, y+1, if(braille shr 1 and 0x1 == 1) fg.rgb else bg.rgb)
    this.setRGB(x, y+2, if(braille shr 2 and 0x1 == 1) fg.rgb else bg.rgb)
    this.setRGB(x+1, y, if(braille shr 3 and 0x1 == 1) fg.rgb else bg.rgb)
    this.setRGB(x+1, y+1, if(braille shr 4 and 0x1 == 1) fg.rgb else bg.rgb)
    this.setRGB(x+1, y+2, if(braille shr 5 and 0x1 == 1) fg.rgb else bg.rgb)
    this.setRGB(x, y+3, if(braille shr 6 and 0x1 == 1) fg.rgb else bg.rgb)
    this.setRGB(x+1, y+3, if(braille shr 7 and 0x1 == 1) fg.rgb else bg.rgb)
}

fun File.getSubFile(name: String): File {
    return File(this.absolutePath.plus("/$name"))
}