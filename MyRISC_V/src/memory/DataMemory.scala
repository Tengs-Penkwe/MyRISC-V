package MyRISC_V

import chisel3._
import chisel3.util.{log2Ceil}

class MemoryPort(val bitWidth: Int, val memorySize: Long) extends Bundle {
    val readAddr = Input(UInt(log2Ceil(memorySize).W))
    val readData = Output(UInt(bitWidth.W))

    val writeAddr = Input(UInt(log2Ceil(memorySize).W))
    val writeData = Input(UInt(bitWidth.W))
    val writeMask = Input(UInt((bitWidth/8).W))

    val writeEnable = Input(Bool())
}

class DataMemory(
    bitWidth: Int = 64,
    memorySize: Long = 1 * 1024,
    memoryFile: String = "",
    debug: Boolean = false,
) extends Module {
    val totalWords = memorySize / bitWidth;

    val io = IO(new Bundle {
        val memoryPort = new MemoryPort(bitWidth, memorySize)
    });

    if (debug) {
        println(s"""
            Read Sync Memory: 
            |    Words: ${totalWords}
            |    Size: ${totalWords * bitWidth} bytes
            |    Bit Width: ${bitWidth}
            |    Address Width: ${io.memoryPort.readAddr.getWidth} bits
            |    Load Memory File: ${memoryFile}
            """.stripMargin
        );
    }

    val mem = SyncReadMem(totalWords, UInt(bitWidth.W));

    io.memoryPort.readData := mem.read(io.memoryPort.readAddr);
    when (io.memoryPort.writeEnable) {
        mem.write(io.memoryPort.writeAddr, io.memoryPort.writeData);
    }

}
